#!/usr/bin/env mksh
# -*- mode: sh -*-
#-
# Copyright © 2016, 2017, 2018, 2019
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
#-
# Script to check dependencies

# initialisation
export LC_ALL=C
unset LANGUAGE
PS4='++ '
set -e
set -o pipefail
cd "$(dirname "$0")"
saveIFS=$' \t\n'

x=$(sed --posix 's/u\+/x/g' <<<'fubar fuu' 2>&1) && alias 'sed=sed --posix'
x=$(sed -e 's/u\+/x/g' -e 's/u/X/' <<<'fubar fuu' 2>&1)
case $?:$x {
(0:fXbar\ fuu) ;;
(*)
	print -ru2 -- '[ERROR] your sed is not POSIX compliant'
	exit 1 ;;
}

# get project metadata
pompath=..
<"$pompath/pom.xml" xmlstarlet sel \
    -N pom=http://maven.apache.org/POM/4.0.0 -T -t \
    -c /pom:project/pom:groupId -n \
    -c /pom:project/pom:artifactId -n \
    -c /pom:project/pom:version -n \
    |&
IFS= read -pr pgID
IFS= read -pr paID
IFS= read -pr pVSN
# check old file is sorted
sort -uo ckdep.tmp ckdep.lst
if cmp -s ckdep.lst ckdep.tmp; then
	abend=0
else
	print -ru2 -- '[WARNING] list of dependencies was not sorted!'
	cat ckdep.tmp >ckdep.lst
	abend=1
fi
# analyse Maven dependencies
(cd .. && mvn -B dependency:list) 2>&1 | \
    tee /dev/stderr | sed -n \
    -e 's/ -- module .*$//' \
    -e '/^\[INFO]    org.evolvis.veraweb:/d' \
    -e '/^\[INFO]    org.evolvis.veraweb.middleware:/d' \
    -e '/^\[INFO]    \([^:]*\):\([^:]*\):jar:\([^:]*\):\([^:]*\)$/s//\1:\2 \3 \4 ok/p' \
    >ckdep.tmp
while IFS=' ' read ga v scope rest; do
	[[ $scope != @(compile|runtime) ]] || print -r -- ${ga/:/ } $v
