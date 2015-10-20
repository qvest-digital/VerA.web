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
% vwor wird nun auch ohne OA verwendet (Tomcat-Teil)

\begin{minipage}{\textwidth}
Fügen Sie bitte am Ende der Datei \texttt{/etc/default/tomcat7} (wenn Sie
nicht Debian benutzen, an einer äquivalenten Stelle) folgende Zeilen ein,
um weitere JVM-Startoptionen zu setzen:

\begin{lstlisting}[language=sh]
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"
\end{lstlisting}
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

\begin{lstlisting}[language=XML]
<Resource name="jdbc/vwonlinereg" auth="Container" type="javax.sql.DataSource"
 driverClassName="org.postgresql.Driver" username="veraweb" password="veraweb"
 url="jdbc:postgresql://localhost:5432/veraweb" maxActive="8" maxIdle="4" />
\end{lstlisting}

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

\begin{lstlisting}[language=XML]
<role rolename="veraweb" />
<user roles="veraweb" username="veraweb" password="veraweb" />
\end{lstlisting}
\end{minipage}
