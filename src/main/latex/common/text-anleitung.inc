% VerA.web (public) Installationsanleitung
%
% Copyright © 2015, 2016
%	Thorsten Glaser <t.glaser@tarent.de>
% Copyright © 2014, 2015
%	Thorsten Glaser <thorsten.glaser@teckids.org>
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
% Characters requiring escaping:
% • { } # & _ % $ ⇒ quote by prepending a backslash \
% • \ → \textbackslash
% • ~ → \textasciitilde
% • (nbsp) → ~
% • (en dash) → \dash
%-
% TODO tglase:
% - Struktur: do not use cross-references; duplicate content (KISS)
% - Apache, SSL, …
% - LDAP: slapd, lam
% - svc: primer on service management with DJB dæmontools
% - Upgradeanleitung wenn mit OA
% - Debian jessie-tauglich machen
% TODO Team:
% - neuer Mail-Kram

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
\fi% !oa
durch einen Systemadministrator
eigenständig installiert werden kann. Hierbei wird eine empfohlene
Installation beschrieben; ein Betrieb ist auch mit abweichender
Konfiguration (z.B. ohne Apache) möglich, aber nicht durch dieses
Dokument abgedeckt.

Alle Code-Listings finden Sie auch nochmal als Plaintext unter
\texttt{\jobname.lst} im „files“-Tarball
(\texttt{veraweb-core-\vwiaverssw{}-files.tgz} \emph{oder}
\texttt{veraweb-core-\vwiaverssw{}-files.zip}, siehe
\nameref{subsec:intro-distro}).
Zwecks Zuordnung finden Sie die Listing-Nummern am äußeren
Seitenrand bzw. als Teil der Überschrift in der Listing-Datei.

Die Installation von VerA.web ist äußerst flexibel und anpaßbar.
In dieser Anleitung haben wir daher einige Annahmen getroffen, um
sie nicht wegen kombinatorischer Explosion der Möglichkeiten noch
unverständlicher zu machen. Hierzu gehören:\keinumbruch

\begin{itemize}
 \item{Die Installation wird auf nicht anderweitig verwendeten,
  frischen (virtuellen) Maschinen durchgeführt.}
 \item{Als Betriebssystem wird Debian 7 „wheezy“ eingesetzt; an
  den Stellen, wo Konfigurationsdateien angepaßt werden müssen,
  können wir die Debian-Standardwerte voraussetzen.}
 \item{Das System ist grundsätzlich installiert, betriebsbereit
  und rebootfest; Firewalleinstellungen sind hinreichend getroffen.
  Für jede Maschine ist ein gültiges SSL-Zertifikat vorhanden.}
 \item{Die PostgreSQL-Datenbank für VerA.web wird auf dem System
  lokal installiert.}
 \item{Die REST-API wird nicht vom VerA.web core getrennt.}
\ifoa
 \item{OSIAM ist entweder vorhanden, oder wird auf einem eigenen
  System installiert, oder wird auf demselben System wie VerA.web
  core installiert.}
 \item{Die PostgreSQL-Datenbank für OSIAM wird auf demselben
  System lokal installiert.}
 \item{Die Online-Anmeldung wird entweder auf einem eigenen System
  (Regelfall) oder auf demselben System wie VerA.web core betrieben.}
 \item{Alle Komponenten kommunizieren auch untereinander über mit
  HTTPS gesicherte Verbindungen und durch die Apache-Frontends.}
\fi% oa
\end{itemize}

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

VerA.web authentifiziert Sachbearbeiter über einen \nameref{subsec:req-ldap}.
Ein LDAP-Server ist Voraussetzung für den Betrieb von VerA.web.

\ifoa
VerA.web mit Online-Anmeldung benutzt OSIAM zur sicheren Identitätsverwaltung.
Eine funktionierende OSIAM-Installation ist Voraussetzung für den Betrieb von
VerA.web mit Online-Anmeldung; sollten Sie jedoch noch keine OSIAM-Instanz bei
Ihnen betreiben wird eine für VerA.web taugliche Kurzinstallation erläutert.
OSIAM kann auf einem separaten System oder zusammen mit VerA.web core laufen
und benötigt eine eigene Datenbank, die aber im selben PostgreSQL-Cluster
liegen kann (und, bei Installation auf demselben Server, sollte). OSIAM für
VerA.web ist ein reines „Backend“ ohne Weboberfläche.

Die Online-Anmeldung selber wird üblicherweise auf einem separaten System
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

\ifoa

\subsection{Systemübersicht}\label{subsec:intro-overview-blocks}

Im folgenden finden Sie eine grobe Übersicht über die einzelnen oben
erwähnten Komponenten und ihr Zusammenspiel:\keinumbruch

\begin{minipage}{\textwidth}

\begin{tikzpicture}[
  >=stealth',
  people/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above left,rectangle,draw=CornflowerBlue,minimum width=12mm},
  acloud/.style={thick,text=black,fill=white,drop shadow,cloud,cloud puffs=9,draw=green,above right,minimum width=20mm,minimum height=10mm},
 ]

% \draw (0mm,0mm) -- (0mm,120mm) -- (173mm,120mm) -- (173mm,0mm) -- (0mm,0mm);

  \node[above right,rectangle,fill=blue!20,minimum width=119mm,minimum height=15mm] (ldap) at (3mm,75mm) {LDAP};
  \node[above right,rectangle,fill=green!20,minimum width=119mm,minimum height=15mm] (web) at (3mm,56mm) {VerA.web Webanwendung};
  \node[above right,rectangle,fill=yellow!40,minimum width=119mm,minimum height=15mm] (oa) at (3mm,10mm) {VerA.web Online-Anmeldung};
  \node[above right,rectangle,fill=red!20,minimum width=99mm,minimum height=15mm] (osiam) at (23mm,37mm) {OSIAM};

  \node[people]   (sb)     at (168mm,60mm) {Mitarbeiter};
  \node[people]   (guests) at (168mm,13mm) {Gäste};
  \node[acloud]   (inet)   at (130mm,15mm) {};

  \draw[thick,->] (web) -- (ldap);
  \draw[thick,->] (sb.west) -- (sb-|web.east);
  \draw[thick,->] (web.south) -- (web|-osiam.north);
  \draw[thick,->] (guests) -- (inet);
  \draw[thick,->] (inet) -- (oa);
  \draw[thick,->] ([xshift=-5cm]oa.north) -- ([xshift=-5cm]oa|-web.south);
  \draw[thick,->] (oa.north) -- (oa|-osiam.south);

  \node[above right,inner sep=0pt] at (140mm,33mm) {internes Netz};
  \node[below right,inner sep=0pt] at (140mm,31mm) {extern erreichbar};
  \draw[dashed]   (0mm,32mm) -- (173mm,32mm);

\end{tikzpicture}

\end{minipage}

\fi% oa

\subsection{Apache als Frontend}\label{subsec:intro-apache}

Wir setzen unsere Systeme üblicherweise aus Sicherheitsgründen so auf,
daß HTTPS-Verbindungen im Apache Webserver terminiert werden und von
diesem an die einzelnen Anwendungsserver (über das AJP-Protokoll an
\ifoa
Tomcat und als HTTP-Proxy an den Microservice der Online-Anmeldung)
\else% !oa
Tomcat)
\fi% !oa
weitergereicht werden. Sämtliche SSL/TLS-Keys, -Features und weitere
sicherheitsrelevante Einstellungen richten wir im Apache Webserver ein,
auch um die Angriffsoberfläche zu reduzieren und besser getesteten und
weiter verbreiteten Code zu verwenden, und weil die Administration der
genannten Features in Apache bekannter und erprobter ist als in den
individuellen Anwendungsservern. Sie \emph{können} die Dienste auch direkt
über HTTP ansprechen, dies widerspricht jedoch unserem empfohlenen Setup.

\ifoa
Es wird je eine Installation des Webservers pro VM benötigt, da in
der von uns empfohlenen Installationemethode die Komponenten auch
untereinander ausschließlich verschlüsselt und sicher über HTTPS
miteinander kommunizieren.
\fi% oa

%TODO weg mit diesem Absatz
Die Installation des Webservers (SSL/TLS-Zertifikat, Zugangskontrollen,
falls nötig vhosts) liegt außerhalb des Rahmens dieses Dokuments. Es
wird davon ausgegangen, daß Apache so weit eingerichtet wird, daß er
statische Webseiten ausliefern kann; in diesem Dokument wird nur die
Integration mit dem Veranstaltungsmanagement beschrieben. (Vergessen
Sie bei Debian \texttt{sudo a2enmod ssl} nicht.)

