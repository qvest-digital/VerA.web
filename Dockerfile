FROM tomcat:7.0.64
MAINTAINER Veraweb Team <veraweb@tarent.de>

ADD ./target/veraweb.war /usr/local/tomcat/webapps/
 
