/* $Id: TcCommonConfig.java,v 1.8 2007/03/07 17:28:22 christoph Exp $
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

package de.tarent.octopus.config;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import de.tarent.octopus.data.TarDBConnection;
import de.tarent.octopus.data.TcDataAccessException;
import de.tarent.octopus.data.TcGenericDataAccessWrapper;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcTaskList;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.security.LoginManagerAcceptAll;
import de.tarent.octopus.security.LoginManagerFilter;
import de.tarent.octopus.security.LoginManagerLDAP;
import de.tarent.octopus.security.LoginManagerXML;
import de.tarent.octopus.security.TcSecurityException;
import de.tarent.octopus.server.LoginManager;
import de.tarent.octopus.server.PersonalConfig;
import de.tarent.octopus.util.DataFormatException;
import de.tarent.octopus.util.Xml;

/**
 * Kapselt Einstellungen und Informationen, die
 * f�r alle Benutzer gleich sind.
 * <br><br>
 * Diese Informationen stammen aus der Configdatei (./WEB-INF/config/config.xml) und dem TcEnv Container,
 * der mit Servletinformationen und den Parametern aus dem Deployment-Descriptor gef�llt ist.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcCommonConfig {
    /** Authorisierungstyp: beliebiges Akzeptieren */
    public static final String AUTH_TYPE_ACCEPT = "ACCEPT";
    /** Authorisierungstyp: von vorgeschaltetem Filter akzeptieren */
	public static final String AUTH_TYPE_FILTER = "FILTER";
    /** Authorisierungstyp: gegen XML-Datei pr�fen */
	public static final String AUTH_TYPE_XML = "XML";
    /** Authorisierungstyp: gegen LDAP pr�fen */
	public static final String AUTH_TYPE_LDAP = "LDAP";
    
	/** Der Logger */
    private static Log logger = LogFactory.getLog(TcCommonConfig.class);

    /** Die Daten aus der Haupt Konfigurations Datei
     * und Environment Werte aus der ServletEnv
     */
    private TcEnv env;

    /**
     * Dieses Objekt erlaubt ein dynamisches Nachladen neuer Module.
     */
    private TcModuleLookup moduleLookup;

    /**
     * Login Manager
     */
    protected LoginManager loginManager = null;

    /**
     * 
     */
    protected Octopus octopus;

    /** 
     * Die Konfigurationen der einzelnen Module.
     * Mit ihren Namen als String Keys und TcModuleConfig Objekten als Values.
     */
    private Map moduleConfigs;
    
    /**
     * Map mit Eintr�gen der LoginManager Konfiguration
     */
    private Map configLoginManager = new HashMap();

    /**
     * Die ModuleConfig, die benutzt wird, wenn nichts anderes angegeben ist.
     */
    protected TcModuleConfig defaultModuleConfig;

    /**
     * Puffer Mit Zugriffsklassen f�r die Verschiedenen Datenquellen.
     * Die Keys sind String in der Form: 'ModulName.dataSourceName', wobei der ModulName der 
     * Name des Modules ist, indem die Datenquelle Deklariert wurde
     * Die Values sind Objekte vom Typ TcGenericDataAccessWrapper oder Spezialisierungen davon.
     */
    protected Map bufferedDataAccessWrappers;

    /**
     * Initialisierung:
     * Parst die Haupt Konfigurations Datei.
     *
     * @param env Ein Datenkontainer, mit mindestens den Eintr�gen:
     *  config.configRoot, config.configData
     */
    public TcCommonConfig(TcEnv env, Octopus octopus) throws TcConfigException {
        this.env = env;
        this.octopus = octopus;
        parseConfigFile();
        bufferedDataAccessWrappers = new HashMap();
        logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_INITIALIZED"));
    }

    /**
     * Parsen der Haupt Konfigurations Datei.
     *
     * Wenn Fehler auftreten, werden diese nur durch den Logger protokolliert
     * und nicht zur�ck gegeben.
     */
    protected void parseConfigFile() throws TcConfigException {
        String filename =
            Resources.getInstance().get(
                "COMMONCONFIG_URL_CONFIG_FILE",
                env.get(TcEnv.KEY_PATHS_ROOT),
                env.get(TcEnv.KEY_PATHS_CONFIG_ROOT),
                env.get(TcEnv.KEY_PATHS_CONFIG_FILE));
        Document document = null;
        try {
            logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_CONFIG_FILE", filename));
            logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_PARSE_START"));
            document = Xml.getParsedDocument(filename);
            logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_PARSE_STOP"));
        } catch (SAXParseException se) {
            logger.error(Resources.getInstance().get(
                    "COMMONCONFIG_LOG_PARSE_SAX_EXCEPTION",
                    new Integer(se.getLineNumber()),
                    new Integer(se.getColumnNumber())),
                se);
            throw new TcConfigException(Resources.getInstance().get("COMMONCONFIG_EXC_PARSE_ERROR"), se);
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("COMMONCONFIG_LOG_PARSE_ERROR"), e);
            throw new TcConfigException(Resources.getInstance().get("COMMONCONFIG_EXC_PARSE_ERROR"), e);
        }

        NodeList sections = document.getDocumentElement().getChildNodes();
        // Auslesen der Sections
        for (int i = 0; i < sections.getLength(); i++) {
            Node currNode = sections.item(i);
            if ("application".equals(currNode.getNodeName())) {
                try {
                    env.setAllValues(Xml.getParamMap(currNode));
                } catch (DataFormatException dfe) {
                    String msg = Resources.getInstance().get("COMMONCONFIG_LOG_PARSE_APPLICATION_ERROR");
                    logger.error(msg, dfe);
                    throw new TcConfigException(msg, dfe);
                }
            }else if("loginManager".equals(currNode.getNodeName())){
            	try {
					configLoginManager.putAll(Xml.getParamMap(currNode));
                } catch (DataFormatException dfe) {
                    String msg = Resources.getInstance().get("COMMONCONFIG_LOG_PARSE_APPLICATION_ERROR");
                    logger.error(msg, dfe);
                    throw new TcConfigException(msg, dfe);
                }
            }
        }
        env.overrideValues("common", "/de/tarent/octopus/overrides/common");

        moduleConfigs = new HashMap();

        // defaultModuleConfig setzen
        if (env.get(TcEnv.KEY_DEFAULT_MODULE) == null) {
            String msg = Resources.getInstance().get("COMMONCONFIG_LOG_DEFAULT_ENTRY_MISSING");
            logger.debug(msg);
        }
    }

    /**
     * Liefert die Wrapperklasse f�r Datenzugriffe
     * Diese Wrapperklassen werden alle hier verwaltet und vorr�tig gehalten.
     * Wenn jemand nach einer Datenzugriffsklasse fragt,
     * wird erst im aktuellen Modul unter dem Namen nach einer Datenquelle gesucht.
     * Wenn dort nichts vorhanden ist, wird das Defaultmodule benutzt.
     *
     * @param dataAccessName Name einer Datenquelle im angegebenen Modul oder dem Defaultmodule
     * @param moduleName Name des Modules, indem gesucht werden soll.
     * @return In dem Attribut, des dataAccess Elementes in der Config Datei, 
     *         angegebener Subtype von TcGenericDataAccessWrapper oder TcGenericDataAccessWrapper als Default
     */
    public TcGenericDataAccessWrapper getDataAccess(String moduleName, String dataAccessName)
        throws TcDataAccessException {

        String dataAccessKey =
            Resources.getInstance().get("COMMONCONFIG_KEY_ACCESS_WRAPPER", moduleName, dataAccessName);

        TcGenericDataAccessWrapper accessWrapper;

        accessWrapper = (TcGenericDataAccessWrapper) bufferedDataAccessWrappers.get(dataAccessKey);
        if (accessWrapper != null && accessWrapper.isOld()) {
            bufferedDataAccessWrappers.remove(dataAccessKey);
            try {
				accessWrapper.disconnect();
                logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_CLOSED_ACCESS_WRAPPER", dataAccessKey));
			} catch (SQLException e) {
                logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_ERROR_CLOSING_WRAPPER", dataAccessKey), e);
			}
            accessWrapper = null;
        }

        if (accessWrapper == null) {
            // Neu erstellen
            logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_CREATING_ACCESS_WRAPPER", dataAccessKey));
            Map source = null;

            TcModuleConfig module = getModuleConfig(moduleName);
            if (module != null) {
                Map dataAccess = module.getDataAccess();
                source = (Map) dataAccess.get(dataAccessName);
            }
            if (source == null && defaultModuleConfig != null) {
                Map dataAccess = defaultModuleConfig.getDataAccess();
                source = (Map) dataAccess.get(dataAccessName);
            }

            logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_ACCESS_WRAPPER_DATA", source));
            if (source == null) {
                String msg = Resources.getInstance().get("COMMONCONFIG_LOG_ACCESS_WRAPPER_NO_DATA", dataAccessKey);
                logger.error(msg);
                throw new TcDataAccessException(msg);
            }

            TarDBConnection dbConnection = new TarDBConnection(source);
            try {
                Class clazz = module.getClassLoader().loadClass(dbConnection.getAccessWrapperClassName());
                accessWrapper = (TcGenericDataAccessWrapper) clazz.newInstance();
                accessWrapper.setDBConnection(dbConnection);
                logger.debug(Resources.getInstance().get(
                        "COMMONCONFIG_LOG_CREATED_ACCESS_WRAPPER",
                        dbConnection.getAccessWrapperClassName()));
            } catch (Exception e) {
                String msg = Resources.getInstance().get("COMMONCONFIG_LOG_ACCESS_WRAPPER_ERROR", dataAccessKey);
                logger.error(msg, e);
                throw new TcDataAccessException(msg, e);
            }
            bufferedDataAccessWrappers.put(dataAccessKey, accessWrapper);
        }

        return accessWrapper;
    }

    /**
     * Diese Methode verwirft die Datenzugriffsinstanzen eines Moduls. 
     * 
     * @param moduleName Name des betroffenen Moduls
     */
    public void dropModuleDataAccess(String moduleName) {
        if (moduleName == null)
            return;
        MessageFormat format = new MessageFormat(Resources.getInstance().get("COMMONCONFIG_KEY_ACCESS_WRAPPER"));
        StringBuffer buffer = new StringBuffer();
        Iterator itAccessWrappers = bufferedDataAccessWrappers.entrySet().iterator();
        int i;
        String key = null;
        for (i = 0; itAccessWrappers.hasNext(); i++) try {
            Map.Entry entry = (Map.Entry) itAccessWrappers.next();
            key = entry.getKey().toString();
            Object[] refs = format.parse(key);
            if (moduleName.equals(refs[0])) {
                TcGenericDataAccessWrapper wrapper = (TcGenericDataAccessWrapper) entry.getValue();
                try {
					wrapper.disconnect();
				} catch (SQLException e) {
                    logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_ERROR_CLOSING_WRAPPER", key), e);
                }
                itAccessWrappers.remove();
                if (buffer.length() > 0)
                    buffer.append(", ");
                buffer.append(key);
            }
        } catch(ParseException pe) {
            logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_INVALID_ACCESS_KEY", key), pe);
        }
        logger.debug(Resources.getInstance().get("COMMONCONFIG_LOG_REMOVED_ACCESS_WRAPPERS", moduleName, new Integer(i), buffer));
    }

    /**
     * Diese Methode liefert eine strukturierte Liste der Tasks des angegebenen
     * Moduls.
     * 
     * @param moduleName Name des betreffenden Moduls.
     * @return eine strukturierte Taskliste zum betreffenden Modul oder null.
     */
    public TcTaskList getTaskList(String moduleName) {
        TcModuleConfig module = getModuleConfig(moduleName);
        return module != null ? module.getTaskList() : null;
    }

    /**
     * Verzeichnis, indem die Templates f�r ein bestimmtes Modul liegen.
     *
     * @return Verzeichnis
     */
    public File getTemplateRootPath(String moduleName) {
        TcModuleConfig config = getModuleConfig(moduleName);
        if (config.getParam(TcEnv.KEY_PATHS_TEMPLATE_ROOT) == null ||
        		config.getParam(TcEnv.KEY_PATHS_TEMPLATE_ROOT).length() == 0)
        	return config.getRealPath();
        else
        	return new File(config.getRealPath(), config.getParam(TcEnv.KEY_PATHS_TEMPLATE_ROOT));
    }

    /**
     * Verzeichnis, indem die Templates f�r ein bestimmtes Modul liegen.
     * Relativ zum Rootverzeichnis der Module
     *
     * @return Verzeichnisname mit abschlie�endem /
     */
    public String getRelativeTemplateRootPath(String moduleName) {
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        if (moduleConfig != null)
            return moduleConfig.getParam(TcEnv.KEY_PATHS_TEMPLATE_ROOT);
        return "./";
    }

    /**
     * Verzeichiss, indem sich die Statischen Web Ressourcen f�r das angegebene Modul befinden.
     * Relativ zum Rootverzeichnis des Servers
     *
     * @return Verzeichnissname mit abschlie�endem /
     */
    public String getRelativeWebRootPath(String moduleName) {
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        return (moduleConfig != null) ? moduleConfig.getWebPathStatic() : '/' + moduleName + '/';
    }

    /**
	 * Liefert das Config Objekt eines modules
	 */
    public TcModuleConfig getModuleConfig(String moduleName) {
        if (moduleConfigs.containsKey(moduleName))
        	return (TcModuleConfig) moduleConfigs.get(moduleName);
        
        synchronized (moduleConfigs) {
        	if (moduleLookup == null) {
        		logger.error(Resources.getInstance().get("COMMONCONFIG_LOG_MODULELOOKUP_NA", moduleName));
        		return null;
        	}
        	
        	Preferences modulePreferences = octopus.getModulePreferences(moduleName);
    		File modulePath = moduleLookup.getModulePath(moduleName);
    		
			if (modulePath == null) {
				String modulePrefPath = modulePreferences.get(TcModuleLookup.PREF_NAME_REAL_PATH, null);
				logger.info(Resources.getInstance().get("OCTOPUS_STARTER_LOG_MODULE_PATH_PREFERENCES", moduleName));
				if (modulePrefPath != null && modulePrefPath.length() != 0)
					modulePath = new File(modulePrefPath);
			}
			
    		File configFile = new File(modulePath, "config.xml");
    		if (!configFile.exists()) {
    			configFile = new File(modulePath, "module-config.xml");
    		}
    		if (!configFile.exists()) {
    			String configEnvFile = (String) env.getValue(TcEnv.KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + moduleName);
    			if (File.separatorChar != '/')
    				configEnvFile = configEnvFile.replace('/', File.separatorChar);
    			if (new File(configEnvFile).isAbsolute())
        			configFile = new File(configEnvFile);
    			else
    				configFile = new File(System.getProperty("user.dir"), configEnvFile);
    		}
    		if (!configFile.exists()) {
    			logger.error(Resources.getInstance().get("OCTOPUS_STARTER_LOG_MODULE_CONFIG_PATH_INVALID", moduleName, configFile));
    			return null;
    		}
    		
    		try {
    			logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_PARSING_MODULE_CONFIG", configFile, moduleName));
    			Document document = Xml.getParsedDocument(Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath()));
    			TcModuleConfig moduleConfig = new TcModuleConfig(
    					moduleName,
    					modulePath,
    					document,
    					modulePreferences);
    			moduleConfigs.put(moduleName, moduleConfig);
    			
        		modulePreferences.put(TcModuleLookup.PREF_NAME_REAL_PATH, configFile.getParent());
        		env.setValue(TcEnv.KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + moduleName, configFile.getAbsolutePath());
        		
        		registerModule(moduleName, moduleConfig);
        		
    			return moduleConfig;
    		} catch (SAXParseException e) {
    			logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_SAX_EXCEPTION", new Integer(e.getLineNumber()), new Integer(e.getColumnNumber())), e);
    		} catch (DataFormatException e) {
    			logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_FORMAT_EXCEPTION"), e);
    		} catch (Exception e) {
    			logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_EXCEPTION"), e);
    		}
    		
    		modulePreferences.remove(TcModuleLookup.PREF_NAME_REAL_PATH);
    		env.remove(TcEnv.KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + moduleName);
    		
    		return null;
        }
    }

    /**
     * Diese Methode registriert ein Modul mittels der �bergebenen Modulkonfiguration.
     *  
     * @param moduleName Modulname
     * @param config Modulkonfiguration
     */
    public void registerModule(String moduleName, TcModuleConfig moduleConfig) {
        if (moduleConfig == null)
            return;
        if (moduleConfigs.containsKey(moduleName))
            deregisterModule(moduleName);
        moduleConfigs.put(moduleName, moduleConfig);
        Map taskErrorsMap = moduleConfig.getTaskList().getErrors();
        if (taskErrorsMap != null) {
            Iterator itTaskErrors = taskErrorsMap.entrySet().iterator();
            while (itTaskErrors.hasNext()) {
                Map.Entry taskErrors = (Map.Entry) itTaskErrors.next();
                Object task = taskErrors.getKey();
                if (!(taskErrors.getValue() instanceof List)) {
                    logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_TASK_ERROR_INVALID", moduleName, task, taskErrors.getValue()));
                    continue;
                }
                Iterator itErrors = ((List)taskErrors.getValue()).iterator();
                while (itErrors.hasNext())
                    logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_TASK_ERROR", moduleName, task, itErrors.next()));
            }
        }
        try {
            octopus.doAutostart(moduleName, this);
        } catch (Exception e) {
            logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_AUTOSTART_ERROR", moduleName), e);
        }
    }

    /**
     * Diese Methode deregistriert ein Modul.
     * 
     * @param moduleName Modulname
     */
    public void deregisterModule(String moduleName) {
        if (moduleConfigs.containsKey(moduleName)) {
            try {
                octopus.doCleanup(moduleName, this);
            } catch (Exception e) {
                logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_CLEANUP_ERROR", moduleName), e);
            }
            try {
                dropModuleDataAccess(moduleName);
            } catch (Exception e) {
                logger.warn(Resources.getInstance().get("COMMONCONFIG_LOG_CLEANUP_ERROR", moduleName), e);
            }
            moduleConfigs.remove(moduleName);
        }
    }

    /**
     * Liefert einen <code>Iterator</code> aller existierenden Module.
     * @return Iterator
     */
    public Iterator getExistingModuleNames() {
        return moduleConfigs.keySet().iterator();
    }

    /**
     * Verzeichnis, indem die Daten des angegebenen Modules liegen.
     *
     * @return Verzeichnis
     */
    public File getModuleRootPath(String moduleName) {
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        return (moduleConfig != null) ? moduleConfig.getRealPath() : null;
    }

    /**
     * Liefert den Typ der Response, der benutzt werden soll,
     * wenn keiner angegeben ist, z.B. wenn ein Fehler auf getreten ist.
     */
    public String getDefaultResponseType(String moduleName) {
        String result = null;
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        if (moduleConfig != null)
            result = moduleConfig.getParam(TcEnv.KEY_DEFAULT_RESPONSE_TYPE);
        if (result == null || result.length() == 0)
            result = env.get(TcEnv.KEY_DEFAULT_RESPONSE_TYPE);
        return result;
    }
    /**
     * Gibt den Namen einer Fehleraufgabe 
     */
    public String getDefaultErrorDescriptionName(String moduleName) {
        String result = null;
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        if (moduleConfig != null)
            result = moduleConfig.getParam(TcEnv.KEY_DEFAULT_ERROR_DESCRIPTION);
        if (result == null || result.length() == 0)
            result = env.get(TcEnv.KEY_DEFAULT_ERROR_DESCRIPTION);
        return result;
    }

    /**
     * Liefert das Default-Encoding z.B. f�r Velocity-Templates
     */
    public String getDefaultEncoding(String moduleName) {
        String result = null;
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        if (moduleConfig != null)
            result = moduleConfig.getParam(TcEnv.KEY_DEFAULT_ENCODING);
        if (result == null || result.length() == 0)
            result = env.get(TcEnv.KEY_DEFAULT_ENCODING);
        return result;
    }

    /**
     * Liefert den ContentType, der gesetzt werden soll, wenn nichts anderes angegeben ist.
     */
    public String getDefaultContentType(String moduleName) {
        String result = null;
        TcModuleConfig moduleConfig = getModuleConfig(moduleName);
        if (moduleConfig != null)
            result = moduleConfig.getParam(TcEnv.KEY_DEFAULT_CONTENT_TYPE);
        if (result == null || result.length() == 0)
            result = env.get(TcEnv.KEY_DEFAULT_CONTENT_TYPE);
        return result;
    }

    /**
     * Liefert das Default-Modul
     */
    public String getDefaultModuleName() {
        return env.get(TcEnv.KEY_DEFAULT_MODULE);
    }

    /**
     * Diese Methode erzeug eine neue {@link PersonalConfig}-Instanz. Die verwendete
     * Klasse kann �ber den Modul-Parameter {@link TcModuleConfig#CONFIG_PARAM_PERSONAL_CONFIG_CLASS}
     * gesetzt werden, Vorgabe ist {@link TcPersonalConfig}. 
     * 
     * @param module
     * @return eine leere {@link PersonalConfig}-Instanz
     * @throws TcConfigException 
     */
    public PersonalConfig createNewPersonalConfig(String module) throws TcConfigException {
        PersonalConfig pConfig = null;
        if (module != null && module.length() > 0)
            pConfig = getModuleConfig(module).createNewPersonalConfig();
        else
            pConfig = new TcPersonalConfig();
        pConfig.setUserGroups(new String[]{PersonalConfig.GROUP_ANONYMOUS});
        pConfig.setUserLogin("anonymous");
        pConfig.setUserID(new Integer(0));
        pConfig.setUserGivenName("givenName");
        pConfig.setUserLastName("lastName");
        pConfig.setUserEmail("anonymous@anonymous.org");
        return pConfig;
    }
    

    /**
     * Diese Methode liefert einen LoginManager f�r ein bestimmtes Modul. Liegt im Modul
     * keine (ausreichende) LoginManager-Konfiguration vor, so wird der Default-LoginManager
     * des Octopus geliefert. 
     * 
     * @param module Name des Moduls, f�r den ein LoginManager gesucht wird.
     * @return LoginManager f�r das angegebene Modul
     * @see #getLoginManager()
     */
    public LoginManager getLoginManager(String module) throws TcSecurityException {
        TcModuleConfig moduleConfig = getModuleConfig(module);
        return getLoginManager(moduleConfig);
    }

    /**
     * Diese Methode liefert einen LoginManager f�r ein bestimmtes Modul. Liegt im Modul
     * keine (ausreichende) LoginManager-Konfiguration vor, so wird der Default-LoginManager
     * des Octopus geliefert. 
     * 
     * @param moduleConfig Konfiguration des Moduls, f�r den ein LoginManager gesucht wird.
     * @return LoginManager f�r das angegebene Modul
     * @see #getLoginManager()
     */
    public LoginManager getLoginManager(TcModuleConfig moduleConfig) throws TcSecurityException {
        LoginManager loginManager = moduleConfig != null ? moduleConfig.getLoginManager() : null;
        return loginManager != null ? loginManager : getLoginManager();
    }

    /**
     * Diese Methode liefert einen Default-LoginManager.
     * 
     * @return Default-LoginManager
     * @throws TcSecurityException
     */
    public synchronized LoginManager getLoginManager() 
    	throws TcSecurityException {
        if (loginManager == null) {
        	Map loginManagerParams = getConfigLoginManager();
            if (!loginManagerParams.isEmpty()) {
                logger.debug("Default-Login-Manager-Erstellung");
                //Octopus m�chte seine Config selber machen...
                String loginManagerClassName = (String) loginManagerParams.get("loginManagerClass");
                if (loginManagerClassName != null) {
                    logger.debug("Lade LoginManager Klasse: " + loginManagerClassName);
                    try {
                        //Eintrag ist angegeben...
                        Class loginManagerClass = Class.forName(loginManagerClassName);
                        loginManager = (LoginManager) loginManagerClass.newInstance();
                        loginManager.setConfiguration(loginManagerParams);
                    } catch (Exception e) {
                        logger.error("Fehler beim Laden des LoginManagers." + e.getMessage());
                        throw new TcSecurityException(TcSecurityException.ERROR_SERVER_AUTH_ERROR, e);
                    }
                }
            }
            if (loginManager == null) {
                //Hier der Fallback-Teil f�r die alte Konfiguration...
                String authType = env.get(TcEnv.KEY_AUTHENTICATION_TYPE);
                logger.debug("LoginManager: Fallback auf Octopus 1.1 Variante: " + authType);
                if (AUTH_TYPE_LDAP.equalsIgnoreCase(authType)) {
                    loginManager = new LoginManagerLDAP();
                } else if (AUTH_TYPE_XML.equalsIgnoreCase(authType)) {
                    loginManager = new LoginManagerXML();
                } else if (AUTH_TYPE_FILTER.equalsIgnoreCase(authType)) {
                    loginManager = new LoginManagerFilter();
                } else if (AUTH_TYPE_ACCEPT.equalsIgnoreCase(authType)) {
                    loginManager = new LoginManagerAcceptAll();
                } else {
                    String msg = Resources.getInstance().get("COMMONCONFIG_LOG_INVALID_AUTH_METHOD", authType);
                    logger.error(msg);
                    throw new TcSecurityException(msg);
                }
            }
        }
        return loginManager;
    }


        
    /**
     * Methode, die bestimmte Config ausliest
     * @param key - der auszulesende Key
     * @return Wert
     */
    public String getConfigData(String key) {
        return env.get(key);
    }

    /**
     * Returns an iterator over all available configuration
     * items.
     * 
     * @return Iterator.
     */
    public Iterator getConfigKeys() {
        return env.keySet().iterator();
    }

    public void setModuleLookup(TcModuleLookup moduleLookup) {
    	this.moduleLookup = moduleLookup;
    }

    public TcModuleLookup getModuleLookup() {
    	return moduleLookup;
    }

    /**
     * Diese Methode liefert die Umgebungswerte
     * 
     * @return Umgebungsobjekt.
     */
    public TcEnv getEnvironment() {
        return env;
    }

    /**
	 * @return Returns the configLoginManager.
	 */
	public Map getConfigLoginManager() {
		return configLoginManager;
	}
}