\ifoa

\subsubsection{Detailübersicht}\label{subsubsec:intro-overview-coarse}

In der nächsten Graphik werden die zu VerA.web gehörenden Komponenten
mit mehr Detailtiefe aufgeschlüsselt:\keinumbruch

\begin{minipage}{\textwidth}

\begin{tikzpicture}[
  >=stealth',
  microsvc/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above right,rectangle,draw=Orange,minimum width=76mm},
  webapp/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above right,rectangle,draw=red,minimum width=76mm},
  syssvc/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above right,rectangle,draw=Violet,minimum width=25mm},
  scrptr/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above right,rectangle,draw=black,minimum width=25mm},
  scrptl/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above left,rectangle,draw=black,minimum width=25mm},
  people/.style={thick,text=black,fill=white,drop shadow,minimum height=10mm,above left,rectangle,draw=CornflowerBlue,minimum width=12mm},
  acloud/.style={thick,text=black,fill=white,drop shadow,cloud,cloud puffs=9,draw=green,above right,minimum width=20mm,minimum height=10mm},
 ]

% \draw (0mm,0mm) -- (0mm,120mm) -- (173mm,120mm) -- (173mm,0mm) -- (0mm,0mm);

  \fill[yellow!40] (2mm,33mm) -- (121mm,33mm) -- (121mm,48mm) -- (2mm,48mm);
  \fill[green!20] (2mm,50mm) -- (121mm,50mm) -- (121mm,94mm) -- (2mm,94mm);

  \node[above right,rectangle,fill=blue!20,minimum width=119mm,minimum height=15mm] (ldap) at (2mm,96mm) {LDAP};
  \node[above right,rectangle,fill=red!20,minimum width=169mm,minimum height=15mm] (osiam) at (2mm,10mm) {OSIAM};

  \node[microsvc] (svwoa)  at ( 39mm,36mm) {VerA.web Online-Anmeldung};
  \node[webapp]   (score)  at ( 39mm,82mm) {VerA.web Webanwendung};
  \node[webapp]   (svwor)  at ( 39mm,64mm) {VerA.web REST-API};
  \node[syssvc]   (dbvw)   at (  4mm,53mm) {veraweb DB};
  \node[people]   (admins) at (167mm,99mm) {Admins};
  \node[people]   (sb)     at (167mm,82mm) {Sachbearbeiter};
  \node[people]   (guests) at (167mm,36mm) {Gäste};
  \node[acloud]   (inet)   at (129mm,38mm) {};

  \draw[thick,->] (admins) -- ([yshift=3mm]ldap);
  \draw[thick,->] ([yshift=-6mm]admins) -| +(-32mm,0mm) |- ([yshift=3mm]score);
  \draw[thick,->] ([yshift=3mm]score.west) -| ([xshift=-60mm]ldap);
  \draw[thick,->] (sb) -- (score);
  \draw[thick,->] ([yshift=-3mm]score) -| (dbvw);
  \draw[thick,->] ([xshift=4mm]score) |- ++(89mm,-8mm) |- ++(-20mm,-48mm) -| ([xshift=6mm]osiam);
  \draw[thick,->] (score.south) -- (score|-svwor.north);
  \draw[thick,->] (guests) -- (inet);
  \draw[thick,->] (inet) -- (svwoa);
  \draw[thick,->] (svwoa) -- (svwor);
  \draw[thick,->] (svwor) -| ([xshift=6mm]dbvw);
  \draw[thick,->] (svwoa.south) -- (svwoa|-osiam.north);

\end{tikzpicture}

{\footnotesize Zugang für Gäste via DMZ durch das Internet (Wolke);
 Administratoren und Sachbearbeiter via Intranet/VPN}

\end{minipage}

Auch in dieser Abbildung werden LDAP und OSIAM als Blackbox
dargestellt. Um Verwirrung zu vermeiden muß aber bereits hier
erwähnt werden, daß sowohl der LDAP-Server als auch OSIAM ihre
eigene Datenhaltung betreiben – die LDAP-Datenbank für slapd
(der OpenLDAP-Server), und eine separate PostgreSQL-Datenbank
für OSIAM.

\subsubsection{Komponentendiagramm}\label{subsubsec:intro-overview-fine}

In dieser Darstellung schlüsseln wir die verwendeten Komponenten
detailliert auf. In der linken Spalte finden Sie Systemdienste
(LDAP, Mailserver) und PostgreSQL-Datenbanken, in der zweiten
Spalte finden Sie die eigentlichen Dienste, in der dritten Spalte
die zugehörigen Apache-Frontends, und in der vierten Spalte die
Benutzer. In schwarzen Kästen befinden sich jeweils zugehörige
Skripte.

\begin{minipage}{\textwidth}

\begin{tikzpicture}[
  >=stealth',
  every node/.style={thick,text=black,fill=white,drop shadow},
  every rectangle node/.style={minimum height=10mm},
  apache/.style={above right,rectangle,draw=Dandelion,minimum width=35mm},
  microsvc/.style={above right,rectangle,draw=Orange,minimum width=32mm},
  webapp/.style={above right,rectangle,draw=red,minimum width=32mm},
  syssvc/.style={above right,rectangle,draw=Violet,minimum width=25mm},
  sysmta/.style={above right,rectangle,draw=Violet,minimum width=15mm},
  scrptr/.style={above right,rectangle,draw=black,minimum width=25mm},
  scrptl/.style={above left,rectangle,draw=black,minimum width=25mm},
  people/.style={above left,rectangle,draw=CornflowerBlue,minimum width=12mm},
  acloud/.style={cloud,cloud puffs=9,draw=green,above right,minimum width=20mm,minimum height=10mm},
 ]

% \draw (0mm,0mm) -- (0mm,120mm) -- (173mm,120mm) -- (173mm,0mm) -- (0mm,0mm);

  \fill[red!20] (2mm,3mm) -- (171mm,3mm) -- (171mm,39mm) -- (36mm,39mm) -- (36mm,34mm) -- (2mm,34mm);
  \fill[yellow!40] (36mm,42mm) -- (121mm,42mm) -- (121mm,57mm) -- (36mm,57mm);
  \fill[green!20] (2mm,35mm) -- (33mm,35mm) -- (33mm,61mm) -- (121mm,61mm) -- (121mm,94mm) -- (2mm,94mm);
  \fill[blue!20] (2mm,96mm) -- (121mm,96mm) -- (121mm,111mm) -- (2mm,111mm);

  \node[apache]   (alam)   at ( 79mm,99mm) {LDAP Account Mgr};
  \node[apache]   (acore)  at ( 79mm,82mm) {VerA.web-core};
  \node[apache]   (avwor)  at ( 79mm,63mm) {REST-API};
  \node[apache]   (avwoa)  at ( 79mm,45mm) {Online-Anmeldung};
  \node[apache]   (aosiam) at ( 79mm,18mm) {OSIAM};
  \node[microsvc] (svwoa)  at ( 39mm,45mm) {VWOA};
  \node[webapp]   (score)  at ( 39mm,82mm) {core};
  \node[webapp]   (svwor)  at ( 39mm,63mm) {VWOR};
  \node[webapp]   (sauth)  at ( 39mm,27mm) {auth-server};
  \node[webapp]   (srsrc)  at ( 39mm, 9mm) {resource-server};
  \node[syssvc]   (ldap)   at (  4mm,99mm) {LDAP-Server};
  \node[syssvc]   (dbvw)   at (  4mm,52mm) {veraweb DB};
  \node[syssvc]   (dbong)  at (  4mm,18mm) {ong DB};
  \node[sysmta]   (mta)    at ( 14mm,71mm) {MTA};
  \node[scrptr]   (usql)   at (  4mm,37mm) {upgrade.sql};
  \node[scrptl]   (riosh)  at (167mm,18mm) {register-into-osiam.sh};
  \node[people]   (admins) at (167mm,99mm) {Admins};
  \node[people]   (sb)     at (167mm,82mm) {Sachbearbeiter};
  \node[people]   (guests) at (167mm,45mm) {Gäste};
  \node[acloud]   (inet)   at (129mm,47mm) {};

  \draw[thick,->] (admins) -- ([yshift=3mm]alam);
  \draw[thick,->] (alam) -- (ldap);
  \draw[thick,->] ([yshift=-6mm]admins) -| +(-32mm,0mm) |- ([yshift=3mm]acore);
  \draw[thick,->] (acore) -- (score);
  \draw[thick,->] ([yshift=5mm]score) -| (ldap);
  \draw[thick,->] (sb) -- (acore);
  \draw[thick,->] (score) -| ([xshift=-7mm]dbvw.north);
  \draw[thick,->] ([yshift=-5mm]score) -| (mta);
  \draw[thick,->] (svwor) -| (mta);
  \draw[thick,->] ([xshift=4mm]score) |- ++(112mm,-8mm) |- ++(-20mm,-39mm) -| ([xshift=6mm]aosiam);
  \draw[thick,->] (score) |- +(25mm,-11mm) -| (avwor);
  \draw[thick,->] (guests) -- (inet);
  \draw[thick,->] (inet) -- (avwoa);
  \draw[thick,->] (avwoa) -- (svwoa);
  \draw[thick,->] (svwoa) |- +(25mm,9mm) -| (avwor);
  \draw[thick,->] (avwor) -- (svwor);
  \draw[thick,->] ([yshift=-4mm]svwor) -| ([xshift=6mm]dbvw);
  \draw[thick,->] (usql) -- (dbvw);
  \draw[thick,<->] ([yshift=3mm]aosiam) -| (sauth);
  \draw[thick,<->] ([yshift=-3mm]aosiam) -| (srsrc);
  \draw[thick,->] (sauth) -| (dbong);
  \draw[thick,->] (srsrc) -| ([xshift=3mm]dbong);
  \draw[thick,->] (riosh) -- +(0mm,-17mm) -| ([xshift=-3mm]dbong);
  \draw[thick,->] (riosh) -- (aosiam);
  \draw[thick,->] (svwoa) |- +(20mm,-10mm) -| (aosiam);

