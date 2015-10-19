% VerA.web (public) Installationsanleitungstextteil
%
% Copyright © 2015
%	Thorsten Glaser <t.glaser@tarent.de>
%
% Provided that these terms and disclaimer and all copyright notices
% are retained or reproduced in an accompanying document, permission
% is granted to deal in this work without restriction, including un‐
% limited rights to use, publicly perform, distribute, sell, modify,
% merge, give away, or sublicence.
%
% This work is provided “AS IS” and WITHOUT WARRANTY of any kind, to
% the utmost extent permitted by applicable law, neither express nor
% implied; without malicious intent or gross negligence. In no event
% may a licensor, author or contributor be held liable for indirect,
% direct, other damage, loss, or other issues arising in any way out
% of dealing in the work, even if advised of the possibility of such
% damage or existence of a defect, except proven that it results out
% of said person’s immediate fault when using the work as intended.
%-
% im Moment nur Notizen

mkdir -p /var/lib/veraweb/uploaded/ # writable for tomcat7

install core-files/vwor.properties to /etc/veraweb/

install core-files/veraweb.properties (if not OA) or
oa-files/core/veraweb.properties (if OA) to /etc/veraweb/
after substitution

TODO auch in -core: starting the JVM with -Dfile.encoding=UTF-8

TODO auch in -core: JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"

Selbes System wie -core, welcher ok sein muß.

Bearbeiten /etc/tomcat7/context.xml vor das schließende </Context> am Dateiende:

<Resource name="jdbc/vwonlinereg" auth="Container" type="javax.sql.DataSource"
 username="veraweb" password="veraweb" url="jdbc:postgresql://localhost:5432/veraweb"
 maxActive="8" maxIdle="4" driverClassName="org.postgresql.Driver" />

Bearbeiten /etc/tomcat7/tomcat-users.xml vor das schließende </tomcat-users> am Dateiende:

  <role rolename="veraweb"/>
  <user username="veraweb" password="veraweb" roles="veraweb"/>

Anpassen username/password (spätere HTTP Basic Auth-Absicherung der API)! ⇒ veraweb.properties

Tomcat neustarten.

https://maven-repo.evolvis.org/releases/org/evolvis/veraweb/rest-api/\vwiaverssw{}/rest-api-\vwiaverssw{}.war
als vwor.war reinwerfen.

Apache-Konfig zusätzlich:
	JkMount /vwor* ajp13_worker
Absicherung: macht sein eigenes HTTP Basic Auth
