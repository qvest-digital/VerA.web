% VerA.web (public) Installationsanleitungstextteil
%
% Copyright © 2015
%	Thorsten „mirabilos“ Glaser <t.glaser@tarent.de>
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

\section{blah}\label{sec:blah}\label{subsec:blah}

First page. normal \textbf{bold \textit{bolditalic}} \textit{italic} normal

\begin{lstlisting}[language=c]
task main() {
 SetSensorTouch(IN_1);
 OnFwd(OUT_AB, 75);
 while (Sensor(IN_1) == 0) {}
 Off(OUT_AB);
}
\end{lstlisting}
