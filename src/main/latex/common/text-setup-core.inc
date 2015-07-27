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
% Textabschnitt 3.1-3.3: Setup VerA.web-core

\section{Installation internes VerA.web-System}\label{sec:setup-int}

Nach dem erfolgten Aufsetzen des Systems mit den Basispaketen
und dem Download der Installationspakete wird im folgenden die
Installation der einzelnen Komponenten beschrieben.

\subsection{Dateien VerA.web-core}\label{subsec:setup-core-files}

\begin{minipage}{\textwidth}
Benennen Sie die Datei \texttt{veraweb-core-\produktversion{}.war}
in \texttt{veraweb.war} um. Entpacken Sie die Datei
\texttt{veraweb-core-\produktversion{}-files.tgz} (oder
\texttt{.zip}) und wechseln Sie in das angelegte neue Verzeichnis
(im Beispiel wird die Unix-Variante mit \texttt{.tgz} verwendet):

\begin{lstlisting}[language=sh]
mv veraweb-core-〈\produktversion{}〉.war veraweb.war
tar -xzf veraweb-core-〈\produktversion{}〉-files.tgz
cd veraweb-core-〈\produktversion{}〉
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis \texttt{/etc/veraweb} mit einem
Unterverzeichnis \texttt{l10n} an und kopieren Sie die Sprachdateien
dorthin:

\begin{lstlisting}[language=sh]
sudo mkdir -p /etc/veraweb/l10n
sudo install -c -o 0 -g 0 -m 644 l10n/* /etc/veraweb/l10n/
\end{lstlisting}
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

\begin{lstlisting}[language=sh]
sudo install -c -o 0 -g 0 -m 644 config_ldap_access.xml /etc/veraweb/
\end{lstlisting}
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

\begin{lstlisting}[language=sh]
sudo -u postgres createuser -D -P -R -S veraweb
sudo -u postgres createdb -E UTF-8 -O veraweb -T template0 veraweb
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Falls Sie als Datenbankpaßwort \emph{nicht} \texttt{veraweb} benutzen oder
anderweitig von den Standardvorgaben (lokale PostgreSQL, Standardport 5432,
Datenbankname \texttt{veraweb}, Datenbankbenutzer \texttt{veraweb}) müssen
Sie noch eine Konfigurationsdatei anpassen. Entpacken Sie diese zunächst:

\begin{lstlisting}[language=sh]
unzip -j ../veraweb.war OCTOPUS/config_database_pools.xml
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Bearbeiten Sie danach die Datei \texttt{config\_database\_pools.xml} und
installieren Sie sie schließlich nach \texttt{/etc/veraweb}:

\begin{lstlisting}[language=sh]
$EDITOR config_database_pools.xml
sudo install -c -o 0 -g 0 -m 644 config_database_pools.xml /etc/veraweb/
\end{lstlisting}
\end{minipage}

Nun können Sie das Datenbankschema anlegen und die Stammdaten importieren.
Bei letzteren haben Sie die Möglichkeit, statt der vorgegebenen Stammdaten
lediglich einen Minimalsatz zu laden (\texttt{stammdaten-colours.sql} statt
\texttt{stammdaten-full.sql}).

\begin{minipage}{\textwidth}
Loggen Sie sich zunächst auf der Datenbank ein:

\begin{lstlisting}[language=sh]
psql -U veraweb -h 127.0.0.1 -W veraweb
\end{lstlisting}
\end{minipage}

Falls Sie einen anderen Datenbankserver, Port, Benutzer oder Datenbank
verwenden müssen Sie diese hier selbstverständlich anpassen.

\begin{minipage}{\textwidth}
Dann setzen Sie folgende SQL-Kommandos ab:

\begin{lstlisting}[language=SQL]
\i schema.sql
SELECT serv_verawebschema(1);
\i stammdaten-full.sql
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Danach müssen Sie diejenigen LDAP-Benutzer, die initial als Administratoren
agieren sollen, bekanntgeben. Setzen Sie hierzu für jeden Benutzer eine
Zeile entsprechend der folgenden ab (\texttt{vnnnnn} sei das LDAP-Kürzel):

\begin{lstlisting}[language=SQL]
INSERT INTO veraweb.tuser (fk_orgunit, username, role)
    VALUES (NULL, 'vnnnnn', 5);
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Schließlich müssen einige Sequenzen initialisiert und das Datenbankschema
auf die jeweils aktuelle Version aktualisiert werden; danach können Sie
den \texttt{psql}-Client wieder verlassen:

\begin{lstlisting}[language=SQL]
SELECT veraweb.serv_build_sequences();
\i upgrade.sql
\q
\end{lstlisting}
\end{minipage}

Damit ist die Einrichtung der Datenbank abgeschlossen.