\end{tikzpicture}

\vspace{0.3cm}

\begin{tabu} to \linewidth {rllll}
\rowfont\bfseries\multicolumn{5}{c}{Legende}\\[0.2cm]
 Erste Spalte: & \tikz \node[thick,rectangle,draw=black] {}; Skript &
		 \multicolumn{3}{l}{%
			\tikz \node[thick,rectangle,draw=Violet] {};
			Systemdienst (LDAP, Mailserver,
			PostgreSQL-Datenbank)}\\
Zweite Spalte: & \multicolumn{2}{l}{%
			\tikz \node[thick,rectangle,draw=red] {};
			Webservice (WAR) in Tomcat} &
		 \multicolumn{2}{l}{%
			\tikz \node[thick,rectangle,draw=Orange] {};
			Microservice (JAR)}\\
Dritte Spalte: & \multicolumn{4}{l}{%
			\tikz \node[thick,rectangle,draw=Dandelion] {};
			 Apache Webserver (SSL, mod\_jk oder mod\_proxy\_http)}\\
Vierte Spalte: & \multicolumn{2}{l}{%
			\tikz \node[thick,rectangle,draw=CornflowerBlue] {};
			 Nutzergruppe (per Webbrowser)} &
		 \multicolumn{2}{l}{%
			\tikz \node[thick,rectangle,draw=black] {};
			Skript (Installation/Upgrade)}\\
 Schattierung: & \tikz \node[rectangle,fill=red!30] {}; OSIAM &
		 \tikz \node[rectangle,fill=yellow!60] {}; Online-Anmeldung &
		 \tikz \node[rectangle,fill=green!30] {}; VerA.web core &
		 \tikz \node[rectangle,fill=blue!30] {}; LDAP\\
\multicolumn{5}{l}{\footnotesize
		   Zugang für Gäste via DMZ durch das Internet (Wolke);
		   Administratoren und Sachbearbeiter via Intranet/VPN}\\
\end{tabu}

\end{minipage}

Die WAR- und JAR-Dateien aus der zweiten Spalte finden Sie im
nächsten Abschnitt als individuelle Einträge (bzw. bei OSIAM
im entsprechenden Kapitel direkt als Download). Die schwarz
umrahmten Skripte befinden sich in den \texttt{files}-Archiven.

\fi% oa

\subsection{Die VerA.web-Distribution}\label{subsec:intro-distro}

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

\subsection{Installationsvorgehen}\label{subsec:intro-install}

Installation und Upgrade von VerA.web funktionieren, indem
zunächst Teile der Distribution (siehe oben) auf den Server
kopiert werden, und alle weiteren Schritte dann auf dem System
stattfinden. Die Beispiele sind so gehalten, daß die gesamte
Installation entweder als normaler Benutzer (auf dem Server),
der sich vermittels \texttt{sudo} Superuserrechte verschaffen
kann, oder als \texttt{root} durchlaufen werden kann (hierzu
installieren Sie \texttt{sudo} \dash die Standardkonfiguration
erlaubt \texttt{root} unbegrenzten Zugriff \dash oder lassen
das Wort \texttt{sudo} in allen Codebeispielen weg).

Die weiteren Schritte, die auf dem Server durchzuführen sind,
involvieren:\keinumbruch

\begin{itemize}
 \item{Entpacken der \texttt{files}-Archive}
 \item{Wechseln in ein spezifisches Unterverzeichnis \dash
  alle weiteren Schritte nehmen an, daß dieses Unterverzeichnis
  Ihr aktuelles Arbeitsverzeichnis ist; sollten Sie zwischendurch
  das Verzeichnis wechseln müssen, wechseln Sie einfach zurück,
  bevor Sie den jeweils nächsten Schritt angehen.}
 \item{Anpassen von (Konfigurations-)Dateien}
 \item{Installation von Dateien und Neustart von Diensten}
 \item{Löschen der ausgepackten Dateien und Installationsarchive}
\end{itemize}

Bei einem Upgrade möchten Sie ggfs. vorher eine Sicherung erstellen.

\section{Systemvoraussetzungen}\label{sec:requirements}

\ifoa

Die Systemvoraussetzungen unterscheiden sich je nachdem wie Sie die
Komponenten trennen. Im folgenden werden empfohlene Werte für zwei
mögliche Installationsvarianten angegeben (OSIAM getrennt oder mit
auf dem core-System); die Online-Anmeldung („OA“) wird als getrenntes
System ausgeführt, bei einer Zusammenlegung der Online-Anmeldung mit
dem core-System erhöhen sich die Anforderungen minimal.

\begin{tabular}{| r || l | l |}\hline
                  & Minimum    & empfohlen\\\hline\hline
 CPU je VM        & 1          & 1\\\hline
 RAM OSIAM        & 960 MiB    & 1536 MiB\\\hline
 RAM core         & 512 MiB    & 1024 MiB, besser mehr\\\hline
 RAM core + OSIAM & 1024 MiB   & 2048 MiB, besser mehr\\\hline
 RAM OA           & 512 MiB    & 768 MiB\\\hline
 HDD OSIAM        & 2 GB       & 4 GB\\\hline
 HDD core         & 2 GB       & 8 GB\\\hline
 HDD core + OSIAM & 3 GB       & 10 GB\\\hline
 HDD OA           & 1½ GB      & 3 GB\\\hline
 Betriebssystem   & Debian 7   & Debian 7\Hair\textsuperscript{\ref{fn:jessie}}\\\hline
%XXX jessie mit sysvinit vielleicht? Testen!
 Webserver        & Apache 2.2 & Apache 2.2 oder 2.4\Hair\textsuperscript{\ref{fn:apache}}\\\hline
 Java Runtime     & OpenJDK 7  & OpenJDK 7 oder 8\\\hline
\end{tabular}

Bitte beachten Sie bei der Dimensionierung der Maschinen und der
Netzwerkanbindung, daß das Veranstaltungsmanagement (core) nur
durch Ihre Mitarbeiter bedient wird, während die Online-Anmeldung
üblicherweise offen im Internet steht und entsprechender Last
ausgesetzt sein \emph{kann}.

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

