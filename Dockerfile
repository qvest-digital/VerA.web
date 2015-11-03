FROM java:7
MAINTAINER Veraweb Team <veraweb@tarent.de>

RUN mkdir -p /opt/online-anmeldung

ADD ./target/vw-online-registration.jar /opt/online-anmeldung/

CMD ["java",  "-jar", "/opt/online-anmeldung/vw-online-registration.jar", "server", "/root/veraweb-oa/veraweb-oa-config.yml"]  
