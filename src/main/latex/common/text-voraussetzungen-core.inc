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
% Textabschnitt 2.2: Systemvoraussetzungen VerA.web-core

\subsection{Interne Veranstaltungs- und Adreßverwaltung}\label{subsec:req-core}

Eine VM mit mindestens 512 MiB RAM und 512 MiB Swap wird empfohlen.
Als Betriebssystem legen wir Debian 7 „wheezy“ nahe; die Software
\emph{kann} jedoch auch mit einer anderen Debian-basierten Distribution
(z.B. Ubuntu 14.04 „trusty“) oder einer anderen Java/Tomcat-Umgebung
betrieben werden.

An weiterer Software werden Apache 2 mit \texttt{mod\_jk}, Tomcat 7
mit Java™ 7, sowie eine PostgreSQL-Datenbank benötigt. Auf einem
Debian 7 „wheezy“-System können Sie diese wie folgt lokal installieren:

\begin{lstlisting}[language=sh]
sudo apt-get install postgresql openjdk-7-jre-headless tomcat7 apache2 libapache2-mod-jk
\end{lstlisting}

Bitte achten Sie darauf, daß keine ältere Version des JDK verwendet wird:

\begin{lstlisting}[language=sh]
sudo apt-get purge openjdk-6-jre-headless gcj-4.6-jdk gcj-4.7-jdk
\end{lstlisting}

Zur Installation müssen Sie folgende Dateien („die VerA.web-core
Distribution“) von tarent beziehen:

\begin{itemize}
 \item{\texttt{veraweb-core-\produktversion{}-files.tgz}
  \emph{oder} \texttt{veraweb-core-\produktversion{}-files.zip}}
 \item{\texttt{veraweb-core-\produktversion{}.war}}
\end{itemize}

Bitte wenden Sie sich hierzu an unseren Produktvertrieb.