VerA.web hat keine eigene Benutzerverwaltung sondern fragt beim
Login eines Nutzers das LDAP-Verzeichnis ab, ob die Kombination
aus Benutzernamen und Paßwort gültig ist. Nur die Zuordnung von
Benutzern (identifiziert durch den Unix-Benutzernamen bzw. die
\texttt{uid}) zu Benutzerrechten (z.B. Administrator oder Lesen
eingeschränkt) und Mandanten wird durch VerA.web gespeichert;
Näheres finden Sie in Kapitel 6.2 (Administration → Benutzer)
im VerA.web-Benutzerhandbuch.

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
(OSIAM kann auf dem System mitinstalliert werden, wenn es nur für
VerA.web verwendet wird)
\fi% oa
mit Debian Linux 7 „wheezy“ als Betriebssystem. Zwar \emph{kann}
die Software auch mit einer anderen Debian-basierten Distribution
(z.B. Ubuntu 14.04 „trusty“) oder einer anderen Java/Tomcat-Umgebung
betrieben werden, allerdings geht diese Installationsanleitung von
der empfohlenen Systemumgebung aus. Eine graphische Oberfläche auf
dem Server wird zum Betrieb nicht benötigt. Eine Installation mit
Debian 8 „jessie“ wird in einer späteren Version dieser Anleitung
berücksichtigt werden.%XXX TODO

Für VerA.web core benötigen Sie mindestens 512 MiB Arbeitsspeicher;
1 GiB oder mehr wird empfohlen. Wir empfehlen eine Festplattengröße
von mindestens 2 GB, besser 8 GB.

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

\Warning \emph{Achtung:} Java™ 6 oder älter wird nicht unterstützt
und führt während der Installation zu Fehlern!

\subsubsection{Mailserver (MTA)}\label{subsubsec:req-core-mta}

Auf dem Server für VerA.web core und REST-API muß ein Mailserver
installiert sein, der lokale Verbindungen auf Port 25 entgegennimmt
und die eingehenden Nachrichten, ggfs. über einen als Smarthost
eingestellten anderen Mailserver, zustellt.

\ifoa

\subsection{Online-Anmeldung}\label{subseq:req-oa}

Zum Betrieb der Online-Anmeldung empfehlen wir ebenfalls eine
eigenständige VM; sie kann allerdings auch auf demselben System
wie der VerA.web core eingerichtet werden. Auch hier wird eine
Installation von Debian Linux 7 „wheezy“ ohne graphisch Oberfläche
vorausgesetzt.

Die Online-Anmeldung hat relativ bescheidene Voraussetzungen
und kann bereits mit 512 MiB Arbeitsspeicher und 1½ GB Festplatte
betrieben werden; die Werte steigen je nach Auslastung entsprechend.

Auch hier benötigen Sie den Apache Webserver und die Java™ 7
Laufzeitumgebung, allerdings keinen Tomcat-Applikationsserver, da
die Online-Anmeldung als sogenannter Microservice implementiert wurde.

\fi% oa

\ifupgradeanleitung
\else

\subsection{Bevor Sie beginnen}\label{subsec:req-prereq}

Im Verlauf der Installation werden Sie mindestens einen Useraccount
in VerA.web als Administrator anlegen. Hierfür informieren Sie sich
bitte vorab über die LDAP-Kürzel (Unix-Loginnamen, üblicherweise das
Attribut \texttt{uid}) des oder der Benutzer, die initial als
VerA.web-Administratoren dienen sollen.

\ifoa
\section{Installation OSIAM-System}\label{sec:setup-osiam}

Dieser Abschnitt gliedert sich in zwei Teile: die generelle Installation
von OSIAM, sofern Sie noch keine laufende OSIAM-Installation haben, und
die Registration der VerA.web Online-Anmeldung beim OSIAM-Server.

Kopieren Sie jedoch bitte zunächst die Datei
\texttt{online-anmeldung-\vwiaverssw{}-files.tgz} (oder ihr
ZIP-Äquivalent; in dieser Anleitung verwenden wir exemplarisch
aber die tar.gz-Variante) auf den OSIAM-Server.

\begin{minipage}{\textwidth}
Entpacken Sie das \texttt{files}-Archiv und wechseln in das
Unterverzeichnis mit den OSIAM-Dateien:

\begin{lstdump}{Entpacken OSIAM}
tar -xzf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉/osiam
chmod go-rwx .
\end{lstdump}
\end{minipage}

Alle weiteren Schritte in diesem Kapitel nehmen an, daß dieses
Unterverzeichnis Ihr aktuelles Arbeitsverzeichnis ist.

Der \texttt{chmod}-Aufruf dient dazu, anderen auf demselben System
eingeloggten Nutzern den Zugriff auf die Konfigurationsdateien zu
verweigern während Sie sie bearbeiten, da sie später Paßwörter
enthalten werden.

\subsection{OSIAM-Server Schnellinstallation}\label{subsec:setup-osiam-fast}

VerA.web benötigt aktuell OSIAM auth-server und resource-server in
der Version \vwiaversosiam{} (enthalten in der OSIAM Distribution
Version \vwiaversodist{}) und ist \emph{nur} mit dieser Version
getestet worden.
\\% XXX TODO besser in Upgradeanleitung, sobald es die gibt
\Warning \emph{Achtung:} OSIAM Server 2.3 und neuer können
nicht von VerA.web-Versionen bevor 1.6.34 angesprochen werden.

Falls Sie bereits eine lauffähige OSIAM-Installation besitzen stellen
Sie bitte sicher, daß auf die vorhandene Installation über HTTPS
zugegriffen werden kann; danach können Sie direkt im nächsten Abschnitt
(\nameref{subsec:setup-osiam-register}) weiterlesen.

\begin{minipage}{\textwidth}
Sofern Sie noch nicht über eine OSIAM-Installation besitzen, bereiten
Sie bitte zunächst das System vor, indem Sie einige Pakete installieren:

\begin{lstdump}{Pakete OSIAM}
sudo apt-get install postgresql openjdk-7-jre-headless tomcat7 \
    apache2-mpm-prefork apache2 libapache2-mod-jk curl
\end{lstdump}
\end{minipage}

Jetzt sind der Datenbankserver, die Java™-Laufzeitumgebung (in der
Version für Server und Webanwendungen), der Tomcat-Applikationsserver,
der Webserver sowie Hilfsprogramme installiert.

\begin{minipage}{\textwidth}
Stellen Sie nun sicher, daß keine ältere Java-Version installiert
ist, damit sie nicht versehentlich verwendet werden kann\Hair\textsuperscript{\ref{fn:jessie}}:

\begin{lstdump}{altes Java entfernen}
sudo apt-get purge openjdk-6-jre-headless gcj-4.6-base gcj-4.7-base
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Legen Sie den Datenbankbenutzer und die Datenbank an:

\begin{lstdump}{DB erstellen OSIAM}
# createuser fragt nach einem zu vergebenden Paßwort
sudo -u postgres createuser -D -P -R -S ong
sudo -u postgres createdb -E UTF-8 -O ong -T template0 ong
\end{lstdump}

Falls Sie hierbei ein anderes Paßwort als \texttt{ong} für
den Datenbanknutzer vergeben müssen Sie jetzt die Angaben
in den Dateien \texttt{auth-server.properties} und
\texttt{resource-server.properties} entsprechend anpassen.
\end{minipage}

\begin{minipage}{\textwidth}
In den Dateien \texttt{auth-server.properties} und
\texttt{resource-server.properties} ersetzen Sie bitte an allen
Stellen \texttt{HOSTNAME-OSIAM} durch den vollen Hostnamen des
OSIAM-Servers. Danach installieren Sie diese:

\begin{lstdump}{/etc/osiam fuellen}
sudo mkdir -p /etc/osiam
sudo chown 0:tomcat7 /etc/osiam
sudo chmod 2750 /etc/osiam
sudo install -c -o 0 -g tomcat7 -m 640 *.properties /etc/osiam/
\end{lstdump}
\end{minipage}

Fügen Sie bitte am Ende der Datei \texttt{/etc/default/tomcat7}
einige Zeilen ein, um weitere JVM-Startoptionen zu setzen.

\begin{minipage}{\textwidth}
Diese Option setzen Sie bitte in jedem Fall, um eine korrekte
Kodierung von Dateien, Logs und internen Abläufen zu gewährleisten:

\begin{lstdump}{Tomcat Encoding setzen}
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Diese (Linux-spezifische) Option spart u.a. beim Starten Zeit,
indem sie eine nichtblockierende Quelle für Zufallszahlen auswählt:

\begin{lstdump}{Tomcat /dev/urandom}
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"
\end{lstdump}
\end{minipage}

