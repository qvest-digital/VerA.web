% VerA.web (public) Installationsanleitung
%
% Copyright © 2015, 2016
%	Thorsten Glaser <t.glaser@tarent.de>
% Copyright © 2014, 2015
%	Thorsten “mirabilos” Glaser <thorsten.glaser@teckids.org>
% Copyright © 2013, 2014
%	Dominik George <dominik.george@teckids.org>
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
% TODO: slapd, lam, MTA
% TODO: graphics from experiments.tex for overall concepts
% TODO core: curl https://veraweb:veraweb@veraweb-ohne.lan.tarent.de/vwor/rest/available/;echo ⇒ OK
% TODO vwoa: primer on service management with DJB dæmontools

\ifupgradeanleitung
 \ifoa
  \newcommand{\vwianame}{Upgradeanleitung VerA.web-full}
 \else% ifupgradeanleitung !ifoa
  \newcommand{\vwianame}{Upgradeanleitung VerA.web-core}
 \fi% ifupgradeanleitung !ifoa
\else% !ifupgradeanleitung
 \ifoa
  \newcommand{\vwianame}{Installationsanleitung VerA.web-full}
 \else% !ifupgradeanleitung !ifoa
  \newcommand{\vwianame}{Installationsanleitung VerA.web-core}
 \fi% !ifupgradeanleitung !ifoa
\fi% !ifupgradeanleitung

\tarentanleitung{\vwianame}{\vwiaverssw}
 {\vwiaversfassungnr}{\vwiaversfassungmonat}{\vwiaversfassungjahr}{veraweblogo}

\input{toc.inc}

\section{Einleitung}\label{sec:intro}

„VerA.web“ steht für Veranstaltungs- und Adreßverwaltung im „web“ (Internet).
VerA.web ist eine Open Source-Webanwendung, die die IT-gestützte Planung und
Durchführung von Anlässen, Konferenzen und anderen Veranstaltungen maßgeblich
unterstützt.

\ifoa
Die „Online-Anmeldung“ ermöglicht es Gästen, sich selbst zu Veranstaltungen
anzumelden, die für diese Prozedur freigeschaltet sind, sowie zu einer
Veranstaltung angemeldeten Delegationen, ihre Mitglieder selbst zu melden.
\fi% oa

\subsection{Über diese Anleitung}\label{subsec:aboutmanual}

\ifoa
Dieses Dokument beschreibt, wie das Veranstaltungsmanagement
VerA.web mit Online-Anmeldung und (falls nötig) das
Identitätsmanagementsystem OSIAM
\else% !oa
Dieses Dokument beschreibt, wie VerA.web core (die eigentliche
Veranstaltungsmanagementsoftware)
\endif% !oa
durch einen Systemadministrator
eigenständig installiert werden kann. Hierbei wird eine empfohlene
Installation beschrieben; ein Betrieb ist auch mit abweichender
Konfiguration (z.B. Trennung von Core und REST-API) möglich, aber
nicht durch dieses Dokument abgedeckt.

Alle Code-Listings finden Sie auch nochmal als Plaintext unter
\texttt{\jobname.lst} im „files“-Tarball
(\texttt{veraweb-core-\vwiaverssw{}-files.tgz} \emph{oder}
\texttt{veraweb-core-\vwiaverssw{}-files.zip}, siehe
\hyperlink{manual:distro}{Die VerA.web-Distribution}).

\subsection{Konzepte}\label{subsec:concepts}

Der Kern von VerA.web besteht aus zwei Webanwendungen, die in einem
Java-Applikationsserver (Apache Tomcat) laufen. Diese beiden Komponenten
werden in der Regel auf demselben Server installiert und greifen auf
dieselbe Datenbank zu.

\begin{itemize}
 \item{\texttt{veraweb.war}: Die eigentliche Veranstaltungsmanagementsoftware,
  welche später über einen Apache-Webserver von Sachbearbeitern bedient wird.}
 \item{\texttt{vwor.war}: Die sogenannte „REST-API“; eine Funktionssammlung,
  welche Dienste für sowohl VerA.web als auch die (optionale) Online-Anmeldung
  bereitstellt.}
\end{itemize}

\ifoa
VerA.web mit Online-Anmeldung benutzt OSIAM zur sicheren Identitätsverwaltung.
Eine funktionierende OSIAM-Installation ist Voraussetzung für den Betrieb von
VerA.web mit Online-Anmeldung; sollten Sie jedoch noch keine OSIAM-Instanz bei
Ihnen betreiben wird eine für VerA.web taugliche Kurzinstallation erläutert.
OSIAM kann auf einem separaten System oder zusammen mit VerA.web core laufen
und benötigt eine eigene Datenbank, die aber im selben PostgreSQL-Cluster
liegen kann (und, bei Installation auf demselben Server, sollte). OSIAM für
VerA.web ist ein reines „Backend“ ohne Weboberfläche.

Die Online-Anmeldung wird üblicherweise auf einem separaten System
installiert, da auf diese Komponente durch Dritte über das Internet
zugegriffen werden soll (im Gegensatz zu VerA.web core, auf welchem
sich nur Ihre Sachbearbeiter einloggen). Sie verfügt über keine eigene
Datenbank und greift auf OSIAM und die VerA.web REST-API zu.
\fi% oa

Es wird empfohlen, nur mit der Administration der Systeme betreuten
Personen Zugriff auf die Systeme auf Shell-Ebene zu gewähren, und
ggfs. weitere geeignete Maßnahmen zur Absicherung zu treffen.

\subsection{Die Rolle der REST-API}\label{subsec:intro-restapi}

Die Aufrufe der REST-API werden durch HTTP Basic Authentication mit einem
(Maschinen‑)Benutzernamen und Paßwort gesichert, welche bei den aufrufenden
Anwendungen (der Veranstaltungsmanagementsoftware und, sofern vorhanden, der
Online-Anmeldung) hinterlegt wird; die REST-API wird niemals direkt durch
einen Menschen bedient. Es wird standardmäßig der Benutzer \texttt{veraweb}
mit dem Paßwort \texttt{veraweb} verwendet; eine Änderung der Zugangsdaten
ist \emph{dringend empfohlen}, um die Betriebssicherheit zu gewährleisten;
an den entsprechenden Stellen in dieser Anleitung finden Sie Hinweise hierzu.

\section{Systemvoraussetzungen}\label{sec:requirements}

\ifoa

Die Systemvoraussetzungen unterscheiden sich je nachdem wie Sie die
Komponenten trennen. Im folgenden werden empfohlene Werte für zwei
mögliche Installationsvarianten angegeben (OSIAM getrennt oder mit
auf dem core-System); die Online-Anmeldung („OA“) wird als getrenntes
System ausgeführt, bei einer Zusammenlegung mit dem core-System
sind die Werte in der Spalte „empfohlen“ als Minimum zu verstehen.

