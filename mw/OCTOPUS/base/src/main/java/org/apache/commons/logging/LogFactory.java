package org.apache.commons.logging;

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

import org.apache.commons.logging.impl.LogBridge;
import org.apache.commons.logging.impl.LogFactoryImpl;

/**
 * Subset of commons-logging 1.2 LogFactory interface
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
public abstract class LogFactory {
    /**
     * Empty default constructor, so we can instantiate the subclass.
     */
    protected LogFactory() {
    }

    private static LogFactory implementation = null;

    /**
     * Returns the singleton actual factory instance.
     *
     * @return {@link LogFactory}
     */
    public static LogFactory getFactory() throws LogConfigurationException {
        if (implementation == null) {
            implementation = new LogFactoryImpl();
        }
        return implementation;
    }

    /**
     * Throws away all internal references to any {@link LogFactory} instances
     * in the specified classloader, after calling {@link #release()} on them.
     *
     * In this implementation, identical to {@link #releaseAll()}.
     */
    public static void release(@SuppressWarnings("unused") final ClassLoader classLoader) {
        releaseAll();
    }

    /**
     * Throws away all internal references to any {@link LogFactory} instances
     * after calling {@link #release()} (see there) on them.
     */
    public static void releaseAll() {
        implementation = null;
    }

    /**
     * Returns a logger for the requested class.
     *
     * @param clazz {@link Class} to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public static Log getLog(final Class clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
    }

    /**
     * Returns a logger for the requested class name.
     *
     * @param clazzName {@link String} name of class to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public static Log getLog(final String clazzName) throws LogConfigurationException {
        return getFactory().getInstance(clazzName);
    }

    /**
     * Returns a logger for the requested class.
     *
     * @param clazz {@link Class} to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public abstract Log getInstance(final Class clazz) throws LogConfigurationException;

    /**
     * Returns a logger for the requested class name.
     *
     * @param clazzName {@link String} name of class to log for
     * @return {@link Log} instance ({@link LogBridge} to Log4j2)
     */
    public abstract Log getInstance(final String clazzName) throws LogConfigurationException;

    /**
     * Throws away all internal references to any {@link Log} instances.
     *
     * Some application server implementations handle reload by throwing
     * the classloader away, so it’s prudent to keep no references to
     * objects in those alive.
     */
    public abstract void release();

    /**
     * In this implementation, does nothing.
     *
     * @return null
     */
    public abstract Object getAttribute(final String name);

    /**
     * In this implementation, does nothing.
     *
     * @return zero-length array
     */
    public abstract String[] getAttributeNames();

    /**
     * In this implementation, does nothing.
     */
    public abstract void removeAttribute(final String name);

    /**
     * In this implementation, does nothing.
     */
    public abstract void setAttribute(final String name, final Object value);
}
