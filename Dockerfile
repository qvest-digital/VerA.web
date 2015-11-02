FROM tomcat:7.0.64
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

ADD ./target/vwor.war /usr/local/tomcat/webapps/
 