\begin{tabular}{| r || l | l |}\hline
                 & Minimum    & empfohlen\\\hline\hline
 CPU je VM       & 1          & 1\\\hline
 RAM OSIAM       & 960 MiB    & 1536 MiB\\\hline
 RAM core        & 512 MiB    & 1024 MiB, besser mehr\\\hline
 RAM zusammen    & 1024 MiB   & 2048 MiB, besser mehr\\\hline
 RAM OA          & 512 MiB    & 768 MiB\\\hline
 HDD OSIAM       & 2 GB       & 4 GB\\\hline
 HDD core        & 2 GB       & 8 GB\\\hline
 HDD zusammen    & 3 GB       & 10 GB\\\hline
 HDD OA          & 1½ GB      & 3 GB\\\hline
 Betriebssystem  & Debian 7   & Debian 7 oder 8\Hair\textsuperscript{\ref{fn:jessie}}\\\hline
 Webserver       & Apache 2.2 & Apache 2.2 oder 2.4\Hair\textsuperscript{\ref{fn:apache}}\\\hline
 Java Runtime    & OpenJDK 7  & OpenJDK 7 oder 8\\\hline
\end{tabular}

\else% !oa
% Voraussetzungen für VerA.web ohne Online-Anmeldung:
\begin{tabular}{| r || l | l |}\hline
                 & Minimum    & empfohlen\\\hline\hline
 CPU             & 1          & 1\\\hline
 Arbeitsspeicher & 512 MiB    & 1 GiB, besser mehr\\\hline
 Festplatte      & 2 GB       & 8 GB\\\hline
 Betriebssystem  & Debian 7   & Debian 7 oder 8\Hair\textsuperscript{\ref{fn:jessie}}\\\hline
 Webserver       & Apache 2.2 & Apache 2.2 oder 2.4\Hair\textsuperscript{\ref{fn:apache}}\\\hline
 Java Runtime    & OpenJDK 7  & OpenJDK 7 oder 8\\\hline
\end{tabular}
\fi% !oa

\subsection{LDAP-Verzeichnisdienst}\label{subsec:req-ldap}

Sie benötigen ein LDAP-Verzeichnis, in dem Sie die Zugänge (user
accounts) für die Sachbearbeiter, die VerA.web benutzen sollen,
sowie Administratorenzugänge pflegen. Hierfür wird OpenLDAP empfohlen.

Die Einrichtung eines solchen LDAP-Verzeichnisses ist aktuell
außerhalb des Rahmens dieser Installationsanleitung.

Für eine kleine Site ist es möglich, den LDAP-Server (Debian-Pakete
\texttt{slapd} und \texttt{ldap-utils} sowie, als einfach bedienbare
Oberfläche, \texttt{ldap-account-manager}) auf demselben Server wie
die interne Veranstaltungs- und Adreßverwaltung laufenzulassen; dies
erhöht die Systemvoraussetzungen nicht nennenswert.

\subsection{Interne Veranstaltungs- und Adreßverwaltung}\label{subsec:req-core}

Zum Betrieb von VerA.web core empfehlen wir eine eigenständige VM
\ifoa
je Komponente (core, Online-Anmeldung; OSIAM kann auf dem core-System
mitinstalliert werden, wenn es nur für VerA.web verwendet wird)
\fi% oa
mit Debian Linux 7 „wheezy“ als Betriebssystem. Zwar \emph{kann}
die Software auch mit einer anderen Debian-basierten Distribution
(z.B. Ubuntu 14.04 „trusty“) oder einer anderen Java/Tomcat-Umgebung
betrieben werden, allerdings geht diese Installationsanleitung von
der empfohlenen Systemumgebung aus. Eine graphische Oberfläche auf
dem Server wird zum Betrieb nicht benötigt. Eine Installation mit
Debian 8 „jessie“ wird in einer späteren Version dieser Anleitung
berücksichtigt werden.%XXX TODO

\ifoa\else% !oa
Für VerA.web core benötigen Sie mindestens 512 MiB Arbeitsspeicher;
1 GiB oder mehr wird empfohlen. Wir empfehlen mindestens 2 GiB Festplatte,
besser 8 GiB.
\fi% !oa

In dieser Anleitung sowie den Systemvoraussetzungen wird weiterhin
angenommen, daß der notwendige Datenbankserver (PostgreSQL) auf
derselben VM wie die interne Veranstaltungs- und Adreßverwaltung läuft;
falls nicht ändern Sie bitte an den relevanten Stellen den Hostnamen,
ggfs. Port, und Datenbanknamen in den Konfigurationsdateien.
\ifoa
Der PostgreSQL-Datenbankserver für OSIAM muß hingegen aus Gründen der
Einfachheit auf demselben System wie der OSIAM-Anwendungsserver laufen.
\fi% oa

Desweiteren benötigen Sie den Apache 2 Webserver (idealerweise mit
dem \texttt{prefork} MPM; in dieser Anleitung gehen wir von einem
Setup mit Apache 2.2\Hair\footnote{\label{fn:apache}Debian 7 „wheezy“
wird mit Apache 2.2 ausgeliefert, Debian 8 „jessie“\Hair\textsuperscript{\ref{fn:jessie}}
mit Apache 2.4} auf Debian wheezy\Hair\footnote{\label{fn:jessie}Diese
Installationsanleitung beschreibt aktuell nur den Betrieb unter
Debian 7 „wheezy“.} aus) und \texttt{mod\_jk} sowie den
Applikationsserver Tomcat 7 mit der Java™ 7 Laufzeitumgebung (OpenJDK)
ohne GUI (headless).
\ifoa
Es wird je eine Installation des Webservers pro VM benötigt, da in
der von uns empfohlenen Installationemethode die Komponenten auch
untereinander ausschließlich verschlüsselt und sicher über HTTPS
miteinander kommunizieren.
\fi% oa

\Warning \emph{Achtung:} Java™ 6 oder älter wird nicht unterstützt
und führt während der Installation zu Fehlern!

Wir setzen unsere Systeme üblicherweise aus Sicherheitsgründen so auf,
daß HTTPS-Verbindungen im Apache Webserver terminiert werden und über
das AJP-Protokoll von diesem an Tomcat weitergereicht werden. Sämtliche
SSL/TLS-Keys, -Features und weitere sicherheitsrelevante Einstellungen
richten wir im Apache Webserver ein. Sie \emph{können} Tomcat auch direkt
über HTTP ansprechen, dies widerspricht jedoch unserem empfohlenen Setup.

Die Installation des Webservers (SSL/TLS-Zertifikat, Zugangskontrollen,
falls nötig vhosts) liegt außerhalb des Rahmens dieses Dokuments. Es
wird davon ausgegangen, daß Apache so weit eingerichtet wird, daß er
statische Webseiten ausliefern kann; in diesem Dokument wird nur die
Integration mit dem Veranstaltungsmanagement beschrieben. (Vergessen
Sie bei Debian \texttt{sudo a2enmod ssl} nicht.)

\begin{minipage}{\textwidth}
\hypertarget{manual:aptget}{}
Auf einem Debian 7 „wheezy“-System können Sie den Datenbankserver,
die Java™-Laufzeitumgebung, den Applikationsserver und den Webserver
wie folgt lokal installieren:

\begin{lstdump}[language=sh]{Pakete installieren}
sudo apt-get install postgresql openjdk-7-jre-headless tomcat7 \
    apache2-mpm-prefork apache2 libapache2-mod-jk
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Bitte achten Sie darauf, daß keine ältere Version des JDK verwendet
wird (hier werden die in „wheezy“ vorhandenen älteren Versionen
deinstalliert):

