/* $Id: OctopusConnectionFactory.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.client;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import de.tarent.octopus.request.directCall.OctopusDirectCallConnection;
import de.tarent.octopus.request.directCall.OctopusStarter;
import de.tarent.octopus.client.remote.*;

/** 
 * Factory zur Lieferung einer Client-Schnittstelle zum Octopus.
 * Abhängig von einer Konfiguration wird eine OctopusConnection zu einem lokalen 
 * oder entferneten Octopus zurück geliefert.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusConnectionFactory {

    private static Logger logger = Logger.getLogger(OctopusConnectionFactory.class.getName());

    public static final String PROPERTY_FILE_NAME_END = "-octopus-connection.properties";

    public static final String OCTOPUS_PARAMS_KEY_PREFIX = "octopus.";

    public static final String CONNECTION_TYPE_KEY = "type";
    public static final String CONNECTION_TYPE_DIRECT_CALL = "directCall";
    public static final String CONNECTION_TYPE_REMOTE = "remote";

    public static final String MODULE_KEY = "module";

    HashMap connections = new HashMap();
    HashMap configurations = new HashMap();

    protected static OctopusConnectionFactory theInstance = null;
    protected OctopusConnectionFactory() {}
    public static OctopusConnectionFactory getInstance() {
        if (theInstance == null)
            theInstance = new OctopusConnectionFactory();
        return theInstance;        
    }


    public OctopusConnection getConnection(String connectionName)
        throws FactoryConfigurationException {

        if (connections.get(connectionName) != null)
            return (OctopusConnection)connections.get(connectionName);

        if(configurations.get(connectionName)==null){
        	setConfiguration(connectionName, getConfigValues(connectionName));
        }
        Map conf = (Map)configurations.get(connectionName);

        if (CONNECTION_TYPE_DIRECT_CALL.equals(conf.get(CONNECTION_TYPE_KEY))) {
            OctopusConnection con = getDirectCallConnection(conf);
            connections.put(connectionName, con);
            return con;
        } else if (CONNECTION_TYPE_REMOTE.equals(conf.get(CONNECTION_TYPE_KEY))) {
            OctopusConnection con = getRemoteConnection(conf);
            connections.put(connectionName, con);
            return con;
        }
        throw new FactoryConfigurationException("Ander Verbindungstypen als directCall werden von der Factory noch nicht unterstützt.", null);
    }

    public void setConfiguration(String connectionName, Map configValues) {
		configurations.put(connectionName, configValues);
		connections.put(connectionName, null);//Evtl unter diesem Namen bestehende Connections rausschmeissen
	}
    
	protected OctopusConnection getDirectCallConnection(Map conf) {
        OctopusDirectCallConnection con = new OctopusDirectCallConnection();
        con.setModuleName((String)conf.get(MODULE_KEY));
        

        Map octopus_conf = new HashMap();
        for (Iterator iter = conf.keySet().iterator(); iter.hasNext();) {
            String key = (String)iter.next();
        
            if (key.startsWith(OCTOPUS_PARAMS_KEY_PREFIX)) {
                octopus_conf.put(key.substring(OCTOPUS_PARAMS_KEY_PREFIX.length()),
                                 conf.get(key));
            }
        }
        OctopusStarter starter = new OctopusStarter(octopus_conf);
        con.setOctopusStarter(starter);

        return con;
    }

    protected OctopusConnection getRemoteConnection(Map conf) {
        OctopusRemoteConnection con = new OctopusRemoteConnection();
        con.setModuleName((String)conf.get(MODULE_KEY));
        
        con.setServiceURL((String)conf.get(OctopusRemoteConnection.PARAM_SERVICE_URL));
        con.setUsername((String)conf.get(OctopusRemoteConnection.PARAM_USERNAME));
        con.setPassword((String)conf.get(OctopusRemoteConnection.PARAM_PASSWORD));
        con.setAuthType((String)conf.get(OctopusRemoteConnection.AUTH_TYPE));
        con.setAutoLogin("true".equalsIgnoreCase((String)conf.get(OctopusRemoteConnection.AUTO_LOGIN)));
        con.setConnectionTracking("true".equalsIgnoreCase((String)conf.get(OctopusRemoteConnection.CONNECTION_TRACKING)));
        con.setUseSessionCookie("true".equalsIgnoreCase((String)conf.get(OctopusRemoteConnection.USE_SESSION_COOKIE)));
        con.setSessionCookieFile((String)conf.get(OctopusRemoteConnection.SESSION_COOKIE_FILE));

        if (conf.get(OctopusRemoteConnection.KEEP_SESSION_ALIVE) != null) {
            try {
                con.setKeepSessionAlive(new Integer((String)conf.get(OctopusRemoteConnection.KEEP_SESSION_ALIVE)));
            } catch (NumberFormatException  e) {            
                logger.log(Level.WARNING, "Fehlerhafter Wert für Octopus Connection Propertie:"+OctopusRemoteConnection.KEEP_SESSION_ALIVE, e);
            }
        }
        return con;
    }


    protected Map getConfigValues(String connectionName) 
        throws FactoryConfigurationException {
        String fName = "/"+connectionName+PROPERTY_FILE_NAME_END;
        InputStream is = getClass().getResourceAsStream(fName);
        if (is == null)
            throw new FactoryConfigurationException("Kann Resource <"+fName+"> nicht finden.", null);
        Properties p  = new Properties();
        try {
            p.load( is );
        } catch ( IOException e ) {
            throw new FactoryConfigurationException("Fehler beim Laden der Resource <"+fName+">.", e);            
        }
        return p;
    }
}