Suchen Sie die Stelle, wo \texttt{-Xmx128m} in den \texttt{JAVA\_OPTS}
gesetzt wird. \Warning \emph{Achtung:} das erste Vorkommnis dieser
Option ist ein Kommentar und nicht die gesuchte Stelle! Ändern Sie
diesen Wert auf \texttt{-Xms512m -Xmx1024m} falls die VM über mehr
als 1280 MiB RAM verfügt, \texttt{-Xmx512m} andernfalls.

Falls Sie mehrere Webanwendungen im selben Applikationsserver
betreiben kann es sein, daß Sie den sogenannten PermGen-Space
erhöhen müssen, da der Standardwert von \texttt{64m} nicht
mehr ausreicht. Dies betrifft nur Java vor Version 8.

\begin{minipage}{\textwidth}
Hierzu fügen Sie z.B. folgende Zeile hinzu (oder auch \texttt{128m}):

\begin{lstdump}{Tomcat PermGen-Space}
JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=96m"
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

\begin{minipage}{\textwidth}
Suchen Sie bitte in der Datei \texttt{/etc/tomcat7/catalina.properties}
die Zeile, die mit \texttt{shared.loader=} beginnt, und hängen Sie am
Ende der Zeile \texttt{,/etc/osiam} an; das könnte dann wie folgt aussehen:

