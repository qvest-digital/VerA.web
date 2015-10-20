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
% Textabschnitt 4.1-4.4: Upgrade VerA.web-core

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
\fi

Für weitergehenden Installationssupport, insbesondere bei Systemen,
die nicht nach dieser oder einer ähnlichen Anleitung installiert
wurden, kontaktieren Sie bitte unseren Produktvertrieb oder, falls
Sie bereits einen bestehenden Wartungsvertrag haben, unser Service-
und Wartungsteam.

\subsection{Dateien VerA.web-core}\label{subsec:upgrade-core-files}

\input{text-extract.inc}

\begin{minipage}{\textwidth}
Falls das Verzeichnis \texttt{/etc/veraweb/l10n} noch nicht existiert,
legen Sie es an. Kopieren Sie dann die aktuellen Sprachdateien dorthin:

\begin{lstlisting}[language=sh]
sudo mkdir -p /etc/veraweb/l10n
sudo install -c -o 0 -g 0 -m 644 l10n/*.resource /etc/veraweb/l10n/
\end{lstlisting}
\end{minipage}

\ifvwconfigsinetcalready
Diese Fassung der Upgradeanleitung geht davon aus, daß Sie die
Konfigurationsdateien bereits in \texttt{/etc/veraweb} haben
(VerA.web 1.4.3.5 oder 1.5.1.4 und neuer).
\else
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
\fi

\subsection{Upgrade von 1.6.31 oder älter}\label{subsec:upgrade-1631}

\input{text-1631-f.inc}

\begin{minipage}{\textwidth}
Installieren Sie nun die Konfigurationsdateien für die REST-API:

\begin{lstlisting}[language=sh]
sudo install -c -o 0 -g 0 -m 644 *.properties /etc/veraweb/
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Fügen Sie folgende Zeile der Apache-vhost-Konfiguration hinzu:

\begin{lstlisting}
JkMount /vwor* ajp13_worker
\end{lstlisting}
\end{minipage}

Starten Sie den Webserver neu, um die Änderungen wirksam zu machen.

\input{text-1631-t.inc}

\subsection{Datenbank VerA.web-core}\label{subsec:upgrade-core-db}

\begin{minipage}{\textwidth}
Loggen Sie sich auf der Datenbank ein und starten Sie das Upgrade-SQL:

\begin{lstlisting}[language=sh]
psql -U veraweb -h 127.0.0.1 -W veraweb <upgrade.sql
\end{lstlisting}

Falls Sie andere Werte für Datenbankserver, Port, Benutzer oder Datenbankname
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
sudo rm -rf /var/lib/tomcat7/webapps/vwor*
\end{lstlisting}

Bitte beachten Sie, daß der Tomcat-Dienst ggfs. noch einige Sekunden
weiterläuft, nachdem der Stop-Befehl abgeschickt wurde; im Zweifelsfalls
können Sie mit \texttt{ps ax | grep java} prüfen, ob noch Prozesse laufen.
\end{minipage}

\begin{minipage}{\textwidth}
Dann starten Sie den Server wieder und deployen die VerA.web-Anwendung
und die REST-API:

\begin{lstlisting}[language=sh]
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo install -c -o 0 -g 0 -m 644 ../vwor.war /var/lib/tomcat7/webapps/
sudo service tomcat7 start
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr; diese können nun entfernt werden:

\begin{lstlisting}[language=sh]
cd ..
rm -rf veraweb-core-〈\vwiaverssw{}〉* veraweb.war vwor.war
\end{lstlisting}
\end{minipage}
