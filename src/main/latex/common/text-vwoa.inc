% VerA.web (public) Installationsanleitungstextteil
%
% Copyright © 2015, 2016
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

Pakete: openjdk-7-jre-headless apache2-mpm-prefork apache2 libapache2-mod-jk daemontools-run

Kann anderes System als -core sein. Falls ja, Apache gleich aufsetzen.

a2enmod proxy\_http

In den vhost:
	ProxyPass /vwor-app/ http://localhost:8081/
	ProxyPassReverse /vwor-app/ http://localhost:8081/

Temporäres Verzeichnis ~/vwoa anlegen und populieren:
- vwoa/ 0:0 755
- vwoa/vwoa.jar 0:0 644
  von https://maven-repo.evolvis.org/releases/org/evolvis/veraweb/online-anmeldung/\vwiaverssw{}/online-anmeldung-\vwiaverssw{}.jar
- vwoa/config.json 0:vwoa 640
  aus files.tgz, anzupassen:
  * osiam/{clientSecret,endpoint}
    → osiamsecret in register-veraweb-into-osiam.sh
    → endpoint https://HOSTNAME
  * restauth/{password,username}
    → /etc/tomcat7/tomcat-users.xml in vwor
  * verawebEndpoint
    → https://HOSTNAME/vwor
- vwoa/run 0:0 755 aus files.tgz

Nachdem fertig: mv vwoa /etc/service/ und beten.
XXX /service/

svstat /etc/service/vwoa

less /var/log/vwoa/vwoa.log
