FROM debian:jessie

MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

ENV OPENLDAP_VERSION 2.4.40

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install --no-install-recommends -y \
        slapd=${OPENLDAP_VERSION}* && \
    mv /etc/ldap /etc/ldap.dist && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 389

VOLUME ["/etc/ldap", "/var/lib/ldap", "/bootstrap/data"]

COPY modules/ /etc/ldap.dist/modules

COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

CMD ["slapd", "-d", "32768", "-u", "openldap", "-g", "openldap"]
