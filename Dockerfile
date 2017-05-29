FROM tomcat:8.5.15
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

EXPOSE 8009 8080

RUN mkdir -p /etc/veraweb /var/lib/veraweb

ADD ./target/vwor.war /usr/local/tomcat/webapps/
 
VOLUME ["/etc/veraweb", "/var/lib/veraweb"]
