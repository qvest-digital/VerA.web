#!/usr/bin/env mksh
# needs root

set -e
set -o pipefail
cd "$(dirname "$0")"

rm -f *.xml
set +e
print -r -- "cd /var/lib/jenkins/jobs; pax -w -s '!/config!!' -M dist veraweb*/config.xml" | \
    ssh -l root ci-busyapps.lan.tarent.de mksh | paxtar xvvf -
set -A errs -- $? "${PIPESTATUS[@]}"
if [[ ${errs[0]} != 0 ]]; then
	print -ru2 -- E: pipeline failed with statuses "${errs[@]}"
	print -ru2 -- N: in this order, '$?', print, ssh, tar
	rm -f *.xml
	exit 1
fi
set -e
for x in *.xml; do
	echo >>"$x"
done
