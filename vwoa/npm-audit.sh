#!/bin/sh
cd "$(dirname "$0")"/src/main/webroot-src || exit 1
npm audit
echo ⇒ errorlevel $?
exit 0
