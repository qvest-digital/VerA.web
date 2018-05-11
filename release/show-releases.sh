#!/usr/bin/env mksh
set -e
set -o pipefail

wget -qO - 'https://repo-bn-01.lan.tarent.de/service/rest/beta/search?repository=maven-releases&maven.groupId=org.evolvis.veraweb&maven.artifactId=veraweb-parent' | \
    jq -r '.items[].version' | \
    sort --version-sort
