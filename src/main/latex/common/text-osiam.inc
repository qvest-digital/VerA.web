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

Link: https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md

OSIAM Pakete: curl mksh openjdk-7-jre-headless postgresql tomcat7 unzip

RAM: 960 nur OSIAM (1536 rec), 1024 mit VW+VWOA (2048 rec)

Downloads:

http://maven-repo.evolvis.org/releases/org/osiam/osiam-auth-server/2.0/osiam-auth-server-2.0.war

http://maven-repo.evolvis.org/releases/org/osiam/osiam-resource-server/2.0/osiam-resource-server-2.0.war

DB-Konfig (Default-PW ong):

sudo -u postgres createuser -D -P -R -S ong

sudo -u postgres createdb -E UTF-8 -O ong -T template0 ong

Dateien aus vw-online-registration-files.tgz osiam/* nach /etc/osiam/ kopieren und anpassen.

Für OSIAM-Betrieb muß der tomcat auf localhost (oder außen) auf http/8080 lauschen!

Anpassen: /etc/tomcat7/catalina.properties /^shared.loader=/s!$!,/etc/osiam!

Anpassen: /etc/default/tomcat7 /-Xmx128m/ entweder /-Xmx512m/ (bis 1¼ GiB RAM) oder /-Xms512m -Xmx1024m/ (ab 1¼ GiB RAM)

Tomcat neustarten, dann WARs rein als osiam-auth-server.war und osiam-resource-server.war umbenannt.

Wait until deployed (takes more than 3 minutes).
Check if started: curl http://localhost:8080/osiam-resource-server/ServiceProviderConfigs

VerA.web bei OSIAM anmelden: register-veraweb-into-osiam.sh bearbeiten und rennen
