#!/bin/mksh
# -*- mode: sh -*-
#-
# Copyright © 2016, 2017
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
LC_ALL=C; export LC_ALL
unset LANGUAGE
PS4='++ '
set -e
set -o pipefail
cd "$(dirname "$0")"

# generate file with changed dependencies set to be a to-do item
(cd .. && mvn -B -P '!test-only-dependencies' dependency:list) 2>&1 | \
    tee /dev/stderr | sed -n \
    -e '/:test$/d' \
    -e '/^\[INFO]    org.evolvis.veraweb:/d' \
    -e '/^\[INFO]    \([^:]*\):\([^:]*\):jar:\([^:]*\):[^:]*$/s//\1:\2 \3 ok/p' \
    >ckdep.tmp
(
	cd ../src/main/webroot-src
	npm list --only prod >&2
	npm list --only prod --json true 2>/dev/null | jq -r \
	    '.dependencies | to_entries[] | recurse(.value.dependencies | objects | to_entries[]) | [.key, .value.version] | map(gsub("(?<x>[^!#-&*-~ -�]+)"; "{\(.x | @base64)}")) | "npm::" + .[0] + " " + .[1] + " ok"'
	bower list | tee /dev/stderr | LC_ALL=C.UTF-8 sed --posix -n \
	    '/^[ ─-╿]\{1,\}\([a-z0-9.-]\{1,\}\)#\([^ ]\{1,\}\)\( .*\)\{0,1\}$/s//bower::\1 \2 ok/p'
) 2>&1 >>ckdep.tmp | sed 's!^![INFO] !' >&2
sort -uo ckdep.tmp ckdep.tmp
{
	comm -13 ckdep.lst ckdep.tmp | sed 's/ ok$/ TO''DO/'
	comm -12 ckdep.lst ckdep.tmp
} | sort -o ckdep.tmp

# check if the list changed
if cmp -s ckdep.lst ckdep.tmp; then
	print -ru2 -- '[INFO] list of dependencies did not change'
else
	(diff -u ckdep.lst ckdep.tmp || :)
	# make the new list active
	mv -f ckdep.tmp ckdep.lst
	# inform the user
	print -ru2 -- '[WARNING] list of dependencies changed!'
	print -ru2 -- '[INFO] please commit the changed ckdep.lst file!'
	exit 1
fi

rm -f ckdep.tmp
exit 0
