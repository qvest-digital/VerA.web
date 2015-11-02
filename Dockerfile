FROM java:7
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

RUN mkdir -p /opt/online-anmeldung

ADD ./target/vw-online-registration.jar /opt/online-anmeldung/

CMD ["java",  "-jar", "/opt/online-anmeldung/vw-online-registration.jar", "server", "/opt/online-anmeldung/config.yaml"]  