\begin{lstdump}[language=sh]{altes Java entfernen}
sudo apt-get purge openjdk-6-jre-headless gcj-4.6-jdk gcj-4.7-jdk
\end{lstdump}
\end{minipage}

\hypertarget{manual:distro}{}
Zur Installation bzw. Upgrade müssen Sie folgende Dateien („die
VerA.web-core Distribution“) von tarent beziehen:\keinumbruch

\begin{itemize}
 \item{\texttt{veraweb-core-\vwiaverssw{}-files.tgz}
  \emph{oder} \texttt{veraweb-core-\vwiaverssw{}-files.zip}
  \\(der Inhalt dieser Dateien ist identisch, nur anders verpackt)}
\ifoa
 \item{\texttt{online-anmeldung-\vwiaverssw{}-files.tgz}
  \emph{oder} \texttt{online-anmeldung-\vwiaverssw{}-files.zip}
  \\(diese beiden Dateien sind ebenfalls identisch)}
\fi% oa
 \item{\texttt{veraweb-core-\vwiaverssw{}.war}}
 \item{\texttt{rest-api-\vwiaverssw{}.war}}
\ifoa
 \item{\texttt{online-anmeldung-\vwiaverssw{}.jar}}
\fi% oa
\end{itemize}

Bitte wenden Sie sich hierzu an unseren Produktvertrieb.

Die Installation wird als regulärer Benutzer, der sich vermittels
des Programms \texttt{sudo} Superuserrechte verschaffen kann,
durchgeführt, kann aber auch als \texttt{root} durchlaufen werden.

\ifupgradeanleitung
\else

\ifoa
\section{Installation OSIAM-System}\label{sec:setup-osiam}

Dieser Abschnitt gliedert sich in zwei Teile: die generelle Installation
von OSIAM, sofern Sie noch keine laufende OSIAM-Installation haben, und
die Registration der VerA.web Online-Anmeldung beim OSIAM-Server.

\begin{minipage}{\textwidth}
Kopieren Sie bitte zunächst \texttt{online-anmeldung-\vwiaverssw{}-files.tgz}
auf den OSIAM-Server, entpacken Sie diese Datei und wechseln in das
Unterverzeichnis mit den OSIAM-Dateien:

\begin{lstdump}[language=sh]{Entpacken OSIAM}
tar -xzf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files/osiam
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Installieren Sie nun einige notwendige Pakete, sofern noch nicht vorhanden:

\begin{lstdump}[language=sh]{Pakete OSIAM}
sudo apt-get install curl mksh
\end{lstdump}
\end{minipage}

\subsection{OSIAM-Server Schnellinstallation}\label{subsec:setup-osiam-fast}

Diesen Abschnitt können Sie \hyperlink{subsec:setup-osiam-register}
{überspringen}, falls Sie bereits eine lauffähige OSIAM-Installation
besitzen; demfalls stellen Sie bitte sicher, daß auf die vorhandene
Installation über HTTPS zugegriffen werden kann.

VerA.web benötigt aktuell OSIAM auth-server und resource-server in
der Version \vwiaversosiam{} (enthalten in der OSIAM Distribution
Version \vwiaversodist{}) und ist \emph{nur} mit dieser Version
getestet worden.
\\% XXX TODO besser in Upgradeanleitung, sobald es die gibt
\Warning \emph{Achtung:} OSIAM Server 2.3 und neuer können
nicht von VerA.web-Versionen bevor 1.6.34 angesprochen werden.

Sofern Sie noch nicht über eine OSIAM-Installation besitzen, bereiten
Sie bitte zunächst das System wie \hyperlink{manual:aptget}{oben}
beschrieben vor, indem Sie PostgreSQL, OpenJDK, Tomcat und Apache 2
installieren und alte Java-Versionen deinstallieren.

\begin{minipage}{\textwidth}
In der Apache-Konfiguration äquivalent zu \nameref{subsec:setup-core-apache}
mit \texttt{mod\_jk} sind folgende vhost-Einträge für OSIAM nötig:

\begin{lstdump}{AJP workers OSIAM}
JkMount /osiam-auth-server* ajp13_worker
JkMount /osiam-resource-server* ajp13_worker
\end{lstdump}
\end{minipage}

Schützen Sie das System bitte firewalltechnisch oder mit Hilfe von
Apache-Direktiven so, daß Zugriffe auf diese beiden Pfade (oder den
gesamten OSIAM-Server) nur von der lokalen Maschine (mit vollem
Hostnamen, also ggfs. über die externe IP, nicht \texttt{127.0.0.1}),
dem core/REST-API-System und der Online-Anmeldung möglich sind.

\begin{minipage}{\textwidth}
Legen Sie jetzt den Datenbankbenutzer und die Datenbank an:

\begin{lstdump}[language=sh]{DB erstellen OSIAM}
sudo -u postgres createuser -D -P -R -S ong
sudo -u postgres createdb -E UTF-8 -O ong -T template0 ong
\end{lstdump}

Falls Sie hierbei ein anderes Paßwort als \texttt{ong} für
den Datenbanknutzer vergeben müssen Sie die Angaben in
\texttt{auth-server.properties} und \texttt{resource-server.properties}
entsprechend anpassen.
\end{minipage}

\begin{minipage}{\textwidth}
In den Dateien \texttt{auth-server.properties} und
\texttt{resource-server.properties} ersetzen Sie bitte an allen
Stellen \texttt{HOSTNAME-OSIAM} durch den vollen Hostnamen des
OSIAM-Servers. Danach installieren Sie diese:

\begin{lstdump}[language=sh]{/etc/osiam fuellen}
sudo mkdir -p /etc/osiam
sudo chown 0:tomcat7 /etc/osiam
sudo chmod 2750 /etc/osiam
sudo install -c -o 0 -g tomcat7 -m 640 *.properties /etc/osiam/
\end{lstdump}
\end{minipage}

Folgen Sie bitte der allgemeinen \nameref{subsec:allg-tomcat} und
den spezifischen Anweisungen für OSIAM. Danach starten Sie den
Tomcat-Anwendungsserver auf jeden Fall neu, bevor Sie mit der
Installation von OSIAM fortfahren, damit die neuen Einstellungen
direkt verfügbar sind.

\begin{minipage}{\textwidth}
Nun müssen Sie die OSIAM-Installationsdateien herunterladen
und installieren:

\begin{lstdump}[language=sh]{OSIAM herunterladen}
curl -o osiam-auth-server.war https://maven-repo.evolvis.org/releases/org/osiam/auth-server/〈\lstdumpesc{\vwiaversosiam}〉/auth-server-〈\lstdumpesc{\vwiaversosiam}〉.war
curl -o osiam-resource-server.war https://maven-repo.evolvis.org/releases/org/osiam/resource-server/〈\lstdumpesc{\vwiaversosiam}〉/resource-server-〈\lstdumpesc{\vwiaversosiam}〉.war
sudo install -c -o 0 -g 0 -m 644 *.war /var/lib/tomcat7/webapps/
\end{lstdump}
\end{minipage}

