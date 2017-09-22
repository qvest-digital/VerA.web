FROM tomcat:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN mkdir -p /etc/veraweb

ADD ../../../core/target/veraweb.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb"]
