<%
 response.setStatus(301);
 response.setContentType("text/html");
 response.setHeader("Location", "do/Main");
 response.setHeader("Refresh", "0; url=do/Main");
%><html><head><title>VerA.web redirection</title></head><body>
<h1>Redirect</h1>
<p>Please go <a href="do/Main">here</a>.</p>
</body></html>
