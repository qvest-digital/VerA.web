package de.tarent.octopus.client;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;

import de.tarent.octopus.client.remote.OctopusRemoteConnection;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.directcall.OctopusDirectCallConnection;
import de.tarent.octopus.request.directcall.OctopusDirectCallStarter;
import de.tarent.octopus.request.internal.OctopusInternalStarter;
import de.tarent.octopus.request.internal.OctopusStarter;

/**
 * Factory zur Lieferung einer Client-Schnittstelle zum Octopus.
 * Abhängig von einer Konfiguration wird eine OctopusConnection zu einem lokalen
 * oder entferneten Octopus zurück geliefert.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusConnectionFactory {

    private static Log logger = LogFactory.getLog(OctopusConnectionFactory.class);

    public static final String PROPERTY_FILE_NAME_END = "-octopus-connection.properties";

    public static final String OCTOPUS_PARAMS_KEY_PREFIX = "octopus.";

    public static final String CONNECTION_TYPE_KEY = "type";
    public static final String CONNECTION_TYPE_DIRECT_CALL = "directCall";
    public static final String CONNECTION_TYPE_REMOTE = "remote";
    public static final String CONNECTION_TYPE_INTERNAL = "internal";

    public static final String MODULE_KEY = "module";

    HashMap connections = new HashMap();
    HashMap configurations = new HashMap();

    /**
     * The Octopus instance for an internal call
     */
    Octopus internalOctopusInstance;

    protected static OctopusConnectionFactory theInstance = null;

    protected OctopusConnectionFactory() {
    }

    public static OctopusConnectionFactory getInstance() {
        if (theInstance == null) {
            theInstance = new OctopusConnectionFactory();
        }
        return theInstance;
    }

    /**
     * Returns a connection for <code>connectionName</code>.
     * Subsequent calls will always return a new Connection instance.
     */
    public OctopusConnection getNewConnection(String connectionName)
      throws FactoryConfigurationException {

        if (configurations.get(connectionName) == null) {
            setConfiguration(connectionName, getConfigValues(connectionName));
        }
        Map conf = (Map) configurations.get(connectionName);

        if (CONNECTION_TYPE_DIRECT_CALL.equals(conf.get(CONNECTION_TYPE_KEY))) {
            return getDirectCallConnection(conf);
        } else if (CONNECTION_TYPE_REMOTE.equals(conf.get(CONNECTION_TYPE_KEY))) {
            return getRemoteConnection(conf);
        } else if (CONNECTION_TYPE_INTERNAL.equals(conf.get(CONNECTION_TYPE_KEY))) {
            return getInternalConnection(conf);
        }
        throw new FactoryConfigurationException(
          "Andere Verbindungstypen als directCall, internal und remote werden von der Factory noch nicht unterstützt.",
          null);
    }

    /**
     * Returns the connection for <code>connectionName</code>.
     * Subsequent calls to this method with the same parameter will return the same Connection instance.
     */
    public OctopusConnection getConnection(String connectionName)
      throws FactoryConfigurationException {

        if (connections.get(connectionName) != null) {
            return (OctopusConnection) connections.get(connectionName);
        }

        OctopusConnection con = getNewConnection(connectionName);
        connections.put(connectionName, con);
        return con;
    }

    public void setConfiguration(String connectionName, Map configValues) {
        configurations.put(connectionName, configValues);
        connections.put(connectionName, null);//Evtl unter diesem Namen bestehende Connections rausschmeissen
    }

    protected OctopusConnection getDirectCallConnection(Map conf) {
        OctopusDirectCallConnection con = new OctopusDirectCallConnection();
        con.setModuleName((String) conf.get(MODULE_KEY));

        Map octopus_conf = new HashMap();
        for (Iterator iter = conf.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();

            if (key.startsWith(OCTOPUS_PARAMS_KEY_PREFIX)) {
                octopus_conf.put(key.substring(OCTOPUS_PARAMS_KEY_PREFIX.length()),
                  conf.get(key));
            }
        }
        OctopusStarter starter = new OctopusDirectCallStarter(octopus_conf);
        con.setOctopusStarter(starter);

        return con;
    }

    protected OctopusConnection getInternalConnection(Map conf) {
        OctopusDirectCallConnection con = new OctopusDirectCallConnection();
        con.setModuleName((String) conf.get(MODULE_KEY));

        // No params or configuration used here
        //         Map octopus_conf = new HashMap();
        //         for (Iterator iter = conf.keySet().iterator(); iter.hasNext();) {
        //             String key = (String)iter.next();

        //             if (key.startsWith(OCTOPUS_PARAMS_KEY_PREFIX)) {
        //                 octopus_conf.put(key.substring(OCTOPUS_PARAMS_KEY_PREFIX.length()),
        //                                  conf.get(key));
        //             }
        //         }
        OctopusStarter starter = new OctopusInternalStarter(getInternalOctopusInstance());
        con.setOctopusStarter(starter);

        return con;
    }

    protected OctopusConnection getRemoteConnection(Map conf) {
        OctopusRemoteConnection con = new OctopusRemoteConnection();
        con.setModuleName((String) conf.get(MODULE_KEY));

        con.setServiceURL((String) conf.get(OctopusRemoteConnection.PARAM_SERVICE_URL));
        con.setUsername((String) conf.get(OctopusRemoteConnection.PARAM_USERNAME));
        con.setPassword((String) conf.get(OctopusRemoteConnection.PARAM_PASSWORD));
        con.setAuthType((String) conf.get(OctopusRemoteConnection.AUTH_TYPE));
        con.setAutoLogin("true".equalsIgnoreCase((String) conf.get(OctopusRemoteConnection.AUTO_LOGIN)));
        con.setConnectionTracking("true".equalsIgnoreCase((String) conf.get(OctopusRemoteConnection.CONNECTION_TRACKING)));
        con.setUseSessionCookie("true".equalsIgnoreCase((String) conf.get(OctopusRemoteConnection.USE_SESSION_COOKIE)));

        if (conf.get(OctopusRemoteConnection.KEEP_SESSION_ALIVE) != null) {
            try {
                con.setKeepSessionAlive(new Integer((String) conf.get(OctopusRemoteConnection.KEEP_SESSION_ALIVE)));
            } catch (NumberFormatException e) {
                logger.warn("Fehlerhafter Wert für Octopus Connection Propertie:" + OctopusRemoteConnection.KEEP_SESSION_ALIVE,
                  e);
            }
        }
        return con;
    }

    protected Map getConfigValues(String connectionName)
      throws FactoryConfigurationException {
        String fName = "/" + connectionName + PROPERTY_FILE_NAME_END;
        InputStream is = getClass().getResourceAsStream(fName);
        if (is == null) {
            throw new FactoryConfigurationException("Kann Resource <" + fName + "> nicht finden.", null);
        }
        Properties p = new Properties();
        try {
            p.load(is);
        } catch (IOException e) {
            throw new FactoryConfigurationException("Fehler beim Laden der Resource <" + fName + ">.", e);
        }
        return p;
    }

    /**
     * Returns the Octopus Instance which is used for internal calls to modules
     */
    public final Octopus getInternalOctopusInstance() {
        return internalOctopusInstance;
    }

    /**
     * Sets the Octopus Instance which is used for internal calls to modules
     */
    public final void setInternalOctopusInstance(final Octopus newInternalOctopusInstance) {
        this.internalOctopusInstance = newInternalOctopusInstance;
    }
}
