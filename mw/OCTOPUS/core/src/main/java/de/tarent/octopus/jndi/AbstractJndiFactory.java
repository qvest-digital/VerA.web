package de.tarent.octopus.jndi;

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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

/**
 * Diese Klasse stellt eine JNDI-{@link ObjectFactory} zum Zugriff auf den aktuellen
 * Octopus-Kontext dar.<br>
 * Sie ist angelehnt an das Beispiel für das erzeugen spezialisierter Resource Factories
 * <a href="http://jakarta.apache.org/tomcat/tomcat-5.0-doc/jndi-resources-howto.html#Adding Custom Resource Factories">hier</a>
 * in der Tomcat-Dokumentation.<br>
 * Zur Nutzung muss diese Factory der JNDI-Machinerie bekannt gemacht werden. Analog
 * obigem Beispiel ist dies im Projekt octopus/webapp getan worden, entsprechende
 * Einträge stehen dort in den Dateien <code>octopus.xml</code> und <code>web.xml</code>
 * im Verzeichnis <code>webapp/WEB-INF</code>.
 */
public abstract class AbstractJndiFactory implements ObjectFactory {
    protected Log logger = LogFactory.getLog(getClass());

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
            logger.info("No JNDI context available. Can not bind context '" + getLookupPath() + "'.", e);
            return null;
        }
    }

    public boolean bind() {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        try {
            context.addToEnvironment(Context.OBJECT_FACTORIES, getClass().getName());
        } catch (NamingException e) {
            logger.warn(
              "Can not add current class '" + getClass().getName() + "' " +
                "to the object factory list.");
        }

        try {
            Object object = context.lookup(getLookupPath());
            if (object != null && object.getClass().getName().equals(getClass().getName())) {
                logger.info(
                  "JNDI context available. " +
                    "Context '" + getLookupPath() + "' already binded.");
                return true;
            } else if (object != null) {
                logger.info(
                  "JNDI context available. " +
                    "Wrong class '" + object.getClass().getName() + "' " +
                    "for context '" + getLookupPath() + "' binded, will rebind it now.");
            } else {
                logger.info(
                  "JNDI context available. " +
                    "Context '" + getLookupPath() + "' not binded yet, will do it now.");
            }
        } catch (NamingException e) {
            logger.info(
              "JNDI context available. " +
                "Exception '" + e.getLocalizedMessage() + "' while lookup " +
                "context '" + getLookupPath() + "' catched, will rebind it now.");
        }

        try {
            context.rebind(getLookupPath(), this);
            logger.info("JNDI context '" + getLookupPath() + "' successful binded.");
            return true;
        } catch (NamingException e) {
            logger.info("JNDI context available, but can not bind context '" + getLookupPath() + "'.");
            return false;
        }
    }

    public boolean unbind() {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        try {
            context.unbind(getLookupPath());
            logger.info("JNDI context '" + getLookupPath() + "' successful unbinded.");
            return true;
        } catch (NamingException e) {
            logger.error("JNDI context available, but can not unbind context '" + getLookupPath() + "'.", e);
            return false;
        }
    }

    abstract protected String getLookupPath();

    public Object getObjectInstance(Object object, Name name, Context context, Hashtable environment) throws Exception {
        return this;
    }
}
