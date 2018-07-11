FROM tomcat:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN mkdir -p /etc/veraweb && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get --purge dist-upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ADD postgresql-jdbc4.jar /usr/local/tomcat/lib/
ADD veraweb.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb"]
