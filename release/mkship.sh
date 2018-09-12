#!/usr/bin/env mksh
# -*- mode: sh -*-
#-
# Copyright © 2017, 2018
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
# Create shipment PKZIP archive of the last release.

die() {
	print -ru2 -- "E: $*"
	exit 1
}

# initialisation
LC_ALL=C; export LC_ALL
unset LANGUAGE
cd "$(dirname "$0")/.." || die cannot cd

do_upload=0
while getopts "u" ch; do
	case $ch {
	(u) do_upload=1 ;;
	(+u) do_upload=0 ;;
	(*) exit 255 ;;
	}
done
shift $((OPTIND - 1))

[[ -s pom.xml ]] || die pom.xml not found
tag=$1
[[ -n $tag ]] || tag=$(set -e; git describe --tags $(set -e; \
    git rev-list --tags --max-count=1)) || \
    die 'cannot fetch tag, set $1 to latest tag'
[[ $tag = [1-9]*([0-9]).@(0|[1-9]*([0-9]))?(.@(0|[1-9]*([0-9]))) ]] || \
    die "latest tag '$tag' invalid, set \$1 to latest tag"
pom=$(git show "$tag:pom.xml") || die git show died
[[ -n $pom ]] || die empty POM from tag "'$tag'"
vsn=$(<<<"$pom" xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 \
    -T -t -c /pom:project/pom:version) || die xmlstarlet died
[[ -n $vsn ]] || die xmlstarlet returned no project version
vsn=${vsn%-SNAPSHOT}
[[ $vsn = [1-9]*([0-9]).@(0|[1-9]*([0-9]))?(.@(0|[1-9]*([0-9]))) ]] || \
    die project version "'$vsn'" invalid

rm -rf target/ship
mkdir -p target/ship/"$vsn"
cd target/ship/"$vsn" || die cannot create working directory

wget "https://repo-bn-01.lan.tarent.de/repository/maven-releases/org/evolvis/veraweb/veraweb-core/$vsn/veraweb-core-$vsn-installationsanleitung.pdf" || die cannot fetch Installationshandbuch
wget "https://repo-bn-01.lan.tarent.de/repository/maven-releases/org/evolvis/veraweb/veraweb-core/$vsn/veraweb-core-$vsn-files.tgz" || die cannot fetch files tarball
wget "https://repo-bn-01.lan.tarent.de/repository/maven-releases/org/evolvis/veraweb/veraweb-core/$vsn/veraweb-core-$vsn.war" || die cannot fetch core war
wget "https://repo-bn-01.lan.tarent.de/repository/maven-releases/org/evolvis/veraweb/rest-api/$vsn/rest-api-$vsn.war" || die cannot fetch Rest-API war
wget "https://repo-bn-01.lan.tarent.de/repository/maven-releases/org/evolvis/veraweb/online-anmeldung/$vsn/online-anmeldung-$vsn.jar" || die cannot fetch Online-Anmeldung

for f in \
    "veraweb-core-$vsn-installationsanleitung.pdf" \
    "veraweb-core-$vsn-files.tgz" \
    "veraweb-core-$vsn.war" \
    "rest-api-$vsn.war" \
    "online-anmeldung-$vsn.jar" \
    ; do
	[[ -s $f ]] || die empty file "'$f'"
done

cd .. || die cannot cd

zip -D -X -9 "$vsn.zip" "$vsn/"* || die zip died
[[ -s $vsn.zip ]] || die zip created empty file
ls -la -- "$(realpath "$vsn.zip")"
sha256sum --tag -- "$(realpath "$vsn.zip")"
(( do_upload )) || exit 0

cat >pom.xml <<EOF
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.evolvis.veraweb</groupId>
		<artifactId>veraweb-parent</artifactId>
		<version>$vsn</version>
	</parent>
	<groupId>org.evolvis.veraweb.ship</groupId>
	<artifactId>veraweb</artifactId>
	<packaging>pom</packaging>
	<description>VerA.web-Distribution</description>
	<build>
		<plugins>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>build-helper-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>attach-artifacts</id>
						<phase>package</phase>
						<goals>
							<goal>attach-artifact</goal>
						</goals>
						<configuration>
							<artifacts>
								<artifact>
									<file>\${project.version}.zip</file>
									<type>zip</type>
								</artifact>
							</artifacts>
						</configuration>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.owasp</groupId>
				<artifactId>dependency-check-maven</artifactId>
				<configuration>
					<skip>true</skip>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>
EOF
set -x
exec mvn -B deploy