Warten Sie einige Zeit, bis beide Artefakte deployt wurden; in
\texttt{/var/log/tomcat7/catalina.out} befinden sich die Logs.

\begin{minipage}{\textwidth}
Nach einem erfolgreichen Start können Sie prüfen, ob sich der
Server ansprechen läßt:

\begin{lstdump}[language=sh]{OSIAM ansprechen}
curl https://$(hostname -f)/osiam-resource-server/ServiceProviderConfigs; echo
\end{lstdump}

Sie sollten hier eine Ausgabe im JSON-Format sehen, die erwähnt,
daß OAuth 2 verwendet wird.
\end{minipage}

Im Abschnitt \nameref{sec:links} finden Sie einen Link auf die
offizielle OSIAM-Installationsanleitung mit weitergehenden
Informationen, u.a. wie man den Standardzugang ändert.

\subsection{Registrierung von VerA.web mit OSIAM}\label{subsec:setup-osiam-register}

Die Datei \texttt{register-veraweb-into-osiam.sh} muß nun
gegebenenfalls angepaßt werden: rund um Zeile 30 finden Sie
Standardwerte für den Datenbanknamen (\texttt{ong}) und den
OSIAM-Standardzugang, die Sie ändern müssen, falls Ihr System
dem nicht entspricht, sowie das Paßwort, mit dem sich später
die VerA.web Online-Anmeldung bei OSIAM einloggen kann, und
welches Sie auf jeden Fall ändern sollten.

\begin{minipage}{\textwidth}
Nun rufen Sie dieses Skript bitte auf:

\begin{lstdump}[language=sh]{register VW into OSIAM}
sudo mksh register-veraweb-into-osiam.sh
\end{lstdump}
\end{minipage}

Das Skript fügt zunächst die VerA.web-spezifischen Daten
zum SCIM-Schema hinzu und benötigt daher Datenbankzugriff
(weshalb die Datenbank für die vereinfachte Installation
auf demselben Server laufen muß wie OSIAM selbst) und
registriert danach über die REST-Schnittstelle von OSIAM
VerA.web als Client.

\begin{minipage}{\textwidth}
Nach erfolgreichem Abschluß können Sie nun die
Installationsdateien löschen:

\begin{lstdump}[language=sh]{Aufraeumen OSIAM}
cd ../..
rm -rf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉*
\end{lstdump}
\end{minipage}

\fi% oa

\section{Installation internes VerA.web-System}\label{sec:setup-int}

Nach dem erfolgten \hyperlink{manual:aptget}{Aufsetzen des Systems
mit den Basispaketen} und dem Download der Installationspakete wird
im folgenden die Installation der einzelnen Komponenten beschrieben.

Bevor Sie beginnen informieren Sie sich bitte über die LDAP-Kürzel
(Unix-Loginnamen) der Nutzer, die zunächst einen Administratorenzugang
zu VerA.web erhalten sollen (idealerweise sollten Sie sich selbst
ebenfalls einen solchen Zugang einrichten). Diese benötigen wir später.

\subsection{Dateien VerA.web-core und REST-API}\label{subsec:setup-core-files}

\nameref{subsec:allg-entpacken} Sie zunächst die Distribution.
Dann müssen Sie einige Konfigurationsanpassungen vornehmen.

Passen Sie in der \texttt{veraweb.properties} bitte den Hostnamen
in der \texttt{vwor.endpoint}-Eigenschaft an (ersetzen Sie
\texttt{HOSTNAME-REST} durch den FQDN des Servers, über den die
REST-API erreichbar ist, üblicherweise also den Hostnamen vom
core-System) und tragen Sie die Zugangsdaten für die REST-API
(siehe \nameref{subsec:intro-restapi}) als \texttt{vwor.auth.user}
und \texttt{vwor.auth.password} ein.

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis an, in dem Sie die hochgeladenen
Bilddateien speichern wollen (wir empfehlen ein Unterverzeichnis,
sodaß es später möglich ist, weitere Strukturen aufzubauen), und
machen Sie es für den Tomcat-Nutzer schreibbar:

\begin{lstdump}[language=sh]{Uploaded anlegen}
sudo mkdir -p /var/lib/veraweb/uploaded
sudo chgrp tomcat7 /var/lib/veraweb/uploaded
sudo chmod 2770 /var/lib/veraweb/uploaded
\end{lstdump}

Falls Sie ein anderes Verzeichnis als das obige verwenden passen
Sie bitte nun die Eigenschaft \texttt{filesLocation} in der Datei
\texttt{vwor.properties} an; bitte achten Sie darauf, daß der
Pfad \emph{unbedingt} mit einem Schrägstrich („/“) enden muß!
\end{minipage}

\subsection{LDAP-Anbindung VerA.web-core}\label{subsec:setup-core-ldap}

Bearbeiten Sie die Datei \texttt{config\_ldap\_access.xml}, indem Sie
dort die nötigen Angaben eintragen, um Ihren LDAP-Verzeichnisdienst
benutzen zu können. Dies sind insbesondere die folgenden Parameter:\keinumbruch

