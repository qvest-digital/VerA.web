% VerA.web (public) Installationsanleitungstextteil
%
% Copyright © 2015
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
% Textabschnitt 1: Einleitung

\section{Einleitung}\label{sec:intro}

„VerA.web“ steht für Veranstaltungs- und Adreßverwaltung im „web“ (Internet).
VerA.web ist eine Open Source-Webanwendung, die die IT-gestützte Planung und
Durchführung von Anlässen, Konferenzen und anderen Veranstaltungen maßgeblich
unterstützt. Dieses Dokument beschreibt, wie VerA.web core (die eigentliche
Veranstaltungsmanagementsoftware) durch einen Systemadministrator eigenständig
installiert werden kann.

\section{Systemvoraussetzungen}\label{sec:requirements}

Im folgenden werden die Systemvoraussetzungen und empfohlene Umgebung
für VerA.web beschrieben, zunächst kurz tabellarisch, dann im Fließtext.

\begin{tabular}{| r || l | l |}\hline
                 & Minimum    & empfohlen\\\hline\hline
 CPU             & 1          & 1\\\hline
 Arbeitsspeicher & 512 MiB    & 1 GiB, besser mehr\\\hline
 Festplatte      & 2 GB       & 8 GB\\\hline
 Betriebssystem  & Debian 7   & Debian 7 oder 8\textsuperscript{\ref{fn:jessie}}\\\hline
 Webserver       & Apache 2.2 & Apache 2.2 oder 2.4\textsuperscript{\ref{fn:apache}}\\\hline
 Java Runtime    & OpenJDK 7  & OpenJDK 7\\\hline
\end{tabular}
