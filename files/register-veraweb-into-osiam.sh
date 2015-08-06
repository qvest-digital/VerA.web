#!/bin/mksh
#-
# Copyright (c) 2015
#	Thorsten Glaser <t.glaser@tarent.de>
#
# Provided that these terms and disclaimer and all copyright notices
# are retained or reproduced in an accompanying document, permission
# is granted to deal in this work without restriction, including un-
# limited rights to use, publicly perform, distribute, sell, modify,
# merge, give away, or sublicence.
#
# This work is provided "AS IS" and WITHOUT WARRANTY of any kind, to
# the utmost extent permitted by applicable law, neither express nor
# implied; without malicious intent or gross negligence. In no event
# may a licensor, author or contributor be held liable for indirect,
# direct, other damage, loss, or other issues arising in any way out
# of dealing in the work, even if advised of the possibility of such
# damage or existence of a defect, except proven that it results out
# of said person's immediate fault when using the work as intended.
#-
# Register VerA.web into OSIAM. Mostly idempotent, except OSIAM bug:
# https://github.com/osiam/auth-server/issues/54
#
#XXX TODO: use https-only for OSIAM

# -*- configuration -*-

# OSIAM authentication (base64 of "example-client:secret")
serverauth=ZXhhbXBsZS1jbGllbnQ6c2VjcmV0
# VerA.web application secret (change it!)
osiamsecret=geheim
# path to VerA.web-core installation (change it!)
verawebserver=https://veraweb.lan.tarent.de/veraweb

# -*- do not change below this point -*-

nl='
'
print -u2 I: updating database
cd /
sudo -u postgres psql ong <<'EOF'
INSERT INTO scim_extension
    SELECT (SELECT MAX(internal_id) + 1 FROM scim_extension),
	'urn:scim:schemas:veraweb:1.5:Person'
    WHERE NOT EXISTS (
	SELECT internal_id FROM scim_extension WHERE urn='urn:scim:schemas:veraweb:1.5:Person'
    );
INSERT INTO scim_extension_field
    SELECT (SELECT MAX(internal_id) + 1 FROM scim_extension_field),
	'tpersonid', false, 'INTEGER',
	(SELECT internal_id FROM scim_extension WHERE urn='urn:scim:schemas:veraweb:1.5:Person')
    WHERE NOT EXISTS (
	SELECT sef.internal_id FROM scim_extension_field sef, scim_extension se
	WHERE sef.name='tpersonid'
	    AND sef.extension_internal_id=se.internal_id
	    AND se.urn='urn:scim:schemas:veraweb:1.5:Person'
    );
EOF

print -u2 I: downloading auth token
if ! x=$(curl -H "Authorization: Basic $serverauth" -X POST \
    -d "grant_type=client_credentials&scope=GET POST PUT DELETE" \
    http://localhost:8080/osiam-auth-server/oauth/token); then
	print -u2 E: cannot retrieve auth token
	exit 1
fi

tok=${x#'{"access_token":"'}
tok=${tok%%\"*}

if [[ $x != '{"access_token":"'+([0-9a-f-])\",* || $tok != +([0-9a-f-]) ]]; then
	print -u2 E: invalid auth response
	print -ru2 "N: got: $x"
	exit 1
fi

print -u2 I: registering client
if ! x=$(curl -i -H "Accept: application/json" \
    -H "Content-type: application/json" \
    -H "Authorization: Bearer $tok" -X POST --data-binary @- \
    http://localhost:8080/osiam-auth-server/Client <<EOD
{
  "accessTokenValiditySeconds": "5000",
  "client_secret": "$osiamsecret",
  "grants": [
    "authorization_code",
    "client_credentials",
    "refresh_token",
    "password"
  ],
  "id": "online-registration",
  "implicit": "false",
  "redirectUri": "$verawebserver",
  "refreshTokenValiditySeconds": "5000",
  "scope": [
    "POST",
    "PUT",
    "GET",
    "DELETE",
    "PATCH"
  ],
  "validityInSeconds": "5000"
}
EOD
    ); then
	print -u2 E: cannot register client
	exit 1
fi

if [[ $x != '{'*'"id":"online-registration"'* ]]; then
	print -u2 E: invalid register response
	[[ $x = *'500 Internal Server Error'* ]] && x=${x%%$nl*}
	print -ru2 "N: got: $x"
	exit 1
fi

print -u2 I: all done
exit 0
