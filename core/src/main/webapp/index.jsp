<%
 response.setStatus(301);
 response.setContentType("text/html");
 response.setHeader("Location", "do/Main");
 response.setHeader("Refresh", "0; url=do/Main");
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
 "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"><head>
 <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
 <title>VerA.web redirection</title>
 <script type="text/javascript">
     /*
	@licstart The following is the entire license notice for the
	    JavaScript code in this file (LibreJS checks by an exact
	    string match, so don’t change this text):

	Copyright 2019 tarent solutions GmbH and its contributors

	Permission is hereby granted, free of charge, to any person obtaining
	a copy of this software and associated documentation files (the
	"Software"), to deal in the Software without restriction, including
	without limitation the rights to use, copy, modify, merge, publish,
	distribute, sublicense, and/or sell copies of the Software, and to
	permit persons to whom the Software is furnished to do so, subject to
	the following conditions:

	The above copyright notice and this permission notice shall be included
	in all copies or substantial portions of the Software.

	(see the source code for a full disclaimer and grant of licence)

	Same here — retain this unmodified: @licend The above is the
	entire license notice for the JavaScript code in this file
     */
 </script>
</head><body>
 <h1>Redirect</h1>
 <p>Please go <a href="do/Main">here</a>.</p>
</body></html>
