#!/bin/mksh
# needs root

rm -f *.xml
print -r -- "cd /var/lib/jenkins/jobs; pax -w -s '!/config!!' -M dist veraweb*/config.xml" | \
    ssh -l root ci-busyapps.lan.tarent.de mksh | paxtar xvvf -
for x in *.xml; do
	echo >>"$x"
done
