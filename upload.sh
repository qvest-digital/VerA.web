#!/usr/bin/env mksh
# -*- mode: sh -*-
#-
# Copyright © 2018
#	mirabilos <t.glaser@tarent.de>
#
# Provided that these terms and disclaimer and all copyright notices
# are retained or reproduced in an accompanying document, permission
# is granted to deal in this work without restriction, including un‐
# limited rights to use, publicly perform, distribute, sell, modify,
# merge, give away, or sublicence.
#
# This work is provided “AS IS” and WITHOUT WARRANTY of any kind, to
# the utmost extent permitted by applicable law, neither express nor
# implied; without malicious intent or gross negligence. In no event
# may a licensor, author or contributor be held liable for indirect,
# direct, other damage, loss, or other issues arising in any way out
# of dealing in the work, even if advised of the possibility of such
# damage or existence of a defect, except proven that it results out
# of said person’s immediate fault when using the work as intended.

# initialisation
LC_ALL=C; export LC_ALL
unset LANGUAGE
hn=$(hostname)
PS4="(${hn%%.*})+++ "
set -e
set -o pipefail
set -x
cd "$(dirname "$0")"

usage() {
	set +x
	print -ru2 -- "[ERROR] usage: $0 [-n] <stage>"
	print -ru2 -- "[INFO] -n = do not upload Online-Anmeldung"
	exit ${1:-1}
}

has_vwoa=1
split_oa=0
tomcat=8

while getopts "hn" ch; do
	case $ch {
	(h) usage 0 ;;
	(n) has_vwoa=0 ;;
	(+n) has_vwoa=1 ;;
	(*) usage ;;
	}
done
shift $(($OPTIND - 1))

function tgtck {
	stage=$1

	case $stage {
	(mit)
		hostbase=veraweb-mit.lan.tarent.de
		hostoa=veraweb-mit-oa.lan.tarent.de
		;;
	(ohne)
		hostbase=veraweb-ohne.lan.tarent.de
		hostoa=
		;;
	(mit/ohne)
		split_oa=1
		hostbase=veraweb-mit.lan.tarent.de
		hostoa=veraweb-mit-oa.lan.tarent.de
		hostohne=veraweb-ohne.lan.tarent.de
		;;
	(po)
		hostbase=veraweb-po.lan.tarent.de
		hostoa=$hostbase
		;;
	(thense)
		hostbase=veraweb-thense.lan.tarent.de
		hostoa=$hostbase
		;;
	(stadtmuenchen)
		hostbase=veraweb-stadtmuenchen.lan.tarent.de
		hostoa=
		tomcat=7
		;;
	(*)
		set +x
		local stages=$(typeset -f tgtck | sed -n \
		    -e '/([*])$/,$d' -e '/^	*(\(.*\))$/s//\1/p')
		print -ru2 -- "[ERROR] stage '$stage' unknown"
		print -ru2 -- "[INFO] known stages: ${stages//$'\n'/, }"
		usage
		;;
	}
}
tgtck "$1"

[[ -n $hostoa ]] || has_vwoa=0

if (( has_vwoa )) && [[ ! -s vwoa/target/vw-online-registration.jar ]]; then
	print -ru2 -- '[WARNING] Online-Anmeldung not built, not uploading it'
	has_vwoa=0
fi

# glue for the following old code
(( split_oa )) && dontsplitoa=false || dontsplitoa=true
(( has_vwoa )) || hostoa=

ssh root@$hostbase "
	PS4='(${hostbase%%.*})++++ '
	set -ex
	(service tomcat$tomcat stop || :)
    "
$dontsplitoa || ssh root@$hostohne "
	PS4='(${hostohne%%.*})++++ '
	set -ex
	(service tomcat$tomcat stop || :)
    "
test -z "$hostoa" || \
    scp vwoa/target/vw-online-registration.jar root@$hostoa:/service/vwoa/
sleep 5
ssh root@$hostbase "
	PS4='(${hostbase%%.*})++++ '
	set -ex
	psql -U veraweb -h 127.0.0.1 veraweb
	rm -rf /var/lib/tomcat$tomcat/webapps/v*
    " <core/src/main/files/upgrade.sql
$dontsplitoa || ssh root@$hostohne "
	PS4='(${hostohne%%.*})++++ '
	set -ex
	psql -U veraweb -h 127.0.0.1 veraweb
	rm -rf /var/lib/tomcat$tomcat/webapps/v*
    " <core/src/main/files/upgrade.sql
scp core/target/veraweb.war vwor/target/vwor.war \
    root@$hostbase:/var/lib/tomcat$tomcat/webapps/
$dontsplitoa || scp core/target/veraweb.war vwor/target/vwor.war \
    root@$hostohne:/var/lib/tomcat$tomcat/webapps/
ssh root@$hostbase "
	PS4='(${hostbase%%.*})++++ '
	set -ex
	service tomcat$tomcat start
    "
$dontsplitoa || ssh root@$hostohne "
	PS4='(${hostohne%%.*})++++ '
	set -ex
	service tomcat$tomcat start
    "
sleep 5
test -z "$hostoa" && sleep 5 || ssh root@$hostoa "
	PS4='(${hostoa%%.*})++++ '
	set -ex
	cd /service/vwoa
	rm -f vwoa.jar
	mv vw-online-registration.jar vwoa.jar
	svc -t /service/vwoa
	sleep 5
	svstat /service/vwoa
    "
set +x
print -ru2 -- "[INFO] installing to stage $stage finished successfully"
