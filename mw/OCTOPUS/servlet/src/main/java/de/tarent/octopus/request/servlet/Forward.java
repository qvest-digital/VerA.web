package de.tarent.octopus.request.servlet;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class Forward extends HttpServlet {
	/** serialVersionUID */
	private static final long serialVersionUID = 3256441417202218291L;

	/** Logger-Instanz */
	public final static Log logger = LogFactory.getLog(Forward.class);

    /** Servlet-Parameter für den Pfad zum KontextRoot der Ziel Webanwendung */
	public final static String INIT_PARAM_TARGET_CONTEXT = "targetContext";
    /** Servlet-Parameter für den Pfad zum Servlet innerhalb der Ziel Webanwendung */
	public final static String INIT_PARAM_TARGET_PATH = "targetPath";
	/** Default-Servlet-Parameter für {@link #INIT_PARAM_TARGET_PATH} */
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

	    if (targetContext == null)
	        throw new ServletException("can not access target context: '" + targetContextString + "'");

	    if (targetPathString == null)
	        targetPathString = DEFAULT_TARGET_PATH;

	    String target = targetPathString + request.getContextPath();
		if (request.getPathInfo() != null)
			target += request.getPathInfo();

		if (logger.isDebugEnabled())
			logger.debug("Forwarding URI " +
					"<" + request.getRequestURI() + "> to" +
					"<" + targetContext.getServletContextName() + target + ">");

		RequestDispatcher dispatcher = targetContext.getRequestDispatcher(target);
		dispatcher.forward(request, response);
    }
}
