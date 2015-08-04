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
% Textabschnitt 2.1: LDAP wird vorausgesetzt

\subsection{LDAP-Verzeichnisdienst}\label{subsec:req-ldap}

Sie benötigen ein LDAP-Verzeichnis, in dem Sie die Zugänge (user
accounts) für die Sachbearbeiter, die VerA.web benutzen sollen,
sowie Administratorenzugänge pflegen. Hierfür wird OpenLDAP empfohlen.

Die Einrichtung eines solchen LDAP-Verzeichnisses ist außerhalb
des Rahmens dieser Installationsanleitung.

Für eine kleine Site ist es möglich, den LDAP-Server (Debian-Pakete
\texttt{slapd} und \texttt{ldap-utils} sowie, als einfach bedienbare
Oberfläche, \texttt{ldap-account-manager}) auf demselben Server wie
die interne Veranstaltungs- und Adreßverwaltung laufenzulassen; dies
erhöht die Systemvoraussetzungen nicht.
