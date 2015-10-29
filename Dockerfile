FROM debian:jessie

MAINTAINER Veraweb Team <veraweb@tarent.de>

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install --no-install-recommends -y \
        apache2 libapache2-mod-jk && \
    a2enmod jk && a2enmod ssl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 443

VOLUME ["/etc/apache2"]

CMD ["slapd", "-d", "32768", "-u", "openldap", "-g", "openldap"]
