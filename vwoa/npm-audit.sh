#!/bin/sh
cd "$(dirname "$0")"/src/main/webroot-src || exit 1
npm audit
rv=$?
if test x"$rv" = x"0"; then
	echo "[INFO] npm audit ran with no problems"
else
	echo "[ERROR] npm audit returned errorlevel $?"
fi
exit 0