\begin{itemize}
 \item{\texttt{ldapurl}: LDAP-Server, zum Beispiel:
  \texttt{ldaps://dc.lan.tarent.de:7636}}
 \item{\texttt{ldapbasedn}: Basis-DN des Verzeichnisses}
 \item{\texttt{ldaprelative}: LDAP-Zweig, in dem sich Benutzerkonten
  befinden; Standardwert: \texttt{ou=Users}}
 \item{\texttt{ldapuser} und \texttt{ldappwd}: falls nötig, ein
  Benutzer, mit dem sich die Software im LDAP authentifizieren kann,
  sofern die Zugriffe nicht anonym möglich sind}
 \item{\texttt{ldapuserobjectclass}: Objektklasse, die Benutzerkonten
  innehaben; Standardwert: \texttt{person}}
\end{itemize}

\subsection{Datenbank VerA.web-core}\label{subsec:setup-core-db}

Im folgenden Abschnitt wird die Datenbank für VerA.web mit dem
Datenbankschema sowie den Stammdaten und Fachadministratorenzugängen
eingerichtet. Hierbei wird prinzipiell von einer auf demselben Server
im Debian-Stil installierten und betriebsbereiten PostgreSQL-Datenbank
ausgegangen.

\begin{minipage}{\textwidth}
Legen Sie zunächst den Datenbankbenutzer und die Datenbank an:

\begin{lstdump}[language=sh]{DB erstellen}
sudo -u postgres createuser -D -P -R -S veraweb
sudo -u postgres createdb -E UTF-8 -O veraweb -T template0 veraweb
\end{lstdump}
\end{minipage}

Falls Sie eine Datenbank auf einem anderen Server verwenden, lassen
Sie den dortigen DBA bitte folgendes anlegen: eine Rolle mit
Login-Berechtigung (hier: Benutzer „veraweb“, Paßwort „veraweb“)
und eine Datenbank (hier: Name „veraweb“) in der Kodierung UTF-8,
die der o.a. Rolle gehört.

Falls Sie als Datenbankpaßwort \emph{nicht} \texttt{veraweb} benutzen oder
anderweitig von den Standardvorgaben (lokale PostgreSQL, Standardport 5432,
Datenbankname \texttt{veraweb}, Datenbankbenutzer \texttt{veraweb}) müssen
Sie noch die Werte für \texttt{serverName}, \texttt{portNumber},
\texttt{databaseName}, \texttt{user} und/oder \texttt{password} in der
Datei \texttt{config\_database\_pools.xml} anpassen.

Nun können Sie das Datenbankschema anlegen und die Stammdaten importieren.
Bei letzteren haben Sie die Möglichkeit, statt der vorgegebenen Stammdaten
lediglich einen Minimalsatz zu laden (\texttt{stammdaten-colours.sql} statt
\texttt{stammdaten-full.sql}).

\begin{minipage}{\textwidth}
Loggen Sie sich zunächst auf der Datenbank ein:

\begin{lstdump}[language=sh]{DB Login}
psql -U veraweb -h 127.0.0.1 -W veraweb
\end{lstdump}
\end{minipage}

Falls Sie andere Werte für Datenbankserver, Port, Benutzer oder Datenbankname
verwenden müssen Sie diese hier selbstverständlich anpassen.

\begin{minipage}{\textwidth}
Dann setzen Sie folgende SQL-Kommandos ab:

\begin{lstdump}[language=SQL]{DB Schema Init Beginn}
\i schema.sql
SELECT serv_verawebschema(1);
\i stammdaten-full.sql
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Nun müssen Sie diejenigen LDAP-Benutzer, die initial als Administratoren
agieren sollen, der Software bekanntgeben. Setzen Sie hierzu für jeden
LDAP-Benutzer eine Zeile entsprechend der folgenden ab, wobei Sie statt
\texttt{vnnnnn} das LDAP-Kürzel (Unix-Username) des jeweiligen Benutzers
einseitzen:

\begin{lstdump}[language=SQL]{DB Admin-User anlegen}
INSERT INTO veraweb.tuser (fk_orgunit, username, role)
    VALUES (NULL, 'vnnnnn', 5);
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Schließlich müssen einige Sequenzen initialisiert und das Datenbankschema
auf die jeweils aktuelle Version aktualisiert werden; danach können Sie
den \texttt{psql}-Client wieder verlassen:

\begin{lstdump}[language=SQL]{DB Schema Init Ende}
SELECT veraweb.serv_build_sequences();
\i upgrade.sql
\q
\end{lstdump}
\end{minipage}

\subsection{Webserver VerA.web-core und REST-API}\label{subsec:setup-core-apache}

\begin{minipage}{\textwidth}
Aktivieren Sie zunächst das \texttt{jk}-Modul, welches benutzt wird,
um vermittels des AJP-Protokolls Anfragen an den Webserver an Tomcat
durchzureichen:

\begin{lstdumpx}[language=sh]
sudo a2enmod jk
\end{lstdumpx}
\end{minipage}

\begin{minipage}{\textwidth}
Dann tragen Sie folgende Zeilen in die vhost-Konfiguration ein:

\begin{lstdump}{AJP workers}
JkMount /veraweb* ajp13_worker
JkMount /vwor* ajp13_worker
\end{lstdump}
\end{minipage}

Diese Zeilen sollte z.B. in der mitgelieferten Standardkonfiguration
des Webservers unter Debian
\texttt{/etc/apache2/sites-available/default-ssl}, welche mit einem
\texttt{sudo a2ensite default-ssl} aktiviert wird, vor das
\texttt{DocumentRoot /var/www} geschrieben werden.

Desweiteren richten wir üblicherweise eine Weiterleitung auf die Startseite
sowie eine von HTTP nach HTTPS ein; beide an der jeweils selben Stelle;
anstelle von \texttt{veraweb.lan.tarent.de} tragen Sie bitte den Namen
Ihres Servers (FQDN) ein.

\begin{minipage}{\textwidth}
In \texttt{/etc/apache2/sites-available/default-ssl} (HTTPS):

\begin{lstdump}{Redirect von /}
RedirectMatch 301 ^/*$ https://veraweb.lan.tarent.de/veraweb/
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Desweiteren \texttt{/etc/apache2/sites-available/default} (HTTP):

\begin{lstdump}{Redirect HTTP -> HTTPS}
RedirectMatch 301 . https://veraweb.lan.tarent.de/veraweb/
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Abschließend starten Sie bitte den Webserver neu:

\begin{lstdump}[language=sh]{Apache neustarten}
sudo service apache2 stop
sudo service apache2 start
\end{lstdump}
\end{minipage}

Ein Aufruf von z.B. \texttt{https://veraweb.lan.tarent.de/veraweb/} sollte
nun einen Fehlercode 503 „Service Temporarily Unavailable“ zurückliefern.
Im \texttt{/var/log/apache2/mod\_jk.log} sollten Sie eine Notiz
„\texttt{ajp\_send\_request::jk\_ajp\_common.c (1630): (ajp13\_worker)
connecting to backend failed. Tomcat is probably not started or is listening
on the wrong port}“ sehen. Hingegen sollte unter der URL
\texttt{https://veraweb.lan.tarent.de/index.html} weiterhin die Standardseite
des Webservers zu sehen sein.

\subsection{Applikationsserver VerA.web-core und REST-API}\label{subsec:setup-core-tomcat}

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis \texttt{/etc/veraweb} mit einem
Unterverzeichnis \texttt{l10n} an und kopieren Sie die Konfigurations-
und Sprachdateien dorthin:

\begin{lstdump}[language=sh]{/etc/veraweb fuellen}
sudo mkdir -p /etc/veraweb/l10n
sudo chown 0:tomcat7 /etc/veraweb
sudo chmod 2750 /etc/veraweb
sudo install -c -o 0 -g tomcat7 -m 640 config_*.xml /etc/veraweb/
sudo install -c -o 0 -g tomcat7 -m 640 *.properties /etc/veraweb/
sudo install -c -o 0 -g 0 -m 644 l10n/*.resource /etc/veraweb/l10n/
\end{lstdump}
\end{minipage}

Richten Sie bitte Tomcat wie im Kapitel \nameref{subsec:allg-tomcat}
beschrieben ein und starten ihn neu.

\begin{minipage}{\textwidth}
Deployen Sie die VerA.web-Anwendung und die REST-API in den Applikationsserver:

\begin{lstdump}[language=sh]{Tomcat deploy}
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo install -c -o 0 -g 0 -m 644 ../vwor.war /var/lib/tomcat7/webapps/
\end{lstdump}
\end{minipage}

Jetzt können Sie z.B. über \texttt{https://veraweb.lan.tarent.de/veraweb/}
auf das Veranstaltungsmanagement zugreifen und sich einloggen. Falls
hierbei Fehler auftauchen prüfen Sie bitte den LDAP-Zugriff und eventuell
in \texttt{/var/log/tomcat7/catalina.out} auftauchende Meldungen.

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr; diese können nun entfernt werden:

\begin{lstdump}[language=sh]{Aufraeumen}
cd ..
rm -rf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉* veraweb.war vwor.war
\end{lstdump}
\end{minipage}

Damit ist die Installation abgeschlossen.

\fi% !ifupgradeanleitung

\ifoa\else% !ifoa - XXX FIXME Upgrade für OA schreiben

\section{Upgrade internes VerA.web-System}\label{sec:upgrade-int}

Im folgenden wird das Upgrade der einzelnen Komponenten
nach erfolgtem Download der Installationspakete beschrieben.

Hierbei wird davon ausgegangen, daß VerA.web gemäß
 \ifupgradeanleitung
  unserer Installationsanleitung
 \else
  dem Abschnitt \nameref{sec:setup-int} der vorliegenden Anleitung,
  oder einer ähnlich aufgebauten Anleitung einer Vorgängerversion,
 \fi
auf einem Debian Linux 7 „wheezy“-System eingerichtet wurde.
Installationen gemäß früherer Anleitungen der tarent solutions GmbH,
die abweichende Verfahren z.B. in der Webserverkonfiguration benutzten,
werden ebenfalls unterstützt, benötigen aber ggfs. Anpassungen.

Falls Sie weitergehende Anpassungen, z.B. an den Velocity-Templates
für die Webseiten, vorgenommen haben müssen Sie die Änderungen
manuell nachpflegen.

\ifvwconfigsinetcalready\else
Upgrades von VerA.web 1.3.15 und neuer werden unterstützt. Bei einem
Upgrade von einer älteren Version als 1.5 (also 1.3.15 und 1.4) ist
generell mehr Vorsicht walten zu lassen.
\fi% !ifvwconfigsinetcalready

Für weitergehenden Installationssupport, insbesondere bei Systemen,
die nicht nach dieser oder einer ähnlichen Anleitung installiert
wurden, kontaktieren Sie bitte unseren Produktvertrieb oder, falls
Sie bereits einen bestehenden Wartungsvertrag haben, unser Service-
und Wartungsteam.

\subsection{Dateien VerA.web-core}\label{subsec:upgrade-core-files}

\nameref{subsec:allg-entpacken} Sie zunächst die Distribution.

\begin{minipage}{\textwidth}
Falls das Verzeichnis \texttt{/etc/veraweb/l10n} noch nicht existiert,
legen Sie es an. Kopieren Sie dann die aktuellen Sprachdateien dorthin:

\begin{lstdump}[language=sh]{/etc/veraweb/l10n fuellen}
sudo mkdir -p /etc/veraweb/l10n
sudo install -c -o 0 -g 0 -m 644 l10n/*.resource /etc/veraweb/l10n/
\end{lstdump}
\end{minipage}

\ifvwconfigsinetcalready
Diese Fassung der Upgradeanleitung geht davon aus, daß Sie die
Konfigurationsdateien bereits in \texttt{/etc/veraweb} haben
(VerA.web 1.4.3.5 oder 1.5.1.4 und neuer).
\else% !ifvwconfigsinetcalready

\subsection{Upgrade von Versionen vor 1.5.1.4}\label{subsec:upgrade-1514}

Falls Sie von einer VerA.web-Version, die die Konfigurationsdateien
noch nicht in \texttt{/etc/veraweb} ablegt, aktualisieren, müssen Sie
dort zunächst (nur) die gegenüber den in der \texttt{veraweb.war}
ausgelieferten Dateien angepaßten Konfigurationen aus dem Verzeichnis
\texttt{/var/lib/tomcat7/webapps/veraweb/OCTOPUS/config\_*.xml}
Ihrer alten Installation hinkopieren.

Das neue Konfigurationsschema wurde mit VerA.web 1.5.1.4 eingeführt,
war jedoch bereits in VerA.web 1.4.3.5 (und \emph{nur} dieser Version
aus dem 1.4-Zweig) verfügbar.

Je nach Datei gibt es hierbei einige Besonderheiten zu beachten:\keinumbruch

\begin{itemize}
 \item{\texttt{config.xml}: kann nicht in \texttt{/etc/veraweb}
  überschrieben werden; Änderungen früherer Versionen müssen nun
  in \texttt{config\_logging.xml} und \texttt{config\_main.xml}
  vollzogen werden}
 \item{\texttt{config\_override.xml}: standardmäßig leer, erlaubt
  eigene Anpassungen, ohne daß gleich die ganze andere config\_*.xml
  kopiert werden muß}
 \item{\texttt{config\_worker\_tasks.xml}: ändert sich mit jeder
  VerA.web-Version; Anpassungen müssen übernommen werden}
\end{itemize}

Die anderen Dateien erhalten generell wenig Anpassungen, allerdings
wurde vor VerA.web 1.5 das Encoding auf UTF-8 geändert. Es wird
empfohlen, an diesen Dateien \dash außer \texttt{config\_ldap\_access.xml}
und ggfs. \texttt{config\_database\_pools.xml} \dash nichts zu ändern,
sondern \texttt{config\_override.xml} anzupassen, wenn möglich.

\emph{Tip:} Entpacken Sie (mit \texttt{unzip}) \texttt{veraweb.war}
aus Ihrer vor dem Upgrade installierten Version und vergleichen Sie
die Dateien aus dem Archiv mit den installierten Dateien, z.B. mit
\texttt{diff -u} oder einem graphischen Tool, um festzustellen, ob
Sie überhaupt Änderungen durchführen müssen, und wenn ja, welche.

Die Datei \texttt{config\_ldap\_access.xml} wurde in manchen
Versionen als \texttt{config\_ldap\_access.xml.dist} und/oder
als separater Download angeboten.
\fi% !ifvwconfigsinetcalready

\subsection{Upgrade von Versionen vor 1.6.32}\label{subsec:upgrade-1632}

Die neu hinzugekommene Komponente REST-API muß konfiguriert werden.

Passen Sie in der \texttt{veraweb.properties} bitte den Hostnamen
in der \texttt{vwor.endpoint}-Eigenschaft an (ersetzen Sie
\texttt{HOSTNAME-REST} durch den FQDN des Servers, über den die
REST-API erreichbar ist, üblicherweise also den Hostnamen vom
core-System) und tragen Sie die Zugangsdaten für die REST-API
(siehe \nameref{subsec:intro-restapi}) als \texttt{vwor.auth.user}
und \texttt{vwor.auth.password} ein.

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis an, in dem Sie die hochgeladenen
Bilddateien speichern wollen (wir empfehlen ein Unterverzeichnis,
sodaß es später möglich ist, weitere Strukturen aufzubauen), und
machen Sie es für den Tomcat-Nutzer schreibbar:

\begin{lstdump}[language=sh]{Uploaded anlegen}
sudo mkdir -p /var/lib/veraweb/uploaded
sudo chgrp tomcat7 /var/lib/veraweb/uploaded
sudo chmod 2770 /var/lib/veraweb/uploaded
\end{lstdump}

Falls Sie ein anderes Verzeichnis als das obige verwenden passen
Sie bitte die Eigenschaft \texttt{filesLocation} in der Datei
\texttt{vwor.properties} an; bitte achten Sie darauf, daß der
Pfad \emph{unbedingt} mit einem Schrägstrich („/“) enden muß!
\end{minipage}

\begin{minipage}{\textwidth}
Installieren Sie nun die Konfigurationsdateien für die REST-API:

\begin{lstdump}[language=sh]{/etc/veraweb/*.properties}
sudo install -c -o 0 -g tomcat7 -m 640 *.properties /etc/veraweb/
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Fügen Sie folgende Zeile der Apache-vhost-Konfiguration hinzu:

\begin{lstdump}{AJP Worker VWOR}
JkMount /vwor* ajp13_worker
\end{lstdump}
\end{minipage}

Starten Sie den Webserver neu, um die Änderungen wirksam zu machen.

Das Upgrade beïnhaltet die Installation der REST-API im Tomcat;
nehmen Sie bitte neben den allgemeinen auch die Einstellungen im
Kapitel \nameref{subsub:allg-tomcat-vwor} vor.

\subsection{Datenbank VerA.web-core}\label{subsec:upgrade-core-db}

\begin{minipage}{\textwidth}
Loggen Sie sich auf der Datenbank ein und starten Sie das Upgrade-SQL:

\begin{lstdump}[language=sh]{DB-Upgrade}
psql -U veraweb -h 127.0.0.1 -W veraweb <upgrade.sql
\end{lstdump}

Falls Sie andere Werte für Datenbankserver, Port, Benutzer oder Datenbankname
verwenden müssen Sie diese hier selbstverständlich anpassen.
\end{minipage}

\subsection{Applikationsserver VerA.web-core}\label{subsec:upgrade-core-tomcat}

\begin{minipage}{\textwidth}
Stoppen Sie bitte zunächst den Applikationsserver und löschen
ggfs. alle vorherigen VerA.web-Webapps (es ist vielleicht
sinnvoll, ein Backup zu machen):

\begin{lstdump}[language=sh]{Tomcat undeploy}
sudo service tomcat7 stop
sudo rm -rf /var/lib/tomcat7/webapps/veraweb*
sudo rm -rf /var/lib/tomcat7/webapps/vwor*
\end{lstdump}

Bitte beachten Sie, daß der Tomcat-Dienst ggfs. noch einige Sekunden
weiterläuft, nachdem der Stop-Befehl abgeschickt wurde; im Zweifelsfalls
können Sie mit \texttt{ps ax | grep java} prüfen, ob noch Prozesse laufen.
\end{minipage}

\begin{minipage}{\textwidth}
Dann starten Sie den Server wieder und deployen die VerA.web-Anwendung
und die REST-API:

\begin{lstdump}[language=sh]{Tomcat deploy}
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo install -c -o 0 -g 0 -m 644 ../vwor.war /var/lib/tomcat7/webapps/
sudo service tomcat7 start
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr; diese können nun entfernt werden:

\begin{lstdump}[language=sh]{Aufraeumen}
cd ..
rm -rf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉* veraweb.war vwor.war
\end{lstdump}
\end{minipage}

Damit ist das Upgrade abgeschlossen.

\fi% !ifoa - XXX FIXME

\section{Allgemeine Prozeduren}\label{sec:allg}

In diesem Kapitel finden Sie Prozeduren, die während der Installation
oder des Upgrades mehrfach verwendet werden, der Übersichtlichkeit
halber ein einzelne Unterkapitel ausgelagert. Auf sie wird weiter oben
in der Anleitung verwiesen.

\subsection{Entpacken}\label{subsec:allg-entpacken}

Vergewissern Sie sich zunächst, \hyperlink{manual:distro}{die
VerA.web-Installationsdateien} für die korrekte Version vollständig
erhalten zu haben, und kopieren Sie die Dateien auf den Server,
auf dem Sie das VerA.web-Veranstaltungsmanagement installieren
oder aktualisieren möchten. Alle weiteren Schritte werden auf
dem Server ausgeführt.

\begin{minipage}{\textwidth}
Benennen Sie die Applikationsdateien in später leicher zu
verwendende Namen um, entpacken Sie das Zusatzarchiv (im
Beispiel wird die Unix-Variante mit \texttt{.tgz} verwendet)
und wechseln Sie in das angelegte neue Verzeichnis:

\ifoa
\begin{lstdump}[language=sh]{Entpacken}
mv veraweb-core-〈\lstdumpesc{\vwiaverssw}〉.war veraweb.war
mv rest-api-〈\lstdumpesc{\vwiaverssw}〉.war vwor.war
tar -xzf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd veraweb-core-〈\lstdumpesc{\vwiaverssw}〉
tar -xzf ../online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
mv online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉/core/* .
\end{lstdump}
\else% !oa
\begin{lstdump}[language=sh]{Entpacken}
mv veraweb-core-〈\lstdumpesc{\vwiaverssw}〉.war veraweb.war
mv rest-api-〈\lstdumpesc{\vwiaverssw}〉.war vwor.war
tar -xzf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd veraweb-core-〈\lstdumpesc{\vwiaverssw}〉
\end{lstdump}
\fi% !oa
\end{minipage}

Die meisten enthaltenen Dateien werden im Verlauf der Einrichtung
verwendet; alle übrigen dienen als Beispiele, Vorlagen oder Hilfen
bei der Anpassung; zusätzlich ist das Benutzerhandbuch, welches
später in der Anwendung auch als Online-Hilfe verfügbar ist, als
\texttt{Benutzerhandbuch.pdf} mitgeliefert, sodaß Sie dieses bereits
vorab Ihren Sachbearbeitern zukommen lassen können.

\subsection{Tomcat-Einrichtung}\label{subsec:allg-tomcat}

Der folgende Abschnitt gilt für alle Tomcat-Applikationsserver,
die Sie im Rahmen der VerA.web-Installation einrichten. Darauf
folgen applikationsserverspezifische Einstellungen, die Sie nur
auf den jeweils angegebenen Systemen durchführen müssen.

Im folgenden wird von einer Standard-Debian-Installation ausgegangen;
passen Sie gegebenenfalls in \texttt{tomcat7} die Versionsnummer an.
Falls Sie nicht den in Debian paketierten Tomcat-Server verwenden
passen Sie bitte alle Anleitungen entsprechend Ihren lokalen Systemen an.

Nach einer Konfigurationsänderung müssen Sie \emph{auf jeden
Fall} den Applikationsserver neustarten. Es bietet sich an,
den gesamten Installationsprozeß bei ausgeschaltetem Server
durchzuführen, d.h. Sie stoppen ihn zu Beginn der Installation
und starten ihn am Ende erst wieder.

\begin{minipage}{\textwidth}
Zum Stoppen des Anwendungsservers benutzen Sie folgenden Befehl:

\begin{lstdump}[language=sh]{Tomcat stop}
sudo service tomcat7 stop
\end{lstdump}

Bitte beachten Sie, daß der Tomcat-Dienst ggfs. noch einige Sekunden
weiterläuft, nachdem der Stop-Befehl abgeschickt wurde; im Zweifelsfalls
können Sie mit \texttt{ps ax | grep java} prüfen, ob noch Prozesse laufen.
\end{minipage}

\begin{minipage}{\textwidth}
Zum Starten verwenden Sie äquivalent:

\begin{lstdump}[language=sh]{Tomcat start}
sudo service tomcat7 start
\end{lstdump}
\end{minipage}

\subsubsection{Tomcat überall}\label{subsub:allg-tomcat-allg}

Fügen Sie bitte am Ende der Datei \texttt{/etc/default/tomcat7} (oder
tomcat8; oder, falls Sie nicht Debian benutzen, an einer äquivalenten
Stelle) folgende Zeilen ein, um weitere JVM-Startoptionen zu setzen.

\begin{minipage}{\textwidth}
Diese Option setzen Sie bitte in jedem Fall, um eine korrekte
Kodierung von Dateien, Logs und internen Abläufen zu gewährleisten:

\begin{lstdump}[language=sh]{Tomcat Encoding setzen}
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Diese (Linux-spezifische) Option spart u.a. beim Starten Zeit,
indem sie eine nichtblockierende Quelle für Zufallszahlen auswählt:

\begin{lstdump}[language=sh]{Tomcat /dev/urandom}
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Nun bearbeiten Sie die Datei \texttt{/etc/tomcat7/server.xml}, um Tomcat
von HTTP auf AJP umzustellen. Suchen Sie zunächst folgenden Abschnitt
(eine einfache Suche nach „8080“ findet ihn) und kommentieren ihn aus,
indem Sie davor \texttt{<!‑‑} und dahinter \texttt{‑‑>} schreiben:

\begin{lstdump}[language=XML]{Tomcat HTTP-Connector}
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           URIEncoding="UTF-8"
           redirectPort="8443" />
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Dann suchen Sie folgenden auskommentierten Abschnitt (ca. 20 Zeilen weiter):

\begin{lstdump}[language=XML]{Tomcat AJP-Connector orig}
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
\end{lstdump}

Entfernen Sie hier die Kommentarzeichen und fügen Sie ein weiteres Attribut
hinzu, um Zugriffe nur vom lokalen System (durch den Webserver) zuzulassen:

\begin{lstdump}[language=XML]{Tomcat AJP-Connector wanted}
<Connector address="127.0.0.1" port="8009" protocol="AJP/1.3" redirectPort="8443" />
\end{lstdump}
\end{minipage}

\ifoa

\subsubsection{Tomcat OSIAM}\label{subsub:allg-tomcat-osiam}

\begin{minipage}{\textwidth}
Suchen Sie bitte in der Datei \texttt{/etc/tomcat7/catalina.properties}
die Zeile, die mit \texttt{shared.loader=} beginnt, und hängen Sie am
Ende der Zeile \texttt{,/etc/osiam} an; das könnte dann wie folgt aussehen:

\begin{lstdump}{Tomcat OSIAM shared.loader}
shared.loader=/var/lib/tomcat7/shared/classes,/var/lib/tomcat7/shared/*.jar,/etc/osiam
\end{lstdump}
\end{minipage}

Suchen Sie in der Datei \texttt{/etc/default/tomcat7} die Stelle, wo
\texttt{-Xmx128m} in den \texttt{JAVA_OPTS} gesetzt wird. Ändern Sie
diesen Wert auf \texttt{-Xms512m -Xmx1024m} falls die VM über mehr als
1280 MiB RAM verfügt, \texttt{-Xmx512m} andernfalls.

\fi% oa

\subsubsection{Tomcat REST-API}\label{subsub:allg-tomcat-vwor}

\begin{minipage}{\textwidth}
Fügen Sie bitte am Ende der Datei \texttt{/etc/tomcat7/context.xml} vor
das schließende \texttt{</Context>}-Tag folgendes Tag ein, um die
 \ifupgradeanleitung
  Datenbank VerA.web-core
 \else
  \nameref{subsec:setup-core-db}
 \fi
für die REST-API verfügbar zu machen:

\begin{lstdump}[language=XML]{Tomcat VWOR JDBC}
<Resource name="jdbc/vwonlinereg" auth="Container" type="javax.sql.DataSource"
 driverClassName="org.postgresql.Driver" username="veraweb" password="veraweb"
 url="jdbc:postgresql://localhost:5432/veraweb" maxActive="8" maxIdle="4" />
\end{lstdump}

Ersetzen Sie jeweils für „veraweb“ den Benutzernamen, das Paßwort und den
Datenbanknamen, die Sie verwendet haben, falls Sie von den Standardvorgaben
 \ifupgradeanleitung
  im Datenbankkapitel
 \else
  in Kapitel \ref{subsec:setup-core-db}
 \fi
abgewichen sind.
\end{minipage}

\begin{minipage}{\textwidth}
Fügen Sie bitte am Ende der Datei \texttt{/etc/tomcat7/tomcat-users.xml} vor
das schließende \texttt{</tomcat-users>}-Tag folgende Tags ein, setzen dabei
aber die von Ihnen gewählten Zugangsdaten der REST-API gemäß Kapitel
\ref{subsec:intro-restapi} ein:

\begin{lstdump}[language=XML]{Tomcat VWOR Auth}
<role rolename="veraweb" />
<user roles="veraweb" username="veraweb" password="veraweb" />
\end{lstdump}
\end{minipage}

\section{Weitere Fragen}\label{sec:outro}

Wenn Sie einen Service- und Wartungsvertrag mit uns abgeschlossen
haben, können Sie bei Fragen und Unklarheiten jederzeit gerne das
Service- und Wartungsteam kontaktieren.

Dazu erstellen Sie im Ticket-System OTRS ein neues Ticket. Den
genauen Ablauf dafür, Ihre OTRS-Zugangsdaten und einen Leitfaden
für die Erstellung eines Tickets finden Sie im Dokument
„OTRS\_Supportablauf\_Kundenname“, das Sie zu Beginn des
Wartungsvertrages von uns erhalten haben. Sollte Ihnen dieses
nicht mehr vorliegen, können Sie jederzeit über
\href{mailto:wartung@tarent.de}{wartung@tarent.de} eine Kopie
davon anfordern.

Falls Sie noch keinen Service- und Wartungsvertrag mit uns abgeschlossen
haben, kontaktieren Sie bitte unseren Produktvertrieb.

\section{Weitere Ressourcen und Links}\label{sec:links}

\begin{itemize}
 \item{\href{https://www.tarent.de/de/angebot/produkte/veraweb}{https://www.tarent.de/de/angebot/produkte/veraweb}
  \dash VerA.web Produktwebseite}
 \item{\href{https://www.tarent.de/de/angebot/service-wartung-hosting}{https://www.tarent.de/de/angebot/service-wartung-hosting}
  \dash Angebote von \scalerel*{\tarentlogo}{B} zu\\
  Service, Wartung und Hosting}
 \item{\href{https://evolvis.org/plugins/scmgit/cgi-bin/gitweb.cgi?p=veraweb/veraweb.git;a=blob_plain;f=src/main/webapp/doc/Benutzerhandbuch.pdf;hb=HEAD}{Benutzerhandbuch}
  (aktuelle Version, online)}
\ifoa
 \item{\href{https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md}{https://github.com/osiam/osiam/blob/master/docs/detailed-reference-installation.md}
  \dash Detaillierte OSIAM-Referenzinstallationsanleitung}
\fi% oa
\end{itemize}
