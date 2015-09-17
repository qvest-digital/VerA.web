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
% Textabschnitt 4.1-4.3: Upgrade VerA.web-core

\section{Upgrade internes VerA.web-System}\label{sec:upgrade-int}

Im folgenden wird das Upgrade der einzelnen Komponenten
nach erfolgtem Download der Installationspakete beschrieben.

Hierbei wird davon ausgegangen, daß VerA.web gemäß dem Abschnitt
\nameref{sec:setup-int} der vorliegenden Anleitung, oder einer
ähnlich aufgebauten Anleitung einer Vorgängerversion, auf einem
Debian Linux 7 „wheezy“-System eingerichtet wurde. Installationen
gemäß früherer Anleitungen der tarent solutions GmbH, die abweichende
Verfahren z.B. in der Webserverkonfiguration benutzten, werden
ebenfalls unterstützt, benötigen aber ggfs. Anpassungen.

Falls Sie weitergehende Anpassungen, z.B. an den Velocity-Templates
für die Webseiten, vorgenommen haben müssen Sie die Änderungen
manuell nachpflegen.

Upgrades von VerA.web 1.3.15 und neuer werden unterstützt. Bei einem
Upgrade von einer älteren Version als 1.5 (also 1.3.15 und 1.4) ist
generell mehr Vorsicht walten zu lassen.

Für weitergehenden Installationssupport, insbesondere bei Systemen,
die nicht nach dieser oder einer ähnlichen Anleitung installiert
wurden, kontaktieren Sie bitte unseren Produktvertrieb oder, falls
Sie bereits einen bestehenden Wartungsvertrag haben, unser Service-
und Wartungsteam.

\subsection{Dateien VerA.web-core}\label{subsec:upgrade-core-files}

\begin{minipage}{\textwidth}
Benennen Sie die Datei \texttt{veraweb-core-\vwiaverssw{}.war}
in \texttt{veraweb.war} um. Entpacken Sie die Datei
\texttt{veraweb-core-\vwiaverssw{}-files.tgz} (oder
\texttt{.zip}) und wechseln Sie in das angelegte neue Verzeichnis
(im Beispiel wird die Unix-Variante mit \texttt{.tgz} verwendet):

\begin{lstlisting}[language=sh]
mv veraweb-core-〈\vwiaverssw{}〉.war veraweb.war
tar -xzf veraweb-core-〈\vwiaverssw{}〉-files.tgz
cd veraweb-core-〈\vwiaverssw{}〉
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Falls das Verzeichnis \texttt{/etc/veraweb/l10n} noch nicht existiert,
legen Sie es an. Kopieren Sie dann die aktuellen Sprachdateien dorthin:

\begin{lstlisting}[language=sh]
sudo mkdir -p /etc/veraweb/l10n
sudo install -c -o 0 -g 0 -m 644 l10n/*.resource /etc/veraweb/l10n/
\end{lstlisting}
\end{minipage}

Falls Sie von einer VerA.web-Version, die die Konfigurationsdateien
noch nicht in \texttt{/etc/veraweb} ablegt, aktualisieren, müssen Sie
dort zunächst die angepaßten Dateien aus dem Verzeichnis
\texttt{/var/lib/tomcat7/webapps/veraweb/OCTOPUS/config\_*.xml}
hinkopieren.

Das neue Konfigurationsschema wurde mit VerA.web 1.5.1.4 eingeführt,
war jedoch bereits in VerA.web 1.4.3.5 (und \emph{nur} dieser Version
aus dem 1.4-Zweig) verfügbar.

Je nach Datei gibt es hierbei einige Besonderheiten zu beachten:

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
empfohlen, an diesen Dateien – außer \texttt{config\_ldap\_access.xml}
und ggfs. \texttt{config\_database\_pools.xml} – nichts zu ändern,
sondern \texttt{config\_override.xml} anzupassen, wenn möglich.

\emph{Tip:} Entpacken Sie (mit \texttt{unzip}) \texttt{veraweb.war}
aus Ihrer vor dem Upgrade installierten Version und vergleichen Sie
die Dateien aus dem Archiv mit den installierten Dateien, z.B. mit
\texttt{diff -u} oder einem graphischen Tool, um festzustellen, ob
Sie überhaupt Änderungen durchführen müssen, und wenn ja, welche.

Die Datei \texttt{config\_ldap\_access.xml} wurde in manchen
Versionen als \texttt{config\_ldap\_access.xml.dist} und/oder
als separater Download angeboten.

\subsection{Datenbank VerA.web-core}\label{subsec:upgrade-core-db}

\begin{minipage}{\textwidth}
Loggen Sie sich auf der Datenbank ein und starten Sie das Upgrade-SQL:

\begin{lstlisting}[language=sh]
psql -U veraweb -h 127.0.0.1 -W veraweb <upgrade.sql
\end{lstlisting}

Falls Sie einen anderen Datenbankserver, Port, Benutzer oder Datenbank
verwenden müssen Sie diese hier selbstverständlich anpassen.
\end{minipage}

\subsection{Applikationsserver VerA.web-core}\label{subsec:upgrade-core-tomcat}

\begin{minipage}{\textwidth}
Stoppen Sie bitte zunächst den Applikationsserver und löschen
ggfs. alle vorherigen VerA.web-Webapps (es ist vielleicht
sinnvoll, ein Backup zu machen):

\begin{lstlisting}[language=sh]
sudo service tomcat7 stop
sudo rm -rf /var/lib/tomcat7/webapps/veraweb*
\end{lstlisting}
%sudo rm -rf /var/lib/tomcat7/webapps/vwor*

Bitte beachten Sie, daß der Tomcat-Dienst ggfs. noch einige Sekunden
weiterläuft, nachdem der Stop-Befehl abgeschickt wurde; im Zweifelsfalls
können Sie mit \texttt{ps ax | grep java} prüfen, ob noch Prozesse laufen.
\end{minipage}

\begin{minipage}{\textwidth}
Dann starten Sie den Server wieder und deployen die VerA.web-Anwendung:

\begin{lstlisting}[language=sh]
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo service tomcat7 start
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr und können sie entfernen:

\begin{lstlisting}[language=sh]
cd ..
rm -rf veraweb-core-〈\vwiaverssw{}〉* veraweb.war
\end{lstlisting}
\end{minipage}
