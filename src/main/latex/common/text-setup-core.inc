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
% Textabschnitt 3.1: Setup VerA.web-core

\section{Installation internes VerA.web-System}\label{sec:setup-int}

Nach dem erfolgten Aufsetzen des Systems mit den Basispaketen
und dem Download der Installationspakete wird im folgenden die
Installation der einzelnen Komponenten beschrieben.

\subsection{Dateien VerA.web-core}\label{subsec:setup-core-files}

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

\subsection{Datenbank VerA.web-core}\label{subsec:setup-core-db}

Im folgenden Abschnitt wird die Datenbank für VerA.web mit dem
Datenbankschema sowie den Stammdaten und Fachadministratorenzugängen
eingerichtet. Hierbei wird von einer auf der lokalen Maschine im
Debian-Stil installierten und betriebsbereiten PostgreSQL-Datenbank
ausgegangen.

Bevor Sie beginnen informieren Sie sich bitte über die LDAP-Kürzel
(Unix-Loginnamen) der Nutzer, die zunächst einen Administratorenzugang
zu VerA.web erhalten sollen (idealerweise sollten Sie sich selbst
ebenfalls einen solchen Zugang einrichten). Diese benötigen wir
weiter unten.

\begin{minipage}{\textwidth}
Legen Sie zunächst den Datenbankbenutzer und die Datenbank an:

\begin{lstlisting}[language=sh]
sudo -u postgres createuser --no-superuser --no-createdb --no-createrole --pwprompt veraweb
sudo -u postgres createdb --encoding=UTF-8 --owner=veraweb --template=template0 veraweb
\end{lstlisting}
\end{minipage}





\section{blah}\label{sec:blah}\label{subsec:blah}

First page. normal \textbf{bold \textit{bolditalic}} \textit{italic} normal

Links: Test \nameref{sec:intro} und \nameref{subsec:req-core} Links.

\begin{lstlisting}[language=c]
task main() {
 SetSensorTouch(IN_1);
 OnFwd(OUT_AB, 75);
 while (Sensor(IN_1) == 0) {}
 Off(OUT_AB);
}
\end{lstlisting}
