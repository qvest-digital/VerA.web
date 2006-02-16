/* $Id: OctopusDirectCallStarter.java,v 1.2 2006/02/16 16:53:41 kirchner Exp $
 * 
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
package de.tarent.octopus.request.directCall;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;

import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.soap.TcSOAPEngine;
import de.tarent.octopus.util.DataFormatException;
import de.tarent.octopus.util.Xml;

/** 
 * Erm�glicht das einfache Starten des Octopus
 * aus einer Anwendung heraus, oder als neuen Prozess.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallStarter implements OctopusStarter {
    private Octopus octopus = null;
    private TcEnv env = null;

    private static Logger logger = Logger.getLogger(OctopusStarter.class.getName());


    private static Logger baseLogger = null;

    private File logFile = null;
    private Handler logHandler = null;

    private TcSOAPEngine soapEngine = null;

    // Derzeit hat ein OctopusStarter genau eine Session
    TcSession tcSession = new TcDirectCallSession();

    /**
     * Startet den Octopus in der Komandozeile
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0 && ("-h".equals(args[0]) || "--help".equals(args[0])))
                dieError();

            Map cfg = new HashMap();
            int i = 0;
            while(args.length > i && args[i].startsWith("-P")) {
                int pos = args[i].indexOf("=");
                if (pos == -1)
                    dieError();
                
                cfg.put(args[i].substring(2,pos), args[i].substring(pos+1));
                i++;
            }
            OctopusDirectCallStarter starter = new OctopusDirectCallStarter(cfg);

            Map params = new HashMap();     
            for (; i+1 < args.length; i+=2) {
                params.put(args[i],args[i+1]);
            }
            starter.request(params);
        } catch (TcDirectCallException re) {
            Throwable rc = re.getRootCause();
            System.out.println("Fehler w�hrend der Octopus-Anfragebearbeitung: "+rc);
            rc.printStackTrace();
        } catch (Exception e) {
            System.out.println("Fehler w�hrend der Octopus-Anfragebearbeitung: "+e);
            e.printStackTrace();
        }
    } 

    protected static void dieError() {
        System.out.println("Usage: octopusStarter [-Ppropname=value ..] [key value ..]");
        System.exit(1);
    }

    
    public OctopusDirectCallStarter() {
    }
    
    /**
     * Inititalisiert die Komponenten, die f�r alle Aufrufe gleich sind.
     *
     * @param configParams Parameter, die die Konfigurationen �berschreiben, darf null sein
     */
    public OctopusDirectCallStarter(Map configParams) {
        boolean loggerWasNull = false;
        try {
            // Statische Loggerinstanz erstellen
            if (baseLogger == null) {
                loggerWasNull = true;
                baseLogger = Logger.getLogger("de.tarent");
                baseLogger.setLevel(Level.ALL);

                baseLogger.getHandlers();
                
                //Keinen Consolen-Logger wenn der Octopus direkt gestartet wird.
                baseLogger.setUseParentHandlers(false);
            }

            env = createEnvObject(configParams);

            //TODO: Warum wird das als System Property gesetzt?
            System.setProperty(TcEnv.KEY_PATHS_ROOT, env.getValueAsString(TcEnv.KEY_PATHS_ROOT));

            // Konfigurieren der Logging Api, wenn der
            // statische looger nicht schon existierte
            if (loggerWasNull) {   
                File logDir = new File(System.getProperty("user.home")+"/.tarent/octopuslog/");
                if (!logDir.exists())
                    logDir.mkdirs();
                logFile = new File(logDir, "current.log");
                ////Verzeichnis anlegen, wenn es nicht bereits existiert
                //(new File(env.getValueAsString(TcEnv.KEY_PATHS_ROOT) + "log")).mkdirs();
                logHandler = new FileHandler(logFile.getAbsolutePath());
                logHandler.setFormatter(new SimpleFormatter());
                baseLogger.addHandler(logHandler);
                baseLogger.info(Resources.getInstance().get("REQUESTPROXY_LOG_START_LOGGING"));
                String logLevel = env.getValueAsString(TcEnv.KEY_LOGGING_LEVEL);
                if (logLevel != null && logLevel.length() > 0)
                    try {
                        Level level = Level.parse(logLevel);
                        baseLogger.config(Resources.getInstance().get("REQUESTPROXY_LOG_NEW_LOG_LEVEL", level));
                        baseLogger.setLevel(level);
                    } catch (IllegalArgumentException iae) {
                        baseLogger.log(
                            Level.WARNING,
                            Resources.getInstance().get("REQUESTPROXY_LOG_INVALID_LOG_LEVEL", logLevel),
                            iae);
                    }
            }
            
            octopus = new Octopus();
            octopus.init(env, new OctopusConfig());

            soapEngine = new TcSOAPEngine(env);
        } catch (Exception e) {
            logger.log(Level.SEVERE, Resources.getInstance().get("REQUESTPROXY_LOG_INIT_EXCEPTION"), e);
            throw new RuntimeException("Fehler beim Initialisieren des lokalen Octopus", e);
        }

        logger.exiting("TcRequextProxy", "init");
    }

    public void destroy() {
        if (octopus != null)
            try {
                octopus.deInit();
            } catch (Exception e) {
                logger.log(Level.WARNING, Resources.getInstance().get("REQUESTPROXY_LOG_CLEANUP_EXCEPTION"), e);
            }

        // TODO: Sollte der logHandler wirklich geschlossen werden?
        // Der baseLogger list ja Statisch und somit in 
        // anderen Instanzen evt. noch aktiv
        baseLogger.removeHandler(logHandler);
        logHandler.close();
    }


    /**
     * Startet die Abarbeitung einer Anfrage
     */
    public OctopusDirectCallResult request(Map requestParams)
        throws TcDirectCallException {

        logger.fine(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_PROCESSING_START"));

        try {
            TcDirectCallResponse response = new TcDirectCallResponse();
            logger.finest(Resources.getInstance().get("REQUESTPROXY_LOG_RESPONSE_OBJECT_CREATED"));
            response.setSoapEngine(soapEngine);
                        
            logger.finest(Resources.getInstance().get("REQUESTPROXY_LOG_SESSION_OBJECT_CREATED"));
            
            TcRequest request = new TcRequest();
            request.setRequestParameters(requestParams);
            request.setParam(TcRequest.PARAM_SESSION_ID, tcSession.getId());
            logger.finest(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_OBJECT_CREATED"));
                        
            octopus.dispatch(request, response, tcSession);
            
            response.flush();
            return new OctopusDirectCallResult(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION"), e);
            throw new TcDirectCallException(Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION"), e);
        }
        
    }


    /**
     * Erstellt eine default-Konfiguration im TcEnv
     * Anschlie�end werden diese Werte durch die im 
     * Knoten /de/tarent/octopus/overrides der Java-System-Prefferences
     * �berschrieben.
     *
     * @param overrideSettings Parameter, die die Konfigurationen �berschreiben, 
     *        darf null sein
     */
    protected TcEnv createEnvObject(Map overrideSettings) {
        // Env Objekt mit Umgebungsvariablen und Einstellungsparametern
        TcEnv env = new TcEnv();

        //env.setValue(TcEnv.KEY_PATHS_ROOT, "." + System.getProperty("file.separator"));
        env.setValue(TcEnv.KEY_PATHS_ROOT, System.getProperty("user.dir") +"/OCTOPUS/");
        env.setValue(TcEnv.KEY_LOGGING_LEVEL, "CONFIG" );
        env.setValue(TcEnv.KEY_PATHS_CONFIG_ROOT, "config/" );
        env.setValue(TcEnv.KEY_PATHS_CONFIG_FILE, "config.xml" );
        //env.setValue(TcEnv.loggerConfigFile, "logger.conf");
        //env.setValue(TcEnv.paths.pageDescriptionRoot, );
        env.setValue(TcEnv.KEY_PATHS_TEMPLATE_ROOT, "templates/");
        env.overrideValues("base", "/de/tarent/octopus/overrides");

        if (null != overrideSettings) 
            for (Iterator iter = overrideSettings.keySet().iterator(); iter.hasNext();) {
                String key = ""+iter.next();
                env.setValue(key, ""+overrideSettings.get(key));
            }       
        
        return env;
    }

    /**
     * Diese Klasse liefert dem Octopus notwendige Daten. 
     */
    protected class OctopusConfig implements Octopus.Configuration {

        public List getPreloadModules() {
            return null;
        }

        /**
         * Diese Methode liefert zu einem Modulnamen die Konfiguration.
         * 
         * TODO: Freie konfigurierbarkeit des Modulpfades in dem Environment
         * 
         * @param module der Name des Moduls.
         * @return Modulkonfiguration zu dem Modul. <code>null</code>
         *  steht hier f�r ein nicht gefundenes Modul.
         * @see de.tarent.octopus.request.Octopus.Configuration#getModuleConfig(String, Preferences)
         */
        public TcModuleConfig getModuleConfig(String module, Preferences modulePreferences) {
            String realPath = null;

            realPath = env.getValue(TcEnv.KEY_PATHS_ROOT) + "modules/"+ module +"/";
            if (realPath == null || ! (new File(realPath)).exists()) {
                realPath = modulePreferences.get(PREF_NAME_REAL_PATH, null);
                logger.log(Level.INFO, Resources.getInstance().get("OCTOPUS_STARTER_LOG_MODULE_PATH_PREFERENCES", module));
            }
            logger.log(Level.INFO, Resources.getInstance().get("OCTOPUS_STARTER_LOG_MODULE_PATH", module, realPath));

            if (realPath == null || (! (new File(realPath)).exists())) {
                logger.log(Level.SEVERE, Resources.getInstance().get("OCTOPUS_STARTER_LOG_MODULE_PATH_INVALID", module, realPath));
                return null;
            }
            File modulePath = new File(realPath);
            
            File configFile;                        
            if (null != env.getValue(TcEnv.KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + module)) {
                String configFileLocation = (String)env.getValue(TcEnv.KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + module);
                if (File.separatorChar != '/')
                    configFileLocation = configFileLocation.replace('/', File.separatorChar);
                configFile = new File(configFileLocation);
                if (!configFile.isAbsolute())
                    configFile = new File(System.getProperty("user.dir"), configFileLocation);
            } else {
                configFile = new File(modulePath, "config.xml");
            }
            if (! configFile.exists()) {
                logger.log(Level.SEVERE, Resources.getInstance().get("OCTOPUS_STARTER_LOG_MODULE_CONFIG_PATH_INVALID", module, configFile));
                return null;
            }
			modulePreferences.put(PREF_NAME_REAL_PATH, realPath);
            try {
                logger.config(Resources.getInstance().get("REQUESTPROXY_LOG_PARSING_MODULE_CONFIG", configFile, module));
                Document document = Xml.getParsedDocument(Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath()));
                return new TcModuleConfig(module, modulePath, document, modulePreferences);
            } catch (SAXParseException se) {
                logger.log(
                    Level.SEVERE,
                    Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_SAX_EXCEPTION", new Integer(se.getLineNumber()), new Integer(se.getColumnNumber())),
                    se);
            } catch (DataFormatException ex) {
                logger.log(Level.SEVERE, Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_FORMAT_EXCEPTION"), ex);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_EXCEPTION"), ex);
            }
            
            return null;
        }
        
        //
        // Konstanten
        //
        public final static String PREF_NAME_REAL_PATH = "realPath";
    }
}
