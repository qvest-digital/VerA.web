#!/bin/sh -e

VERSION=$2
TAR=../veraweb_$VERSION.orig.tar.gz
DIR=veraweb-$VERSION
TAG=$(echo "veraweb-$VERSION" | sed -re's/~(alpha|beta)/-\1-/')

svn export svn://evolvis.org/scmrepos/svn/veraweb/tags/${TAG} $DIR
GZIP=--best tar -c -z -f $TAR --exclude '*.jar' --exclude '*.class' $DIR
rm -rf $DIR ../$TAG

# move to directory 'tarballs'
if [ -r .svn/deb-layout ]; then
  . .svn/deb-layout
  mv $TAR $origDir && echo "moved $TAR to $origDir"
fi
