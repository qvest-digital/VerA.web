#!/bin/sh
set -ex
cd "$(dirname "$0")"
docker build -t veraweb-tools.lan.tarent.de/veraweb-rest-api .
docker push veraweb-tools.lan.tarent.de/veraweb-rest-api
