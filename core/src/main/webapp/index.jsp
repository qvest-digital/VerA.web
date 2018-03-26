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
</head><body>
 <h1>Redirect</h1>
 <p>Please go <a href="do/Main">here</a>.</p>
</body></html>
