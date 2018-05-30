#!/usr/bin/env mksh
set -e
set -o pipefail

wget -qO - https://veraweb-tools.lan.tarent.de/ | \
    sed -n '/^.*<li><a href="\([^"]*\)\/".*$/s//\1/p' | \
    fgrep -v '~' | \
    sort --version-sort
