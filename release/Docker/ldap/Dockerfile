FROM debian:jessie

MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

RUN LC_ALL=C; export LC_ALL; unset LANGUAGE; \
    rm -f /etc/apt/sources.list /etc/apt/sources.list.d/* && \
    printf 'deb http://%s main\n' >/etc/apt/sources.list \
	'httpredir.debian.org/debian/ jessie' \
	'httpredir.debian.org/debian/ jessie-updates' \
	'httpredir.debian.org/debian-security jessie/updates' && \
    apt-get clean && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get --purge install --no-install-recommends -y \
	eatmydata && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge dist-upgrade -y && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge install --no-install-recommends -y \
	slapd && \
    mv /etc/ldap /etc/ldap.dist && \
    eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

EXPOSE 389

VOLUME ["/etc/ldap", "/var/lib/ldap", "/bootstrap/data"]

COPY modules/ /etc/ldap.dist/modules

COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

CMD ["slapd", "-d", "32768", "-u", "openldap", "-g", "openldap"]
