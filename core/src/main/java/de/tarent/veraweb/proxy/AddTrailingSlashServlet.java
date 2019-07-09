package de.tarent.veraweb.proxy;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to add a trailing slash to a request missing one.
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
public class AddTrailingSlashServlet extends HttpServlet {
    private static final long serialVersionUID = 4780970710684579625L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String tgt = req.getRequestURI() + "/";
        final String tgtHTML = StringUtils.replaceEach(tgt,
          /* only those four! cf. https://stackoverflow.com/a/2083770/2171120 */
          new String[] { "&", "\"", "<", ">" },
          new String[] { "&amp;", "&quot;", "&lt;", "&gt;" });

        resp.setStatus(301);
        resp.setContentType("text/html");
        resp.setHeader("Location", tgt);
        resp.setHeader("Refresh", "0; url=" + tgt);

        final PrintWriter out = resp.getWriter();
        out.println("<html><head><title>VerA.web redirection</title></head><body>");
        out.println("<h1>Redirect</h1>");
        out.println("<p>Please go <a href=\"" + tgtHTML + "\">here</a>.</p>");
        out.println("</body></html>");
        out.close();
    }
}
