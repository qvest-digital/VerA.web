% Dokumentklasse „Anleitung“ für tarent solutions GmbH
% Copyright © 2015
%	Thorsten Glaser <t.glaser@tarent.de>
%
% abgeleitet von der Dokumentenklasse pubmat des Teckids e.V.
% Copyright © 2014, 2015
%	Thorsten “mirabilos” Glaser <thorsten.glaser@teckids.org>
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

\usepackage{fancyhdr}
\usepackage{lastpage}
\geometry{footskip=0.6cm}
\usepackage{calc}
\newlength{\myheight}
\pagestyle{fancy}
\fancyhf{}
\renewcommand{\headrulewidth}{0pt}
\renewcommand{\footrulewidth}{0.6pt}
\fancyhead[L]{\headmark}
\fancyhead[R]{\tarentlogo[6cm]{}}
\fancyfoot[L]{\footnotesize\copyright tarent}
\fancyfoot[C]{{\scriptsize\thepage{}/\pageref*{LastPage}}}
\fancyfoot[R]{\href{https://www.tarent.de/}{www.tarent.de}}
%XXX fix page layout, cf. http://tex.stackexchange.com/a/37899/43807
\setlength{\headheight}{30pt}
\setlength{\headsep}{30pt}

% Name Version Fassung vom Jahr
\newcommand{\tarentanleitung}[5]{
\fancyhead[L]{#1}
\fancyfoot[L]{\footnotesize\copyright #5 tarent}
% Deckblatt mit #1, Version #2, Fassung #3 vom #4 #5
%XXX different header/footer here
\colorbox{tarentrot}{\color{white}\textbf{\Huge #1}}
\newline\Large Version #2
\newline\Large Fassung #3 vom #4 #5
\newpage
}
