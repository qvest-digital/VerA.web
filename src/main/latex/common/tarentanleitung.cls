% Dokumentklasse „Anleitung“ für tarent solutions GmbH
% Copyright © 2015
%	Thorsten Glaser <t.glaser@tarent.de>
%
% abgeleitet von der Dokumentenklasse pubmat des Teckids e.V.
% Copyright © 2014, 2015
%	Thorsten Glaser <thorsten.glaser@teckids.org>
% Copyright © 2013, 2014
%	Dominik George <nik@naturalnet.de>
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

\NeedsTeXFormat{LaTeX2e}
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{tarentanleitung}

\PassOptionsToPackage{paper=a4}{typearea}
\LoadClass{scrartcl}

\RequirePackage{tarentstyle}

%XXX always (before hyperref), plus conflicts with scrartcl
\usepackage{fancyhdr}

\newlength{\myheight}
\pagestyle{fancy}
\fancyhf{}
\renewcommand{\headrulewidth}{0pt}
\renewcommand{\footrulewidth}{0.6pt}
\fancyhead[L]{\headmark}
\fancyhead[R]{\tarentlogo[5.25cm]{}}
\fancyfoot[L]{\footnotesize\textcopyright tarent}
\fancyfoot[C]{{\scriptsize\thepage/\ztotpages}}
\fancyfoot[R]{\href{https://www.tarent.de/}{www.tarent.de}}

% Name Jahr
\newcommand{\tarentohnedeckblatt}[2]{
\loadgeometry{tarentstyle}
\fancyhf{}
\renewcommand{\headrulewidth}{0pt}
\renewcommand{\footrulewidth}{0.6pt}
\fancyhead[L]{#1}
\fancyhead[R]{\tarentlogo[5.25cm]{}}
\fancyfoot[L]{\footnotesize\textcopyright #2 tarent}
\fancyfoot[C]{{\scriptsize\thepage/\ztotpages}}
\fancyfoot[R]{\href{https://www.tarent.de/}{www.tarent.de}}
}

% Name Version Fassung vom Jahr Bild
\newcommand{\tarentanleitung}[6]{
 \newcommand{\produktversion}{#2}
% 1. Seite: keine Kopfzeile; Fußzeile 1.1 cm (Abstand 0.6 cm); mehr Rand
\newgeometry{includeheadfoot,inner=2.1cm,outer=1.4cm,top=2cm,bottom=4cm,twoside,headheight=0cm,headsep=0cm,foot=1.7cm}
\fancyhf{}
\renewcommand{\headrulewidth}{0pt}
\renewcommand{\footrulewidth}{0pt}
\fancyfoot[C]{\tarentlogo[7cm]{}}

\colorbox{tarentrot}{
 \parbox[b][3cm][c]{0.975\textwidth}{
  \centering
  \color{white}\textbf{\Huge #1}
 }
}

\begin{flushright}
 \textbf{\Large Version #2}\\\vspace{0.8em}
 \textbf{\Large Fassung #3 vom #4 #5}
\end{flushright}

\ifthenelse{\equal{x#6}{x}}{}{
 \vspace{4cm}
 \begin{center}
  \includegraphics[width=.8\textwidth]{#6}
 \end{center}
}

\newpage
% Folgeseiten
\restoregeometry
\tarentohnedeckblatt{#1}{#5}
\floatstyle{boxed}
\restylefloat{figure}
\renewcommand{\figurename}{Abbildung}
}
