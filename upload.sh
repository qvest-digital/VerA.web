#!/usr/bin/env mksh
# -*- mode: sh -*-
#-
# Copyright © 2018, 2019
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
	print -ru2 -- "[INFO] -f = tail -F the tomcat logs after running, press ^C to abort"
	print -ru2 -- "[INFO] -n = do not upload Online-Anmeldung"
	exit ${1:-1}
}

has_vwoa=1
tomcat=8
tailf=0
while getopts "fhn" ch; do
	case $ch {
	(f) tailf=1 ;;
	(+f) tailf=0 ;;
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
(( has_vwoa )) || hostoa=

if (( tailf )); then
	tcsleep=2
	oasleep=1
else
	tcsleep=5
	oasleep=5
	test -n "$hostoa" || tcsleep=10
fi

ssh root@$hostbase "
	PS4='(${hostbase%%.*})++++ '
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
scp core/target/veraweb.war vwor/target/vwor.war \
    root@$hostbase:/var/lib/tomcat$tomcat/webapps/
ssh root@$hostbase "
	PS4='(${hostbase%%.*})++++ '
	set -ex
	service tomcat$tomcat start
    "
sleep $tcsleep
test -z "$hostoa" || ssh root@$hostoa "
	PS4='(${hostoa%%.*})++++ '
	set -ex
	cd /service/vwoa
	rm -f vwoa.jar
	mv vw-online-registration.jar vwoa.jar
	svc -t /service/vwoa
	sleep $oasleep
	svstat /service/vwoa
    "
set +x
if (( tailf )); then
	trap : INT
	set +e
	ssh root@$hostbase "
		PS4='(${hostbase%%.*})++++ '
		set -x
		exec tail -F /var/log/tomcat$tomcat/catalina.out
	    "
	print -u2
fi
print -ru2 -- "[INFO] installing to stage $stage finished successfully"
