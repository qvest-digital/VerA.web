FROM tomcat:8

MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN LC_ALL=C; export LC_ALL; unset LANGUAGE; \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get --purge install --no-install-recommends -y \
	eatmydata lsb-release && \
    r=$(lsb_release -cs) && case $r in \
    (sarge|etch|lenny|squeeze|wheezy|jessie|stretch|buster) s=$r/updates ;; \
    (*) s=$r-security ;; esac && \
    rm -f /etc/apt/sources.list /etc/apt/sources.list.d/* && \
    printf 'deb http://%s main\n' >/etc/apt/sources.list \
	"deb.debian.org/debian/ $r" \
	"deb.debian.org/debian/ $r-updates" \
	"deb.debian.org/debian-security/ $s" && \
    eatmydata apt-get clean && \
    eatmydata apt-get update && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge dist-upgrade -y && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge install --no-install-recommends -y \
	libpostgresql-jdbc-java netcat-openbsd && \
    mkdir -p /etc/veraweb /var/lib/veraweb && \
    ln -sf /usr/share/java/postgresql-jdbc4.jar /usr/local/tomcat/lib/ && \
    eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

COPY vwor.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb", "/var/lib/veraweb"]
