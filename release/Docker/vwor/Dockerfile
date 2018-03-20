FROM tomcat:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN mkdir -p /etc/veraweb /var/lib/veraweb

ADD postgresql-jdbc4.jar /usr/local/tomcat/lib/
ADD vwor.war /usr/local/tomcat/webapps/

VOLUME ["/etc/veraweb", "/var/lib/veraweb"]
