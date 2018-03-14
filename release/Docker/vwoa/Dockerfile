FROM openjdk:8
MAINTAINER VerA.web Team <veraweb-discuss@lists.evolvis.org>

ADD vw-online-registration.jar /

EXPOSE 8181

CMD ["java", "-Dfile.encoding=UTF-8", "-Djava.awt.headless=true", \
    "-Djava.security.egd=file:/dev/./urandom", "-Xmx128m", \
    "-jar", "/vw-online-registration.jar", \
    "server", "/srv/config.jsn"]

VOLUME ["/srv"]
