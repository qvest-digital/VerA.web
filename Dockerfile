FROM java:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

RUN mkdir -p /opt/online-anmeldung

ADD ./target/vw-online-registration.jar /opt/online-anmeldung/

EXPOSE 8181

CMD ["java", "-Dfile.encoding=UTF-8", "-Djava.awt.headless=true", \
    "-Djava.security.egd=file:/dev/./urandom", "-Xmx128m", \
    "-jar", "/opt/online-anmeldung/vw-online-registration.jar", \
    "server", "/root/veraweb-oa/veraweb-oa-config.json"]
