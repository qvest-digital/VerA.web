FROM debian:jessie

MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install --no-install-recommends -y \
        apache2 libapache2-mod-jk ldap-account-manager && \
    a2enmod jk && a2enmod ssl && a2enmod proxy && a2enmod proxy_http && \
    a2dissite 000-default && a2ensite default-ssl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 443

VOLUME ["/etc/apache2", "/etc/libapache2-mod-jk", \
    "/var/lib/ldap-account-manager/config"]

ENV  LANG=C \
     PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
     APACHE_RUN_USER=www-data \
     APACHE_RUN_GROUP=www-data \
     APACHE_LOG_DIR=/var/log/apache2 \
     APACHE_LOCK_DIR=/var/lock/apache2 \
     APACHE_RUN_DIR=/var/run/apache2 \
     APACHE_PID_FILE=/var/run/apache2/apache2.pid

COPY start.sh /start.sh

CMD ["/start.sh"]
