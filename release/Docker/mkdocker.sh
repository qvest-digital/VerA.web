#!/usr/bin/env mksh
set -ex
cd "$(dirname "$0")"
tag=$1

doone() (
	cd "$1"
	docker build -t veraweb-tools.lan.tarent.de:5000/"$2" .
	docker push veraweb-tools.lan.tarent.de:5000/"$2"
	[[ -n $tag ]] || return 0
	docker tag veraweb-tools.lan.tarent.de:5000/"$2" \
	    veraweb-tools.lan.tarent.de:5000/"$2:$tag"
	docker push veraweb-tools.lan.tarent.de:5000/"$2:$tag"
)

doone core veraweb-core
doone vwoa veraweb-oa
doone vwor veraweb-rest-api
# extra
doone httpd veraweb-httpd
doone ldap veraweb-ldap
exit 0
