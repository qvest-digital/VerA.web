FROM tomcat:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN mkdir -p /etc/veraweb /var/lib/veraweb && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get --purge dist-upgrade -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install --no-install-recommends -y \
        netcat-openbsd && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ADD postgresql-jdbc4.jar /usr/local/tomcat/lib/
ADD vwor.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb", "/var/lib/veraweb"]
