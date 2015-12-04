% VerA.web (public) Installationsanleitungstextbaustein
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
% vwor wird nun auch ohne OA verwendet (Configs)

\begin{minipage}{\textwidth}
Passen Sie in der \texttt{veraweb.properties} bitte den Hostnamen
in der \texttt{vwor.endpoint}-Eigenschaft an und tragen Sie die
Zugangsdaten für die REST-API (siehe \nameref{subsec:intro-restapi})
als \texttt{vwor.auth.user} und \texttt{vwor.auth.password} ein.

\ifoa
\Warning \emph{Achtung:} Bei Installation mit Online-Anmeldung
benutzen Sie bitte die \texttt{core/veraweb.properties} aus dem
Dateipaket der Online-Anmeldung, die mehr Einstellungen enthält!
\fi
\end{minipage}

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
