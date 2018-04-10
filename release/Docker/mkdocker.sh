#!/usr/bin/env mksh

print -ru2 -- '[INFO] Do not forget to run…'
print -ru2 -- '[INFO]      docker system prune --volumes || docker system prune'
print -ru2 -- '[INFO] … for cleanup on veraweb-tools!'

set -ex
cd "$(dirname "$0")"
tag=$1
set -A ours

doone() (
	cd "$1"
	docker build -t veraweb-tools.lan.tarent.de:5000/"$2" .
	docker push veraweb-tools.lan.tarent.de:5000/"$2"
	[[ -n $tag ]] || return 0
	docker tag veraweb-tools.lan.tarent.de:5000/"$2" \
	    veraweb-tools.lan.tarent.de:5000/"$2:$tag"
	docker push veraweb-tools.lan.tarent.de:5000/"$2:$tag"
	set -A ours -- "${ours[@]}" veraweb-tools.lan.tarent.de:5000/"$2:$tag"
)

ln -f ../../core/target/veraweb.war core/
ln -f ../../vwoa/target/vw-online-registration.jar vwoa/
ln -f ../../vwor/target/vwor.war vwor/

tar -xzf ../../core/target/veraweb-core-*-files.tgz \
    -O $(tar -tzf ../../core/target/veraweb-core-*-files.tgz | \
    fgrep postgresql-jdbc4.jar) >core/postgresql-jdbc4.jar
ln -f core/postgresql-jdbc4.jar vwor/

doone core veraweb-core
doone vwoa veraweb-oa
doone vwor veraweb-rest-api
# extra
doone httpd veraweb-httpd
doone ldap veraweb-ldap

# release export
[[ -n $tag ]] || exit 0

set -A externals -- \
    burberius/osiam-simple:2.5.1 \
    postgres:9.3

for x in "${externals[@]}"; do
	docker pull "$x"
done

mkdir -p "/var/www/html/$tag"

for x in "${ours[@]}" "${externals[@]}"; do
	docker save -o "/var/www/html/$tag/${x##*/}.img" "$x"
	gzip -n9 "/var/www/html/$tag/${x##*/}.img" &
done
wait
