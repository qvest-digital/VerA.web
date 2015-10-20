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
% Textabschnitt 2.2: Systemvoraussetzungen VerA.web-core

\subsection{Interne Veranstaltungs- und Adreßverwaltung}\label{subsec:req-core}

Zum Betrieb von VerA.web core empfehlen wir eine eigenständige VM
mit Debian Linux 7 „wheezy“ als Betriebssystem. Zwar \emph{kann}
die Software auch mit einer anderen Debian-basierten Distribution
(z.B. Ubuntu 14.04 „trusty“) oder einer anderen Java/Tomcat-Umgebung
betrieben werden, allerdings geht diese Installationsanleitung von
der empfohlenen Systemumgebung aus.

Für VerA.web core benötigen Sie mindestens 512 MiB Arbeitsspeicher;
1 GiB oder mehr wird empfohlen. Wir empfehlen mindestens 2 GiB Festplatte,
besser 8 GiB.

In dieser Anleitung sowie den Systemvoraussetzungen wird weiterhin
angenommen, daß der notwendige Datenbankserver (PostgreSQL) auf
derselben VM wie die interne Veranstaltungs- und Adreßverwaltung läuft.

Desweiteren benötigen Sie den Apache 2 Webserver (idealerweise mit
dem \texttt{prefork} MPM; in dieser Anleitung gehen wir von einem
Setup mit Apache 2.2\footnote{\label{fn:apache}Debian 7 „wheezy“
wird mit Apache 2.2 ausgeliefert, Debian 8 „jessie“\textsuperscript{\ref{fn:jessie}}
mit Apache 2.4} auf Debian wheezy\footnote{\label{fn:jessie}Diese
Installationsanleitung beschreibt aktuell nur den Betrieb unter
Debian 7 „wheezy“.} aus) und \texttt{mod\_jk}, Tomcat 7 mit Java™ 7.

\Warning \emph{Achtung:} Java™ 6 oder älter wird nicht unterstützt
und führt während der Installation zu Fehlern!

Wir setzen unsere Systeme üblicherweise aus Sicherheitsgründen so auf,
daß HTTPS-Verbindungen im Apache Webserver terminiert werden und über
das AJP-Protokoll von diesem an Tomcat weitergereicht werden. Sämtliche
SSL/TLS-Keys, -Features und weitere Sicherheitsprüfungen wie z.B. ein
Paßwortschutz oder Zugangskontrolle richten wir im Apache Webserver ein.
Sie \emph{können} Tomcat auch direkt über HTTP ansprechen, dies entspricht
jedoch nicht dem von uns empfohlenen Setup.

Die Installation des Webservers (SSL/TLS-Zertifikat, Zugangskontrollen,
falls nötig vhosts) liegt außerhalb des Rahmens dieses Dokuments. Es
wird davon ausgegangen, daß Apache so weit eingerichtet wird, daß er
statische Webseiten ausliefern kann; in diesem Dokument wird nur die
Integration mit dem Veranstaltungsmanagement beschrieben. (Vergessen
Sie bei Debian nicht, \texttt{sudo a2enmod ssl} auszuführen.)

\begin{minipage}{\textwidth}
Auf einem Debian 7 „wheezy“-System können Sie diese sowie den
Datenbankserver wie folgt lokal installieren:

\begin{lstlisting}[language=sh]
sudo apt-get install postgresql openjdk-7-jre-headless tomcat7 \
    apache2-mpm-prefork apache2 libapache2-mod-jk
\end{lstlisting}
\end{minipage}

\begin{minipage}{\textwidth}
Bitte achten Sie darauf, daß keine ältere Version des JDK verwendet
wird (hier werden die in „wheezy“ vorhandenen älteren Versionen
deinstalliert):

\begin{lstlisting}[language=sh]
sudo apt-get purge openjdk-6-jre-headless gcj-4.6-jdk gcj-4.7-jdk
\end{lstlisting}
\end{minipage}

Zur Installation müssen Sie folgende Dateien („die VerA.web-core
Distribution“) von tarent beziehen:

\begin{itemize}
 \item{\texttt{veraweb-core-\vwiaverssw{}-files.tgz}
  \emph{oder} \texttt{veraweb-core-\vwiaverssw{}-files.zip}}
  \\(der Inhalt dieser Dateien ist identisch, nur anders verpackt)
 \item{\texttt{veraweb-core-\vwiaverssw{}.war}}
 \item{\texttt{rest-api-\vwiaverssw{}.war}}
\end{itemize}

Bitte wenden Sie sich hierzu an unseren Produktvertrieb.

Die Installation wird als regulärer Benutzer, der sich vermittels
des Programms \texttt{sudo} Superuserrechte verschaffen kann,
durchgeführt, kann aber auch als \texttt{root} durchlaufen werden.