\begin{lstdump}{Tomcat OSIAM shared.loader}
shared.loader=/var/lib/tomcat7/shared/classes,/var/lib/tomcat7/shared/*.jar,/etc/osiam
\end{lstdump}
\end{minipage}

Nun starten Sie den Anwendungsserver neu, um die geänderten
Einstellungen wirksam zu machen. Beachten Sie dabei, daß der
Tomcat-Dienst ggfs. noch einige Sekunden weiterläuft, nachdem
der Stop-Befehl abgeschickt wurde; im Zweifelsfalle prüfen Sie
explizit, ob noch \texttt{java}-Prozesse laufen.\keinumbruch

\begin{minipage}{\textwidth}
\begin{lstdump}{Tomcat restart}
sudo service tomcat7 stop
ps ax | grep java # sollte maximal grep anzeigen
sudo service tomcat7 start
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Jetzt laden Sie die OSIAM-Anwendungen herunter und installieren Sie sie:

\begin{lstdump}{OSIAM herunterladen}
curl -o osiam-auth-server.war https://maven-repo.evolvis.org/releases/org/osiam/auth-server/〈\lstdumpesc{\vwiaversosiam}〉/auth-server-〈\lstdumpesc{\vwiaversosiam}〉.war
curl -o osiam-resource-server.war https://maven-repo.evolvis.org/releases/org/osiam/resource-server/〈\lstdumpesc{\vwiaversosiam}〉/resource-server-〈\lstdumpesc{\vwiaversosiam}〉.war
sudo install -c -o 0 -g 0 -m 644 *.war /var/lib/tomcat7/webapps/
\end{lstdump}
\end{minipage}

Warten Sie einige Zeit, bis beide Artefakte deployt wurden; in
\texttt{/var/log/tomcat7/catalina.out} befinden sich die Logs.

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
Nach einem erfolgreichen Start können Sie prüfen, ob sich der
Dienst ansprechen läßt:

\begin{lstdump}{OSIAM ansprechen}
curl https://$(hostname -f)/osiam-resource-server/ServiceProviderConfigs; echo
\end{lstdump}

Sie sollten hier eine Ausgabe im JSON-Format sehen, die erwähnt,
daß OAuth 2 verwendet wird.
\end{minipage}

Im Abschnitt \nameref{sec:links} finden Sie einen Link auf die
offizielle OSIAM-Installationsanleitung mit weitergehenden
Informationen, u.a. wie man den Standardzugang ändert.

\subsection{Registrierung von VerA.web mit OSIAM}\label{subsec:setup-osiam-register}

Falls Sie die Installation bisher als \texttt{root} und ohne
\texttt{sudo} durchgeführt haben vergewissern Sie sich bitte,
daß \texttt{sudo} dennoch verfügbar ist; ggfs. fügen Sie den
Paketnamen (\texttt{sudo} oder \texttt{sudo-ldap}, je nach
Ihrer Umgebung) der untenstehenden Installationszeile hinzu.

\begin{minipage}{\textwidth}
Zunächst müssen einige benötigte Hilfsprogramme installiert werden:

\begin{lstdump}{Pakete register-into-osiam}
sudo apt-get install curl mksh
\end{lstdump}
\end{minipage}

In der Datei \texttt{register-veraweb-into-osiam.sh} finden
Sie nun einige ggfs. zu ändernde Stellen rund um Zeile 30:
\keinumbruch

\begin{itemize}
 \item{\texttt{dbname}: der Datenbankname von OSIAM}
 \item{\texttt{serverauth}: der OSIAM-Standardzugang,
  falls nicht geändert}
 \item{\texttt{vwsecret}: das Paßwort, mit dem sich
  hinterher VerA.web bei OSIAM identifizieren wird}
\end{itemize}

\begin{minipage}{\textwidth}
Nun rufen Sie dieses Skript bitte auf:

\begin{lstdump}{register VW into OSIAM}
sudo mksh register-veraweb-into-osiam.sh
\end{lstdump}
\end{minipage}

Das Skript fügt zunächst die VerA.web-spezifischen Daten
zum SCIM-Schema hinzu (und benötigt daher Datenbankzugriff,
weshalb die Datenbank für die vereinfachte Installation
auf demselben Server laufen muß wie OSIAM selbst) und
registriert danach über die REST-Schnittstelle von OSIAM
VerA.web als Client.

\begin{minipage}{\textwidth}
Nach erfolgreichem Abschluß können Sie nun die
Installationsdateien löschen:

\begin{lstdump}{Aufraeumen OSIAM}
cd ../..
rm -rf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉*
\end{lstdump}
\end{minipage}

\fi% oa

\section{Installation internes VerA.web-System}\label{sec:setup-int}

\ifoa
Kopieren Sie bitte zunächst die „\texttt{files}-Dateien“
\texttt{veraweb-core-\vwiaverssw{}-files.tgz} und\linebreak[1]
\texttt{online-anmeldung-\vwiaverssw{}-files.tgz}
\else% !oa
Kopieren Sie bitte zunächst die Datei
\texttt{veraweb-core-\vwiaverssw{}-files.tgz}
\fi% !oa
(oder deren ZIP-Äquivalent; in dieser Anleitung verwenden
wir die tar.gz-Variante) sowie die Installationsarchive
\texttt{veraweb-core-\vwiaverssw{}.war}
und \texttt{rest-api-\vwiaverssw{}.war} auf den core-Server.

\begin{minipage}{\textwidth}
Benennen Sie die Applikationsdateien in später leicher zu
verwendende Namen um, entpacken Sie das Zusatzarchiv und
wechseln Sie in das Unterverzeichnis mit den core-Dateien:

\ifoa
\begin{lstdump}{Entpacken}
mv veraweb-core-〈\lstdumpesc{\vwiaverssw}〉.war veraweb.war
mv rest-api-〈\lstdumpesc{\vwiaverssw}〉.war vwor.war
tar -xzf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd veraweb-core-〈\lstdumpesc{\vwiaverssw}〉
tar -xzf ../online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
mv online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉/core/* .
chmod go-rwx .
\end{lstdump}
\else% !oa
\begin{lstdump}{Entpacken}
mv veraweb-core-〈\lstdumpesc{\vwiaverssw}〉.war veraweb.war
mv rest-api-〈\lstdumpesc{\vwiaverssw}〉.war vwor.war
tar -xzf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd veraweb-core-〈\lstdumpesc{\vwiaverssw}〉
chmod go-rwx .
\end{lstdump}
\fi% !oa
\end{minipage}

Alle weiteren Schritte in diesem Kapitel nehmen an, daß dieses
Unterverzeichnis Ihr aktuelles Arbeitsverzeichnis ist.

Der \texttt{chmod}-Aufruf dient dazu, anderen auf demselben System
eingeloggten Nutzern den Zugriff auf die Konfigurationsdateien zu
verweigern während Sie sie bearbeiten, da sie später Paßwörter
enthalten werden.

Die meisten enthaltenen Dateien werden im Verlauf der Einrichtung
verwendet; alle übrigen dienen als Beispiele, Vorlagen oder Hilfen
bei der Anpassung; zusätzlich ist das Benutzerhandbuch, welches
später in der Anwendung auch als Online-Hilfe verfügbar ist, als
\texttt{Benutzerhandbuch.pdf} mitgeliefert, sodaß Sie dieses bereits
vorab Ihren Sachbearbeitern zukommen lassen können.

\begin{minipage}{\textwidth}
Installieren Sie einige Pakete:

\begin{lstdump}{Pakete core}
sudo apt-get install postgresql openjdk-7-jre-headless tomcat7 \
    apache2-mpm-prefork apache2 libapache2-mod-jk curl
\end{lstdump}
\end{minipage}

Jetzt sind der Datenbankserver, die Java™-Laufzeitumgebung (in der
Version für Server und Webanwendungen), der Tomcat-Applikationsserver,
der Webserver sowie Hilfsprogramme installiert.

\begin{minipage}{\textwidth}
Stellen Sie nun sicher, daß keine ältere Java-Version installiert
ist, damit sie nicht versehentlich verwendet werden kann\Hair\textsuperscript{\ref{fn:jessie}}:

\begin{lstdump}{altes Java entfernen}
sudo apt-get purge openjdk-6-jre-headless gcj-4.6-base gcj-4.7-base
\end{lstdump}
\end{minipage}

\subsection{Datenbank VerA.web-core}\label{subsec:setup-core-db}

Im folgenden Abschnitt wird die Datenbank für VerA.web mit dem
Datenbankschema sowie den Stammdaten und Fachadministratorenzugängen
eingerichtet.

\begin{minipage}{\textwidth}
Legen Sie zunächst den Datenbankbenutzer und die Datenbank an:

\begin{lstdump}{DB erstellen}
# createuser fragt nach einem zu vergebenden Paßwort
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

\begin{lstdump}{DB Login}
psql -U veraweb -h 127.0.0.1 -W veraweb
\end{lstdump}
\end{minipage}

Falls Sie andere Werte für Datenbankserver, Port, Benutzer oder Datenbankname
verwenden müssen Sie diese hier selbstverständlich anpassen.

\begin{minipage}{\textwidth}
Dann setzen Sie im PostgreSQL-Client folgende Kommandos ab:

\begin{lstdump}{DB Schema Init Beginn}
\i schema.sql
SELECT serv_verawebschema(1);
\i stammdaten-full.sql
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Nun müssen Sie diejenigen LDAP-Benutzer, die initial als Administratoren
agieren sollen, der Software bekanntgeben. Setzen Sie hierzu für jeden
LDAP-Benutzer einen SQL-Befehl entsprechend dem folgenden ab, wobei Sie
statt \texttt{vnnnnn} das LDAP-Kürzel (Unix-Username, \texttt{uid}) des
jeweiligen Benutzers einseitzen:

\begin{lstdump}[language=SQL]{DB Admin-User anlegen}
INSERT INTO veraweb.tuser (fk_orgunit, username, role)
    VALUES (NULL, 'vnnnnn', 5);
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Schließlich müssen einige Sequenzen initialisiert und das Datenbankschema
auf die jeweils aktuelle Version aktualisiert werden; danach können Sie
den \texttt{psql}-Client wieder verlassen:

\begin{lstdump}{DB Schema Init Ende}
SELECT veraweb.serv_build_sequences();
\i upgrade.sql
\q
\end{lstdump}
\end{minipage}

\subsection{Konfiguration VerA.web-core und REST-API}\label{subsec:setup-core-files}

Passen Sie in der \texttt{veraweb.properties} bitte den Hostnamen
in der \texttt{vwor.endpoint}-Eigenschaft an (ersetzen Sie
\texttt{HOSTNAME-REST} durch den FQDN des Servers, über den die
REST-API erreichbar ist, üblicherweise also den Hostnamen vom
core-System) und tragen Sie die Zugangsdaten für die REST-API
(siehe \nameref{subsec:intro-restapi}) als \texttt{vwor.auth.user}
und \texttt{vwor.auth.password} ein.
\ifoa
Desweiteren ersetzen Sie bitte \texttt{HOSTNAME-OSIAM} und
\texttt{HOSTNAME-OA} an allen Stellen durch die Hostnamen
des OSIAM- bzw. des Online-Anmeldungssystems. Schließlich
tragen Sie für \texttt{osiam.client.secret} denselben Wert
ein, den Sie bei der \nameref{subsec:setup-osiam-register}
als \texttt{vwsecret} gesetzt haben.
\fi% oa

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis an, in dem Sie die hochgeladenen
Bilddateien speichern wollen (wir empfehlen ein Unterverzeichnis,
sodaß es später möglich ist, weitere Strukturen aufzubauen), und
machen Sie es für den Tomcat-Nutzer schreibbar:

\begin{lstdump}{Uploaded anlegen}
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

\subsection{Applikationsserver VerA.web-core und REST-API}\label{subsec:setup-core-tomcat}

\begin{minipage}{\textwidth}
Legen Sie ein neues Verzeichnis \texttt{/etc/veraweb} mit einem
Unterverzeichnis \texttt{l10n} an und kopieren Sie die Konfigurations-
und Sprachdateien dorthin:

\begin{lstdump}{/etc/veraweb fuellen}
sudo mkdir -p /etc/veraweb/l10n
sudo chown 0:tomcat7 /etc/veraweb
sudo chmod 2750 /etc/veraweb
sudo install -c -o 0 -g tomcat7 -m 640 config_*.xml /etc/veraweb/
sudo install -c -o 0 -g tomcat7 -m 640 *.properties /etc/veraweb/
sudo install -c -o 0 -g 0 -m 644 l10n/*.resource /etc/veraweb/l10n/
\end{lstdump}
\end{minipage}

Fügen Sie bitte am Ende der Datei \texttt{/etc/default/tomcat7}
einige Zeilen ein, um weitere JVM-Startoptionen zu setzen.

\begin{minipage}{\textwidth}
Diese Option setzen Sie bitte in jedem Fall, um eine korrekte
Kodierung von Dateien, Logs und internen Abläufen zu gewährleisten:

\begin{lstdump}{Tomcat Encoding setzen}
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Diese (Linux-spezifische) Option spart u.a. beim Starten Zeit,
indem sie eine nichtblockierende Quelle für Zufallszahlen auswählt:

\begin{lstdump}{Tomcat /dev/urandom}
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

Nun starten Sie den Anwendungsserver neu, um die geänderten
Einstellungen wirksam zu machen. Beachten Sie dabei, daß der
Tomcat-Dienst ggfs. noch einige Sekunden weiterläuft, nachdem
der Stop-Befehl abgeschickt wurde; im Zweifelsfalle prüfen Sie
explizit, ob noch \texttt{java}-Prozesse laufen.\keinumbruch

\begin{minipage}{\textwidth}
\begin{lstdump}{Tomcat restart}
sudo service tomcat7 stop
ps ax | grep java # sollte maximal grep anzeigen
sudo service tomcat7 start
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Deployen Sie die VerA.web-Anwendung und die REST-API in den Applikationsserver:

\begin{lstdump}{Tomcat deploy}
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo install -c -o 0 -g 0 -m 644 ../vwor.war /var/lib/tomcat7/webapps/
\end{lstdump}
\end{minipage}

Warten Sie ca. ½ Minute, bis beide Artefakte deployt wurden; in
\texttt{/var/log/tomcat7/catalina.out} befinden sich die Logs.
\ifoa
Falls OSIAM (oder andere Webanwendungen) auf demselben System
installiert werden kann der Startvorgang deutlich länger dauern.
\fi% oa

\subsection{Webserver VerA.web-core und REST-API}\label{subsec:setup-core-apache}

\begin{minipage}{\textwidth}
Aktivieren Sie zunächst das \texttt{jk}-Modul, welches benutzt wird,
um vermittels des AJP-Protokolls Anfragen an den Webserver an Tomcat
durchzureichen:

\begin{lstdumpx}
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

\begin{lstdump}{Apache neustarten}
sudo service apache2 stop
sudo service apache2 start
\end{lstdump}
\end{minipage}

\subsection{Funktionstest}\label{subsec:setup-core-check}

\begin{minipage}{\textwidth}
Nun können Sie die Lauffähigkeit der REST-API z.B. mit cURL prüfen:

\begin{lstdump}{REST-API check}
curl https://veraweb:veraweb@$(hostname -f)/vwor/rest/available/; echo
\end{lstdump}

Dieser Befehl gibt \texttt{OK} aus, wenn die REST-API läuft und
Benutzername und Paßwort (die beiden \texttt{veraweb} hinter
dem \texttt{https://}) korrekt sind.
\end{minipage}

Jetzt können Sie z.B. über \texttt{https://veraweb.lan.tarent.de/veraweb/}
auf das Veranstaltungsmanagement zugreifen und sich einloggen. Falls
hierbei Fehler auftauchen prüfen Sie bitte den LDAP-Zugriff und die Logs.

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr; diese können nun entfernt werden:

\ifoa
\begin{lstdump}{Aufraeumen}
cd ..
rm -rf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉*
rm -rf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉* veraweb.war vwor.war
\end{lstdump}
\else% !oa
\begin{lstdump}{Aufraeumen}
cd ..
rm -rf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉* veraweb.war vwor.war
\end{lstdump}
\fi% !oa
\end{minipage}

\ifoa

\section{Installation Online-Anmeldung}\label{sec:setup-oa}

\begin{minipage}{\textwidth}
Kopieren Sie bitte zunächst \texttt{online-anmeldung-\vwiaverssw{}-files.tgz}
(oder das ZIP-Äquivalent) und \texttt{online-anmeldung-\vwiaverssw{}.jar}
auf den Server, auf dem Sie die Online-Anmeldung installieren möchten,
entpacken Sie diese Datei und wechseln in das Unterverzeichnis mit den
relevanten Dateien:

\begin{lstdump}{Entpacken OA}
tar -xzf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉/vwoa
chmod go-rwx ..
\end{lstdump}
\end{minipage}

Alle weiteren Schritte in diesem Kapitel nehmen an, daß dieses
Unterverzeichnis Ihr aktuelles Arbeitsverzeichnis ist.

Der \texttt{chmod}-Aufruf dient dazu, anderen auf demselben System
eingeloggten Nutzern den Zugriff auf die Konfigurationsdateien zu
verweigern während Sie sie bearbeiten, da sie später Paßwörter
enthalten werden.

\begin{minipage}{\textwidth}
Setzen Sie nun die benötigten Pakete und Vorbedingungen auf:

\begin{lstdump}{Prereqs OA}
sudo apt-get install openjdk-7-jre-headless \
    apache2-mpm-prefork apache2 daemontools-run adduser
sudo adduser --system --home /etc/service/vwoa --shell /bin/sh \
    --no-create-home --gecos 'VerA.web Online-Anmeldung' --group \
    --disabled-password --disabled-login vwoa
sudo ln -sf etc/service /service
\end{lstdump}
\end{minipage}

\Warning \emph{Achtung:} Das \texttt{daemontools-run}-Paket bedingt
die Nutzung von SysV init (Paket \texttt{sysvinit-core} ab Debian 8
„jessie“\Hair\textsuperscript{\ref{fn:jessie}}) statt systemd!

Bearbeiten Sie jetzt die \texttt{config.json}, indem Sie wieder
\texttt{HOSTNAME-OSIAM} und \texttt{HOSTNAME-REST} durch die
entsprechenden Hostnamen ersetzen; für \texttt{clientSecret}
tragen Sie denselben Wert ein, den Sie bei der
\nameref{subsec:setup-osiam-register} als \texttt{vwsecret}
gesetzt haben; unterhalb von \texttt{restauth} tragen Sie die
von Ihnen gewählten Zugangsdaten der REST-API gemäß Kapitel
\ref{subsec:intro-restapi} ein.

\begin{minipage}{\textwidth}
Nun richten Sie den Dienst für die Online-Anmeldung ein und
löschen danach die Installationsdateien:

\begin{lstdump}{OA service}
mv ../../online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉.jar vwoa.jar
mv config.json run vwoa/
chown -R 0:0 .
chgrp vwoa config.json
chmod 644 *
chmod 755 vwoa run
chmod 640 config.json
cd ..
mv vwoa /service/vwoa
cd ..
rm -rf online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉*
\end{lstdump}
\end{minipage}

Danach können Sie mit \texttt{svstat /service/vwoa} den Zustand
des Dienstes (\texttt{up} oder \texttt{down}) prüfen; unter
\texttt{/var/log/vwoa/vwoa.log} finden Sie die Logausgaben der
Online-Anmeldung. Mit dem Befehl \texttt{svc} können Sie den Dienst
kontrollieren. Weitergehende Angaben hierzu finden Sie in der
Dokumentation der DJB daemontools.
%XXX add a primer here

\begin{minipage}{\textwidth}
Setzen Sie den Webserver äquivalent zu \nameref{subsec:setup-core-apache}
auf, allerdings nicht mit dem \texttt{jk}-Modul sondern mit:

\begin{lstdumpx}
a2enmod proxy_http
\end{lstdumpx}
\end{minipage}

\begin{minipage}{\textwidth}
Die vhost-Einträge sind:

\begin{lstdump}{OA workers}
ProxyPass /vwor-app/ http://localhost:8081/
ProxyPassReverse /vwor-app/ http://localhost:8081/
\end{lstdump}
\end{minipage}

Im Regelfall haben Sie die Online-Anmeldung auf einem getrennten
Webserver installiert; da sie sich URL-mäßig unterhalb von
\texttt{/vwor-app/} befindet ist auch hier, ähnlich dem core,
eine Weiterleitung von \texttt{/} sinnvoll.

\begin{minipage}{\textwidth}
Der hierzu nötige vhost-Eintrag ist:

\begin{lstdump}{OA redirect}
RedirectMatch 301 ^/*$ https://veraweb.lan.tarent.de/vwor-app/
\end{lstdump}
\end{minipage}

\fi% oa

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

\ifoa
Kopieren Sie bitte zunächst die „\texttt{files}-Dateien“
\texttt{veraweb-core-\vwiaverssw{}-files.tgz} und\linebreak[1]
\texttt{online-anmeldung-\vwiaverssw{}-files.tgz}
\else% !oa
Kopieren Sie bitte zunächst die Datei
\texttt{veraweb-core-\vwiaverssw{}-files.tgz}
\fi% !oa
(oder deren ZIP-Äquivalent; in dieser Anleitung verwenden
wir die tar.gz-Variante) sowie die Installationsarchive
\texttt{veraweb-core-\vwiaverssw{}.war}
und \texttt{rest-api-\vwiaverssw{}.war} auf den core-Server.

\begin{minipage}{\textwidth}
Benennen Sie die Applikationsdateien in später leicher zu
verwendende Namen um, entpacken Sie das Zusatzarchiv und
wechseln Sie in das Unterverzeichnis mit den core-Dateien:

\ifoa
\begin{lstdump}{Entpacken}
mv veraweb-core-〈\lstdumpesc{\vwiaverssw}〉.war veraweb.war
mv rest-api-〈\lstdumpesc{\vwiaverssw}〉.war vwor.war
tar -xzf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd veraweb-core-〈\lstdumpesc{\vwiaverssw}〉
tar -xzf ../online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
mv online-anmeldung-〈\lstdumpesc{\vwiaverssw}〉/core/* .
chmod go-rwx .
\end{lstdump}
\else% !oa
\begin{lstdump}{Entpacken}
mv veraweb-core-〈\lstdumpesc{\vwiaverssw}〉.war veraweb.war
mv rest-api-〈\lstdumpesc{\vwiaverssw}〉.war vwor.war
tar -xzf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉-files.tgz
cd veraweb-core-〈\lstdumpesc{\vwiaverssw}〉
chmod go-rwx .
\end{lstdump}
\fi% !oa
\end{minipage}

Alle weiteren Schritte in diesem Kapitel nehmen an, daß dieses
Unterverzeichnis Ihr aktuelles Arbeitsverzeichnis ist.

Der \texttt{chmod}-Aufruf dient dazu, anderen auf demselben System
eingeloggten Nutzern den Zugriff auf die Konfigurationsdateien zu
verweigern während Sie sie bearbeiten, da sie später Paßwörter
enthalten werden.

\begin{minipage}{\textwidth}
Falls das Verzeichnis \texttt{/etc/veraweb/l10n} noch nicht existiert,
legen Sie es an. Kopieren Sie dann die aktuellen Sprachdateien dorthin:

\begin{lstdump}{/etc/veraweb/l10n fuellen}
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

Falls Sie von einer VerA.web-Version, die die Konfigurationsdateien noch
nicht in \texttt{/etc/veraweb} ablegt, aktualisieren, müssen Sie dort
zunächst (nur) die gegenüber den in der alten Version der \texttt{veraweb.war}
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

\begin{lstdump}{Uploaded anlegen}
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

\begin{lstdump}{/etc/veraweb/*.properties}
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

Fügen Sie bitte am Ende der Datei \texttt{/etc/default/tomcat7}
einige Zeilen ein, um weitere JVM-Startoptionen zu setzen, die
in alten Installationen ggfs. noch nicht gesetzt waren.

\begin{minipage}{\textwidth}
Diese Option setzen Sie bitte in jedem Fall, um eine korrekte
Kodierung von Dateien, Logs und internen Abläufen zu gewährleisten:

\begin{lstdump}{Tomcat Encoding setzen}
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Diese (Linux-spezifische) Option spart u.a. beim Starten Zeit,
indem sie eine nichtblockierende Quelle für Zufallszahlen auswählt:

\begin{lstdump}{Tomcat /dev/urandom}
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"
\end{lstdump}
\end{minipage}

Nun muß noch die Konfiguration von Tomcat für die neu hinzugekommene
REST-API vorgenommen werden.

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

\subsection{Datenbank VerA.web-core}\label{subsec:upgrade-core-db}

\begin{minipage}{\textwidth}
Loggen Sie sich auf der Datenbank ein und starten Sie das Upgrade-SQL:

\begin{lstdump}{DB-Upgrade}
psql -U veraweb -h 127.0.0.1 -W veraweb <upgrade.sql
\end{lstdump}

Falls Sie andere Werte für Datenbankserver, Port, Benutzer oder Datenbankname
verwenden müssen Sie diese hier selbstverständlich anpassen.
\end{minipage}

\subsection{Applikationsserver VerA.web-core}\label{subsec:upgrade-core-tomcat}

\begin{minipage}{\textwidth}
Stoppen Sie bitte zunächst den Applikationsserver:

\begin{lstdump}{Tomcat undeploy}
sudo service tomcat7 stop
ps ax | grep java # sollte maximal grep anzeigen
\end{lstdump}

Beachten Sie dabei, daß der Tomcat-Dienst ggfs. noch einige Sekunden
weiterläuft, nachdem der Stop-Befehl abgeschickt wurde; im Zweifelsfalle
prüfen Sie explizit, ob noch \texttt{java}-Prozesse laufen.
\end{minipage}

\begin{minipage}{\textwidth}
Dann löschen Sie die alten und deployen die neuen Fassungen der
VerA.web-Kernanwendung und der REST-API und starten den Server wieder:

\begin{lstdump}{Tomcat deploy}
sudo rm -rf /var/lib/tomcat7/webapps/veraweb*
sudo rm -rf /var/lib/tomcat7/webapps/vwor*
sudo install -c -o 0 -g 0 -m 644 ../veraweb.war /var/lib/tomcat7/webapps/
sudo install -c -o 0 -g 0 -m 644 ../vwor.war /var/lib/tomcat7/webapps/
sudo service tomcat7 start
\end{lstdump}
\end{minipage}

\begin{minipage}{\textwidth}
Die heruntergeladenen und entpackten Dateien benötigen Sie nun nicht
mehr; diese können nun entfernt werden:

\begin{lstdump}{Aufraeumen}
cd ..
rm -rf veraweb-core-〈\lstdumpesc{\vwiaverssw}〉* veraweb.war vwor.war
\end{lstdump}
\end{minipage}

Damit ist das Upgrade abgeschlossen.

\fi% !ifoa - XXX FIXME

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

\section{Referenzkonfigurationen}\label{sec:refcfg}

In diesem Kapitel finden Sie Beispiele für Konfigurationsdateien
auf einem Testsystem (eine VM). Hierbei wurden an allen Stellen
die Standardpaßwörter gewählt; diese Beispiele sind strikt als
nicht-normative Dokumentation zu verstehen.

% Ab hier keine minipages mehr drumherum
% (außer um ganz kleine Beispieldateien?)
\lstset{backgroundcolor=\color{black!7},frame=none}

\subsection{/etc/apache2/sites-enabled/000-default}\label{subsec:refcfg-apache-default}

\ifoa
Diese Datei ist quasi identisch, nur das Ziel der Weiterleitung
unterscheidet sich, je nach Host. Betrifft: alle
\fi% oa

\begin{lstdump}{/etc/apache2/sites-enabled/000-default}
%TODO
\end{lstdump}

\ifoa
\subsection{/etc/apache2/sites-enabled/default-ssl}\label{subsec:refcfg-apache-defssl}

Dieser vhost enthält kommentierte Alternativen für den core
(mit und ohne OSIAM) und die Online-Anmeldung (separat oder
auf dem core-System mitinstalliert). Bitte beachten Sie die
inline-Kommentare genau! Betrifft: alle

\begin{lstdump}{/etc/apache2/sites-enabled/default-ssl +OA}
%TODO
\end{lstdump}

\subsection{/etc/default/tomcat7}\label{subsec:refcfg-tcdef}

Dieses Beispiel ermöglicht es, alles auf einer VM zu betreiben;
siehe auch inline-Kommentare. Betrifft: OSIAM, core

\begin{lstdump}{/etc/default/tomcat7 all-in-one}
%TODO
\end{lstdump}

\else% !oa

\subsection{/etc/apache2/sites-enabled/default-ssl}\label{subsec:refcfg-apache-defssl}

\begin{lstdump}{/etc/apache2/sites-enabled/default-ssl}
%TODO
\end{lstdump}

\subsection{/etc/default/tomcat7}\label{subsec:refcfg-tcdef}

\begin{lstdump}{/etc/default/tomcat7}
%TODO
\end{lstdump}

\fi% !oa
% ↑ shared; ↓ OSIAM/OA-specific
\ifoa

\subsection{/etc/osiam/auth-server.properties}\label{subsec:refcfg-osiam-auth}

Betrifft: OSIAM

\begin{lstdump}{/etc/osiam/auth-server.properties}
%TODO
\end{lstdump}

\subsection{/etc/osiam/resource-server.properties}\label{subsec:refcfg-osiam-rsrc}

Betrifft: OSIAM

\begin{lstdump}{/etc/osiam/resource-server.properties}
%TODO
\end{lstdump}

\subsection{/etc/service/vwoa/config.json}\label{subsec:refcfg-oa-cfg}

Betrifft: Online-Anmeldung

\begin{lstdump}{/etc/service/vwoa/config.json}
%TODO
\end{lstdump}

\subsection{/etc/tomcat7/catalina.properties}\label{subsec:refcfg-tc-prop}

Betrifft: OSIAM

\begin{lstdump}{/etc/tomcat7/catalina.properties}
%TODO
\end{lstdump}

\fi% oa

\subsection{/etc/tomcat7/context.xml}\label{subsec:refcfg-tc-ctx}

\ifoa
Betrifft: core
\fi% oa

\begin{lstdump}{/etc/tomcat7/context.xml}
%TODO
\end{lstdump}

\subsection{/etc/tomcat7/server.xml}\label{subsec:refcfg-tc-server}

\ifoa
Betrifft: OSIAM, core
\fi

\begin{lstdump}{/etc/tomcat7/server.xml}
%TODO
\end{lstdump}

\subsection{/etc/tomcat7/tomcat-users.xml}\label{subsec:refcfg-tc-users}

\ifoa
Betrifft: core
\fi% oa

\begin{lstdump}{/etc/tomcat7/tomcat-users.xml}
%TODO
\end{lstdump}

\subsection{/etc/veraweb/config\_ldap\_access.xml}\label{subsec:refcfg-vw-cfgldap}

\ifoa
Betrifft: core
\fi% oa

\begin{lstdump}{/etc/veraweb/config_ldap_access.xml}
%TODO
\end{lstdump}

\ifoa

\subsection{/etc/veraweb/veraweb.properties}\label{subsec:refcfg-vw-vwprop}

Betrifft: core

\begin{lstdump}{/etc/veraweb/veraweb.properties +OA}
%TODO
\end{lstdump}

\else% !oa

\subsection{/etc/veraweb/veraweb.properties}\label{subsec:refcfg-vw-vwprop}

\begin{lstdump}{/etc/veraweb/veraweb.properties}
%TODO
\end{lstdump}

\fi% !oa

\subsection{/etc/veraweb/vwor.properties}\label{subsec:refcfg-vw-vworprop}

\ifoa
Betrifft: core
\fi% oa

\begin{lstdump}{/etc/veraweb/vwor.properties}
%TODO
\end{lstdump}
