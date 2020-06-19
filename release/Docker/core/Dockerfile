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
    mkdir -p /var/cache/apt/archives && \
    eatmydata apt-get clean && \
    eatmydata apt-get update && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge dist-upgrade -y && \
    DEBIAN_FRONTEND=noninteractive eatmydata apt-get --purge install --no-install-recommends -y \
	ed libpostgresql-jdbc-java netcat-openbsd && \
    mkdir -p /etc/veraweb && \
    ln -sf /usr/share/java/postgresql-jdbc4.jar /usr/local/tomcat/lib/ && \
    printf '%s\n' \
	'java.util.logging.SimpleFormatter.format=%1$tF %1$tT.%1$tL %4$7s (%2$s) [%3$s] %5$s%6$s%n' \
	'org.apache.juli.OneLineFormatter.timeFormat=yyyy-MM-dd HH:mm:ss' \
	'java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter' \
	>>/usr/local/tomcat/conf/logging.properties && \
    printf '%s\n' \
	'/A "Connector" represents/i' \
	'<Connector secretRequired="false" port="8009" protocol="AJP/1.3" redirectPort="8443" />' \
	. w q | ed -s /usr/local/tomcat/conf/server.xml && \
    eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

COPY veraweb.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb"]
