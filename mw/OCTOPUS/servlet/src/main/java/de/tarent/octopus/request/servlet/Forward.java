package de.tarent.octopus.request.servlet;
import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * This servlet forward a request to the octopus dispatcher. It use the
 * context-name as the octopus modulenname and the first pathinfo part as
 * taskname.
 * </p>
 * <p>
 * <strong>Note:</strong> At the moment the target webapplication context will
 * NOT saved in the {@link #init(javax.servlet.ServletConfig)} method, because
 * there is not ensured that the octopus webapplication is already loaded at
 * that moment! (We can change this when our knowledge about all used or
 * certified(?) java enterprise edition servers is growed up. ;-) But this is
 * now not needful, this is not performance critically.)
 * </p>
 *
 * @author Michael Klink, tarent GmbH
 * @author Christoph Jerolimov, tarent GmbH
 */
@Log4j2
public class Forward extends HttpServlet {
    private static final long serialVersionUID = 3256441417202218291L;

    /**
     * Servlet-Parameter für den Pfad zum KontextRoot der Ziel Webanwendung
     */
    public final static String INIT_PARAM_TARGET_CONTEXT = "targetContext";
    /**
     * Servlet-Parameter für den Pfad zum Servlet innerhalb der Ziel Webanwendung
     */
    public final static String INIT_PARAM_TARGET_PATH = "targetPath";
    /**
     * Default-Servlet-Parameter für {@link #INIT_PARAM_TARGET_PATH}
     */
    public final static String DEFAULT_TARGET_PATH = "/octopus";

    /**
     * <p>
     * This service method redirekt all queries to the octopus.
     * </p>
     * <p>
     * Is redirekt this <code>/&lt;modulename&gt;/do/&lt;taskname&gt;</code>
     * query to <code>&lt;targetContext&gt;&lt;targetPath&gt;/&lt;modulename&gt;/&lt;taskname&gt;</code>.
     * </p>
     * <p>
     * If the servlet parameter 'targetContext' it not definend it will be use
     * the current context. And if the servlet parameter 'targetPath' it not
     * defined it will be use the default <code>/octopus</code>.
     * </p>
     *
     * @see HttpServlet#service(HttpServletRequest, HttpServletResponse)
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetContextString = getInitParameter(INIT_PARAM_TARGET_CONTEXT);
        String targetPathString = getInitParameter(INIT_PARAM_TARGET_PATH);

        ServletContext targetContext = (targetContextString == null)
          ? getServletContext()
          : getServletContext().getContext(targetContextString);

        if (targetContext == null) {
            throw new ServletException("can not access target context: '" + targetContextString + "'");
        }

        if (targetPathString == null) {
            targetPathString = DEFAULT_TARGET_PATH;
        }

        String target = targetPathString + request.getContextPath();
        if (request.getPathInfo() != null) {
            target += request.getPathInfo();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Forwarding URI " +
              "<" + request.getRequestURI() + "> to" +
              "<" + targetContext.getServletContextName() + target + ">");
        }

        RequestDispatcher dispatcher = targetContext.getRequestDispatcher(target);
        dispatcher.forward(request, response);
    }
}
