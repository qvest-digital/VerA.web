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
% Textabschnitt 3.1-3.6: Setup VerA.web-core

\section{Installation internes VerA.web-System}\label{sec:setup-int}

Nach dem erfolgten Aufsetzen des Systems mit den Basispaketen
und dem Download der Installationspakete wird im folgenden die
Installation der einzelnen Komponenten beschrieben.

\subsection{Dateien VerA.web-core und REST-API}\label{subsec:setup-core-files}

\input{text-extract.inc}
\input{text-1632-f.inc}

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis \texttt{/etc/veraweb} mit einem
Unterverzeichnis \texttt{l10n} an und kopieren Sie die Konfigurations-
und Sprachdateien dorthin:

\begin{lstdump}[language=sh]{/etc/veraweb fuellen}
sudo mkdir -p /etc/veraweb/l10n
sudo install -c -o 0 -g 0 -m 644 *.properties /etc/veraweb/
sudo install -c -o 0 -g 0 -m 644 l10n/*.resource /etc/veraweb/l10n/
\end{lstdump}
\end{minipage}

Die weiteren Dateien werden im folgenden verwendet. Falls Sie möchten
können Sie bereits das Benutzerhandbuch (\texttt{Benutzerhandbuch.pdf})
dem Nutzerkreis zukommen lassen; es ist jedoch auch als Online-Hilfe
während der Benutzung von VerA.web jederzeit durch einen Klick erreichbar.

\subsection{LDAP-Anbindung VerA.web-core}\label{subsec:setup-core-ldap}

Bearbeiten Sie die Datei \texttt{config\_ldap\_access.xml}, indem Sie
dort die nötigen Angaben eintragen, um Ihren LDAP-Verzeichnisdienst
benutzen zu können. Dies sind insbesondere die folgenden Parameter:

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

\begin{minipage}{\textwidth}
Danach installieren Sie die angepaßte Konfigurationsdatei:

\begin{lstdump}[language=sh]{config_ldap_access.xml installieren}
sudo install -c -o 0 -g 0 -m 644 config_ldap_access.xml /etc/veraweb/
\end{lstdump}
\end{minipage}

Hiermit ist die LDAP-Konfiguration abgeschlossen.

\subsection{Datenbank VerA.web-core}\label{subsec:setup-core-db}

Im folgenden Abschnitt wird die Datenbank für VerA.web mit dem
Datenbankschema sowie den Stammdaten und Fachadministratorenzugängen
eingerichtet. Hierbei wird von einer auf der lokalen Maschine im
Debian-Stil installierten und betriebsbereiten PostgreSQL-Datenbank
ausgegangen.

Bevor Sie beginnen informieren Sie sich bitte über die LDAP-Kürzel
(Unix-Loginnamen) der Nutzer, die zunächst einen Administratorenzugang
zu VerA.web erhalten sollen (idealerweise sollten Sie sich selbst
ebenfalls einen solchen Zugang einrichten). Diese benötigen wir später.

\begin{minipage}{\textwidth}
Legen Sie zunächst den Datenbankbenutzer und die Datenbank an:

\begin{lstdump}[language=sh]{DB erstellen}
sudo -u postgres createuser -D -P -R -S veraweb
sudo -u postgres createdb -E UTF-8 -O veraweb -T template0 veraweb
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Falls Sie als Datenbankpaßwort \emph{nicht} \texttt{veraweb} benutzen oder
anderweitig von den Standardvorgaben (lokale PostgreSQL, Standardport 5432,
Datenbankname \texttt{veraweb}, Datenbankbenutzer \texttt{veraweb}) müssen
Sie noch eine Konfigurationsdatei anpassen. Entpacken Sie diese zunächst:

\begin{lstdump}[language=sh]{config_database_pools.xml entpacken}
unzip -j ../veraweb.war OCTOPUS/config_database_pools.xml
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Bearbeiten Sie danach die Datei \texttt{config\_database\_pools.xml} und
installieren Sie sie schließlich nach \texttt{/etc/veraweb}:

\begin{lstdump}[language=sh]{config_database_pools.xml installieren}
$EDITOR config_database_pools.xml
sudo install -c -o 0 -g 0 -m 644 config_database_pools.xml /etc/veraweb/
\end{lstdump}
\end{minipage}

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
Danach müssen Sie diejenigen LDAP-Benutzer, die initial als Administratoren
agieren sollen, bekanntgeben. Setzen Sie hierzu für jeden Benutzer eine
Zeile entsprechend der folgenden ab (\texttt{vnnnnn} sei das LDAP-Kürzel):

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

Damit ist die Einrichtung der Datenbank abgeschlossen.

\subsection{Webserver VerA.web-core und REST-API}\label{subsec:setup-core-apache}

\begin{minipage}{\textwidth}
Aktivieren Sie zunächst das \texttt{jk}-Modul, welches benutzt wird,
um vermittels des AJP-Protokolls Anfragen an den Webserver an Tomcat
durchzureichen:

\begin{lstdump}[language=sh]{a2enmod jk}
sudo a2enmod jk
\end{lstdump}
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

\subsection{Applikationsserver REST-API}\label{subsec:setup-vwor-tomcat}

\input{text-1632-t.inc}

\subsection{Applikationsserver VerA.web-core}\label{subsec:setup-core-tomcat}

\begin{minipage}{\textwidth}
Deployen Sie die VerA.web-Anwendung und die REST-API in den Applikationsserver:

\begin{lstdump}[language=sh]{Tomcat deploy}
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo install -c -o 0 -g 0 -m 644 ../vwor.war /var/lib/tomcat7/webapps/
\end{lstdump}
\end{minipage}

Nun können Sie bereits über \texttt{http://localhost:8080/veraweb/} auf
das Veranstaltungsmanagement zugreifen und sich testweise einloggen. Falls
hierbei Fehler auftauchen prüfen Sie bitte den LDAP-Zugriff und eventuell
in \texttt{/var/log/tomcat7/catalina.out} auftauchende Meldungen.

\begin{minipage}{\textwidth}
Stoppen Sie bitte nun den Applikationsserver:

\begin{lstdump}[language=sh]{Tomcat stop}
sudo service tomcat7 stop
\end{lstdump}

Bitte beachten Sie, daß der Tomcat-Dienst ggfs. noch einige Sekunden
weiterläuft, nachdem der Stop-Befehl abgeschickt wurde; im Zweifelsfalls
können Sie mit \texttt{ps ax | grep java} prüfen, ob noch Prozesse laufen.
\end{minipage}

\begin{minipage}{\textwidth}
Nun bearbeiten Sie die Datei \texttt{/etc/tomcat7/server.xml}, um Tomcat
von HTTP auf AJP umzustellen. Suchen Sie zunächst folgenden Abschnitt
(eine einfache Suche nach „8080“ findet ihn) und kommentieren ihn aus,
indem Sie davor \texttt{<!-{}-} und dahinter \texttt{-{}->} schreiben:

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

Bitte beachten Sie, daß Sie den Tomcat-Anwendungsserver auch neustarten
müssen, wenn Sie diese Konfigurationsänderungen bereits vorgenommen haben,
damit die unter \nameref{subsec:setup-vwor-tomcat} beschriebenen Änderungen
aktiv werden, da sonst die REST-API nicht einsatzbereit ist.

\begin{minipage}{\textwidth}
Nun können Sie den Server wieder starten:

\begin{lstdump}[language=sh]{Tomcat start}
sudo service tomcat7 start
\end{lstdump}
\end{minipage}

Jetzt können Sie z.B. über \texttt{https://veraweb.lan.tarent.de/veraweb/}
auf das Veranstaltungsmanagement zugreifen und sich einloggen.

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr; diese können nun entfernt werden:

\begin{lstdump}[language=sh]{Aufraeumen}
cd ..
rm -rf veraweb-core-〈\vwiaverssw{}〉* veraweb.war vwor.war
\end{lstdump}
\end{minipage}
