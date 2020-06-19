FROM debian:jessie

MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

RUN LC_ALL=C; export LC_ALL; unset LANGUAGE; \
    rm -f /etc/apt/sources.list /etc/apt/sources.list.d/* && \
    printf 'deb http://%s main\n' >/etc/apt/sources.list \
	'httpredir.debian.org/debian/ jessie' \
	'httpredir.debian.org/debian/ jessie-updates' \
	'httpredir.debian.org/debian-security jessie/updates' && \
    mkdir -p /var/cache/apt/archives && \
    apt-get clean && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get --purge install --no-install-recommends -y \
	eatmydata && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge dist-upgrade -y && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge install --no-install-recommends -y \
	apache2 libapache2-mod-jk ldap-account-manager && \
    a2enmod jk && a2enmod ssl && a2enmod proxy && a2enmod proxy_http && \
    a2dissite 000-default && a2ensite default-ssl && \
    eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

EXPOSE 443

VOLUME ["/etc/apache2", "/etc/libapache2-mod-jk", \
    "/var/lib/ldap-account-manager/config"]

ENV \
     LANG=C \
     PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
     APACHE_RUN_USER=www-data \
     APACHE_RUN_GROUP=www-data \
     APACHE_LOG_DIR=/var/log/apache2 \
     APACHE_LOCK_DIR=/var/lock/apache2 \
     APACHE_RUN_DIR=/var/run/apache2 \
     APACHE_PID_FILE=/var/run/apache2/apache2.pid

COPY start.sh /start.sh

CMD ["/start.sh"]
