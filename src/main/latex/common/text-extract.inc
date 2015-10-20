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
% Entpacken von Dateien (core, vwor)

\begin{minipage}{\textwidth}
Benennen Sie die Datei \texttt{veraweb-core-\vwiaverssw{}.war}
in \texttt{veraweb.war} sowie \texttt{rest-api-\vwiaverssw{}.war}
in \texttt{vwor.war} um. Entpacken Sie die Datei
\texttt{veraweb-core-\vwiaverssw{}-files.tgz} (oder
\texttt{.zip}) und wechseln Sie in das angelegte neue Verzeichnis
(im Beispiel wird die Unix-Variante mit \texttt{.tgz} verwendet):

\begin{lstlisting}[language=sh]
mv veraweb-core-〈\vwiaverssw{}〉.war veraweb.war
mv rest-api-〈\vwiaverssw{}〉.war vwor.war
tar -xzf veraweb-core-〈\vwiaverssw{}〉-files.tgz
cd veraweb-core-〈\vwiaverssw{}〉
\end{lstlisting}
\end{minipage}
