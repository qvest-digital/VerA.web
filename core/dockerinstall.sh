#!/bin/sh
set -ex
cd "$(dirname "$0")"
docker build -t veraweb-tools.lan.tarent.de:5000/veraweb-core .
docker push veraweb-tools.lan.tarent.de:5000/veraweb-core