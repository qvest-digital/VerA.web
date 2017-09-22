#!/bin/sh
#-
# Copyright (c) 2015, 2016
#	Thorsten Glaser <t.glaser@tarent.de>
# Copyright (c) 2013
#	mirabilos <m@mirbsd.org>
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
# Register VerA.web into OSIAM. Should be idempotent.

# -*- configuration -*-

# OSIAM database name
dbname=ong
# OSIAM authentication (base64 of "example-client:secret")
serverauth=ZXhhbXBsZS1jbGllbnQ6c2VjcmV0
# VerA.web application secret (change it!)
vwsecret=geheim

# -*- do not change below this point -*-

case ${KSH_VERSION:-} in
(*MIRBSD\ KSH*) ;;
(*)
	command -v mksh >/dev/null 2>&1 && exec mksh "$0" "$@"
	echo >&2 "E: missing tool: mksh"
	exit 1
	;;
esac

nl='
'

print -u2 I: checking for prerequisites
have_tools=1
for tool in sudo psql curl; do
	whence -p "$tool" >/dev/null && continue
	have_tools=0
	print -ru2 "E: missing tool: $tool"
done
(( have_tools )) || exit 1

print -u2 I: validating hostname
if ! fqhn=$(hostname -f) || [[ $fqhn != *.* ]] || \
    [[ $fqhn = *.@(invalid|local|lan|home) ]]; then
	print -ru2 "E: invalid hostname/FQDN: '$fqdn'"
	exit 1
fi
print -u2 I: checking whether OSIAM is running
# no simple check for auth-server, we try that below
if ! x=$(curl "https://$fqhn/osiam-resource-server/ServiceProviderConfigs") || \
    [[ $x != *'"specUrl":"http://tools.ietf.org/html/rfc6749"'* ]]; then
	print -ru2 "E: OSIAM resource-server not running: $x"
	exit 1
fi

# escape string into JSON string (with surrounding quotes)
function json_escape {
	[[ -o utf8-mode ]]; local u=$?
	set -U
	local o=\" s
	if (( $# )); then
		read -raN-1 s <<<"$*"
		unset s[${#s[*]}-1]
	else
		read -raN-1 s
	fi
	local -i i=0 n=${#s[*]} wc
	local -Uui16 -Z7 x
	local -i1 ch

	while (( i < n )); do
		(( ch = x = wc = s[i++] ))
		case $wc {
		(8) o+=\\b ;;
		(9) o+=\\t ;;
		(10) o+=\\n ;;
		(12) o+=\\f ;;
		(13) o+=\\r ;;
		(34) o+=\\\" ;;
		(92) o+=\\\\ ;;
		(*)
			if (( wc < 0x20 || wc > 0xFFFD || \
			    (wc >= 0xD800 && wc <= 0xDFFF) || \
			    (wc > 0x7E && wc < 0xA0) )); then
				o+=\\u${x#16#}
			else
				o+=${ch#1#}
			fi
			;;
		}
	done
	(( u )) && set +U
	print -nr -- "$o\""
}

print -u2 I: updating database
cd /
sudo -u postgres psql "$dbname" <<'EOF'
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
    "https://$fqhn/osiam-auth-server/oauth/token"); then
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
    "https://$fqhn/osiam-auth-server/Client" <<EOD
{
  "accessTokenValiditySeconds": "5000",
  "client_secret": $(json_escape "$vwsecret"),
  "grants": [
    "authorization_code",
    "client_credentials",
    "refresh_token",
    "password"
  ],
  "id": "online-registration",
  "implicit": "false",
  "redirectUri": "http://localhost/irrelevant",
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

x=$(print -r -- "$x" | tr -d '\r')
if [[ $x != *"$nl$nl{"*'"id":"online-registration"'* ]]; then
	print -u2 E: invalid register response
	[[ $x = *'500 Internal Server Error'* ]] && x=${x%%$nl*}
	print -ru2 "N: got: $x"
	exit 1
fi

print -u2 I: all done
exit 0
