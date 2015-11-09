FROM tomcat:7.0.64
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

ADD ./target/veraweb.war /usr/local/tomcat/webapps/
 
VOLUME ["/etc/veraweb"]
