package de.tarent.commons.logging;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * A simple factory which should be used to get a log instance in tarent projects.
 * This implementations use an on small configuration in a ressource properties file /tarent-logging.properties
 * to decide which underlaying logging system should be used.
 *
 * <p>This way makes it possible to use apache.commmons.logging in a shared environment (i.e. J2EE) an
 * without having to use the same underlying logging system.<p/>
 *
 * <p>For configuration, there has to be a file /tarent-logging.properties in the classpath with the single property
 * <code>logging.api</code>.
 * Possible values for this Property are: jdk (Java util logging backend), log4j (Log4j backend), commons (apache commons
 * default behavior), simple (apache commons simple logger)<p/>
 */
public class LogFactory {

    public static final String TARENT_LOGGING_PROPERTIES = "/tarent-logging.properties";

    public static final String LOGGING_API = "logging.api";

    public static final String LOGGING_API_JDK14 = "jdk";
    public static final String LOGGING_API_LOG4J = "log4j";
    public static final String LOGGING_API_COMMONS = "commons";
    public static final String LOGGING_API_SIMPLE = "simple";

    protected static final int JDK14_LOGGER = 1;
    protected static final int LOG4J_LOGGER = 2;
    protected static final int COMMONS_LOGGER = 3;
    protected static final int SIMPLE_LOGGER = 4;

    static int logger = JDK14_LOGGER;

    static {
        loadProperties();
    }

    public static Log getLog(Class clazz) {
        if (useJdkLogger()) {
            return new Jdk14Logger(clazz.getName());
        } else if (useLog4jLogger()) {
            return new Log4JLogger(clazz.getName());
        } else if (useCommonsLogger()) {
            return org.apache.commons.logging.LogFactory.getLog(clazz);
        } else if (useSimpleLog()) {
            return new SimpleLog(clazz.getName());
        } else {
            return org.apache.commons.logging.LogFactory.getLog(clazz);
        }
    }

    public static void loadProperties() {
        InputStream in = LogFactory.class.getResourceAsStream(TARENT_LOGGING_PROPERTIES);
        if (in != null) {
            Properties properties = new Properties();
            try {
                properties.load(in);
                Object value = properties.get(LOGGING_API);
                if (LOGGING_API_JDK14.equals(value)) {
                    logger = JDK14_LOGGER;
                } else if (LOGGING_API_LOG4J.equals(value)) {
                    logger = LOG4J_LOGGER;
                } else if (LOGGING_API_COMMONS.equals(value)) {
                    logger = COMMONS_LOGGER;
                } else if (LOGGING_API_SIMPLE.equals(value)) {
                    logger = SIMPLE_LOGGER;
                }
            } catch (IOException e) {
                log("FATAL: Error while reading logging configuration from ressource: " + TARENT_LOGGING_PROPERTIES, e);
            }
        }
    }

    static boolean useJdkLogger() {
        return (JDK14_LOGGER == logger);
    }

    static boolean useLog4jLogger() {
        return (LOG4J_LOGGER == logger);
    }

    static boolean useCommonsLogger() {
        return (COMMONS_LOGGER == logger);
    }

    static boolean useSimpleLog() {
        return (SIMPLE_LOGGER == logger);
    }

    public static void log(String message, Exception e) {
        System.err.println(message);
        if (e != null) {
            e.printStackTrace(System.err);
        }
    }
}
