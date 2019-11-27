FROM tomcat:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN mkdir -p /etc/veraweb && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get --purge dist-upgrade -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install --no-install-recommends -y \
        libpostgresql-jdbc-java && \
    ln -sf /usr/share/java/postgresql-jdbc4.jar /usr/local/tomcat/lib/ && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ADD veraweb.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb"]
