package de.tarent.octopus.request.servlet;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.Octopus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.MalformedURLException;

/**
 * This lifecycle listener will be notified if a servlet context has just been created and is available to service its first
 * request, or the servlet context is about to be shutdown.
 *
 * It will be used for realize that new octopus modules will be available. So we can implement
 * {@link Octopus#doAutostart(String, TcCommonConfig)} and {@link Octopus#doCleanup(String, TcCommonConfig)}  also if one
 * module will loaded after the octopus webapplication. (Only in separate installations.)
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class LifecycleListener implements ServletContextListener {
    /**
     * Logger instance
     */
    private Log logger = LogFactory.getLog(LifecycleListener.class);

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String module = getOctopusModule(servletContext);
        Object octopus = getOctopus();

        if (octopus == null) {
            logger.info(
              "Webapplication context '" + module + "' initialized, " +
                "but octopus is not available (yet).");
        } else {
            logger.info(
              "Webapplication context '" + module + "' initialized, " +
                "will be register it at internal octopus.");
            // TODO
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String module = getOctopusModule(servletContext);
        Object octopus = getOctopus();

        if (octopus == null) {
            logger.info(
              "Webapplication context '" + module + "' destroyed, " +
                "but octopus is not available (at the moment).");
        } else {
            logger.info(
              "Webapplication context '" + module + "' destroyed, " +
                "will be unregister it at internal octopus.");
            // TODO
        }
    }

    protected String getOctopusModule(ServletContext servletContext) {
        // This return the display name of the module!
        //return servletContext.getServletContextName()

        try {
            // Return in tomcat 5.5 a URL like this 'jndi:/localhost/modulename/'
            // where the path includes the full part right of the colon.
            String module = servletContext.getResource("/").getPath();
            if (module == null || module.length() <= 1) {
                return null;
            }
            while (module.endsWith("/")) {
                module = module.substring(0, module.length() - 1);
            }
            if (module.lastIndexOf("/") != -1) {
                module = module.substring(module.lastIndexOf("/") + 1);
            }
            return module;
        } catch (MalformedURLException e) {
            logger.warn(e.getLocalizedMessage(), e);
            return null;
        }
    }

    protected Object getOctopus() {
        Context context = getContext();
        if (context == null) {
            return null;
        }
        try {
            return context.lookup("octopus/instance");
        } catch (NamingException e) {
            logger.info("JNDI context available. Can not find octopus instannce.");
            return null;
        }
    }

    /**
     * Currently only support the apache tomcat (and maby more servlet container?)
     * as JNDI context provider. See
     * <code>http://tomcat.apache.org/tomcat-5.5-doc/jndi-resources-howto.html</code>
     * for more informations about this configuration.
     *
     * @return naming context
     */
    protected Context getContext() {
        try {
            return (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            logger.info("No JNDI context available. Can not find octopus instance.", e);
            return null;
        }
    }
}