done <ckdep.tmp | sort -u >ckdep.mvn.tmp
# deal with embedded copies
function dopom {
	cat >ckdep.pom <<-EOF
	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
		<modelVersion>4.0.0</modelVersion>
		<parent>
			<groupId>$pgID</groupId>
			<artifactId>$paID</artifactId>
			<version>$pVSN</version>
			<relativePath>$pompath/</relativePath>
		</parent>
		<artifactId>embedded-code-copy-insert-$1</artifactId>
		<packaging>jar</packaging>
		<dependencies>
	EOF
	shift
	set -o noglob
	for gav in "$@"; do
		IFS=:
		set -- $gav
		IFS=$saveIFS
		cat <<-EOF
			<dependency>
				<groupId>$1</groupId>
				<artifactId>$2</artifactId>
				<version>$3</version>
			</dependency>
		EOF
	done >>ckdep.pom
	set +o noglob
	cat >>ckdep.pom <<-EOF
		</dependencies>
	</project>
	EOF
	mvn -B -f ckdep.pom -Dwithout-implicit-dependencies dependency:list 2>&1 | \
	    tee /dev/stderr | sed -n \
	    -e 's/ -- module .*$//' \
	    -e '/^\[INFO]    \([^:]*\):\([^:]*\):jar:\([^:]*\):\([^:]*\)$/s//\1:\2 \3 \4 ok/p' \
	    >>ckdep.pom.tmp
}
if [[ -s ckdep.ins ]]; then
	set -A cc_found
	set -A cc_where
	set -A cc_which
	ncc=0
	while read first rest; do
		[[ $first = ?('#') ]] && continue
		cc_where[ncc]=$first
		cc_which[ncc++]=$rest
	done <ckdep.ins
	while IFS=' ' read g a v; do
		first=$g:$a
		rest=$g:$a:$v
		unset vf
		:>ckdep.pom.tmp
		i=-1
		while (( ++i < ncc )); do
			[[ ${cc_where[i]} = "$first":* ]] || continue
			if [[ ${cc_where[i]} = "$rest" ]]; then
				# insert embedded dependencies
				if [[ -n ${cc_found[i]} ]]; then
					print -ru2 -- "[ERROR]" matched \
					    "${cc_where[i]} ${cc_which[i]}" \
					    multiple times
					abend=1
				fi
				cc_found[i]=x
				dopom $i ${cc_which[i]}
				vf=
			elif [[ -z ${vf+x} ]]; then
				vf="$rest (wanted ${cc_where[i]})"
			fi
		done
		if [[ -n $vf ]]; then
			print -ru2 -- "[ERROR] version mismatch: $vf"
			abend=1
		fi
		if [[ -s ckdep.pom.tmp ]]; then
			sort -u <ckdep.pom.tmp |&
			while IFS=' ' read -p ga v scope rest; do
				if [[ $scope != compile ]]; then
					print -ru2 -- "[ERROR] unexpected scope: $ga $v $scope"
					abend=1
				fi
				print -ru4 -- ${ga/:/ } $v
				print -ru5 -- inside::$rest::$ga $v embedded ok
			done
		fi
	done <ckdep.mvn.tmp 4>ckdep.mvp.tmp 5>>ckdep.tmp
	i=-1
	while (( ++i < ncc )); do
		if [[ -z ${cc_found[i]} ]]; then
			print -ru2 -- "[ERROR]" did not match \
			    "${cc_where[i]} ${cc_which[i]}"
			abend=1
		fi
	done
	cat ckdep.mvn.tmp >>ckdep.mvp.tmp
	sort -u <ckdep.mvp.tmp >ckdep.mvn.tmp
fi
# add static dependencies from embedded files, for SecurityWatch
[[ -s ckdep.inc ]] && cat ckdep.inc >>ckdep.tmp
# make compile scope superset provided scope, and either superset test scope
x=$(sort -u <ckdep.tmp)
lastp=
lastt=
print -r -- "$x" | while IFS= read -r line; do
	[[ $line = "$lastp" ]] || [[ $line = "$lastt" ]] || print -r -- "$line"
	lastp=${line/ compile / provided }
	lastt=${lastp/ provided / test }
done >ckdep.tmp
# generate file with changed dependencies set to be a to-do item
# except we don’t licence-analyse test-only dependencies
{
	comm -13 ckdep.lst ckdep.tmp | sed 's/ ok$/ TO''DO/'
	comm -12 ckdep.lst ckdep.tmp
} | sed 's/ test TO''DO$/ test ok/' | sort -uo ckdep.tmp

# check if the list changed
if cmp -s ckdep.lst ckdep.tmp && cmp -s ckdep.mvn ckdep.mvn.tmp; then
	print -ru2 -- '[INFO] list of dependencies did not change'
else
	(diff -u ckdep.lst ckdep.tmp || :)
	# make the new list active
	mv -f ckdep.mvn.tmp ckdep.mvn
	mv -f ckdep.tmp ckdep.lst
	# inform the user
	print -ru2 -- '[WARNING] list of dependencies changed!'
	abend=1
fi
rm -f ckdep.pom ckdep.tmp ckdep.*.tmp
# check if anything needs to be committed
if (( abend )); then
	print -ru2 -- '[ERROR] please commit the changed ckdep.{lst,mvn} files!'
	exit 1
fi

# fail a release build if dependency licence review has a to-do item
[[ $IS_M2RELEASEBUILD = true ]] && \
    if grep -e ' TO''DO$' -e ' FA''IL$' ckdep.lst; then
	print -ru2 -- '[ERROR] licence review incomplete'
	exit 1
fi

exit 0
