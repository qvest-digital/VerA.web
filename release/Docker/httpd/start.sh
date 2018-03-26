#!/bin/sh
if ! test -d "$APACHE_RUN_DIR"; then
	mkdir -p "$APACHE_RUN_DIR"
	chown "$APACHE_RUN_USER:$APACHE_RUN_GROUP" "$APACHE_RUN_DIR"
fi
rm -f "$APACHE_PID_FILE"
exec /usr/sbin/apache2 -DFOREGROUND -k start
