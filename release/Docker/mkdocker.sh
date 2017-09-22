#!/usr/bin/env mksh
set -ex
cd "$(dirname "$0")"

doone() (
	cd "$1"
	docker build -t veraweb-tools.lan.tarent.de:5000/"$2" .
	docker push veraweb-tools.lan.tarent.de:5000/"$2"
)

doone core veraweb-core
doone vwoa veraweb-oa
doone vwor veraweb-rest-api
# extra
doone httpd veraweb-httpd
doone ldap veraweb-ldap
