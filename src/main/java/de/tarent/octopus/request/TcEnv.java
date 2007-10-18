/* $Id: TcEnv.java,v 1.10.2.1 2007/10/18 11:54:20 christoph Exp $
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

package de.tarent.octopus.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

/** 
 * Ein key=value Container f�r Einstellungswerte.
 *
 * Die Keys sollten ein Prefix besitzen, das bezeichet, f�r welches Modul oder Package diese Einstellungen relevant sind.
 * <br>z.B. "tcResponse.templatePath"
 * <br><br>
 * Wenn eine Einstellung f�r mehrere Module ist, soll das prefix "global" benutzt werden
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcEnv extends HashMap {
	//
    // private statische Variablen
    //
    /** Logger f�r diese Klasse */
    private static Log logger = LogFactory.getLog(TcEnv.class);
    /**
	 * serialVersionUID = -3477717156564940686L
	 */
	private static final long serialVersionUID = -3477717156564940686L;

	public TcEnv() {
		
	}

    /**
     * Setzt die Map mit den Einstellungen. Alle vorhandenen Werte werden �berschrieben.
     *
     * @param envMap Erwartet eine Map mit Strings als key und Strings und Stringarrays als Values
     */
    public void setAllValues(Map envMap) {
        this.putAll(envMap);
    }

    /**
     * Setzt eine  Einstellung. Ein vorhandener Wert wird �berschrieben.
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @param value Wert
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String key, String value) {
        return this.put(key, value);
    }

    /**
     * Setzt eine  Einstellung. Ein vorhandener Wert wird �berschrieben.
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @param value Wert
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String prefix, String key, String value) {
        return this.put(prefix + "." + key, value);
    }

    /**
     * Setzt ein Einstellungsarray. Ein vorhandener Wert wird �berschrieben.
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @param values Werte als Stringarray
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String key, String[] values) {
        return this.put(key, values);
    }

    /**
     * Setzt ein Einstellungsarray. Ein vorhandener Wert wird �berschrieben.
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @param values Werte als Stringarray
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String prefix, String key, String[] values) {
        return this.put(prefix + "." + key, values);
    }

    /**
     * Gibt einen Wert als String oder String[] zur�ck.
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Ein String oder String[] Objekt
     */
    public Object getValue(String key) {
        return this.get(key);
    }

    /**
     * Gibt einen Wert als Object zur�ck.
     * @param key
     * @return
     */
    public Object getValueAsObject(String key) {
    	return super.get(key);
    }

    /**
     * Gibt einen Wert als String zur�ck
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String getValueAsString(String key) {
        Object value = this.get(key);
        if (value instanceof String)
            return (String) value;
        else if (value instanceof String[] && ((String[]) value).length > 0)
            return ((String[]) value)[0];
        else
            return null;
    }

    /**
     * Gibt einen Wert als String zur�ck
     * Wrapper f�r getValueAsString
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String get(String key) {
        Object value = super.get(key);
        if (value instanceof String)
            return (String) value;
        else if (value instanceof String[] && ((String[]) value).length > 0)
            return ((String[]) value)[0];
        else
            return null;
    }

    /**
     * Gibt einen Wert als String[] zur�ck
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String[] getValueAsStringArray(String key) {
        Object value = this.get(key);
        if (value instanceof String)
            return new String[] {(String) value };
        else if (value instanceof String[])
            return (String[]) value;
        else
            return null;
    }

    /**
     * Gibt einen Wert als boolean zur�ck
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return true, wenn der String, oder der erste Eintrag im String-Array "true" ist, false sonst. Nicht case-Sensitiv.
     */
    public boolean getValueAsBoolean(String key) {
        Object value = super.get(key);
        String stringValue;
        if (value instanceof String)
            stringValue = (String) value;
        else if (value instanceof String[])
            stringValue = ((String[]) value)[0];
        else
            return false;

        return stringValue.toLowerCase().equals("true");
    }

    /**
     * Gibt einen Wert als Zahl zur�ck
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Zahlwert oder 0, wenn es kein g�ltiger Zahlwert ist.
     */
    public int getValueAsInt(String key) {
        Object value = super.get(key);
        String stringValue;
        if (value instanceof String)
            stringValue = (String) value;
        else if (value instanceof String[])
            stringValue = ((String[]) value)[0];
        else
            return 0;

        try {
            return Integer.parseInt(stringValue);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gibt einen Wert als Object zur�ck
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Ein String oder String[] Objekt
     */
    public Object getValue(String prefix, String key) {
        key = prefix + "." + key;
        return this.get(key);
    }

    /**
     * Gibt einen Wert als String zur�ck
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String getValueAsString(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        if (value instanceof String)
            return (String) value;
        else if (value instanceof String[] && ((String[]) value).length > 0)
            return ((String[]) value)[0];
        else
            return null;
    }

    /**
     * Gibt einen Wert als String[] zur�ck
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String[] getValueAsStringArray(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        if (value instanceof String)
            return new String[] {(String) value };
        else if (value instanceof String[])
            return (String[]) value;
        else
            return null;
    }

    /**
     * Gibt einen Wert als boolean zur�ck
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return true, wenn der String, oder der erste Eintrag im String-Array "true" ist, false sonst. Nicht case-Sensitiv.
     */
    public boolean getValueAsBoolean(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        String stringValue;
        if (value instanceof String)
            stringValue = (String) value;
        else if (value instanceof String[])
            stringValue = ((String[]) value)[0];
        else
            return false;

        return stringValue.toLowerCase().equals("true");
    }

    /**
     * Gibt einen Wert als Zahl zur�ck
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return true, wenn der String, oder der erste Eintrag im String-Array "true" ist, false sonst. Nicht case-Sensitiv.
     */
    public int getValueAsInt(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        String stringValue;
        if (value instanceof String)
            stringValue = (String) value;
        else if (value instanceof String[])
            stringValue = ((String[]) value)[0];
        else
            return 0;

        return Integer.parseInt(stringValue);
    }

    /**
     * Diese Methode �berschreibt Werte dieses Environments aus den Preferences.
     * 
     * @param context Kontext f�r Log-Ausgaben
     * @param systemNodeName Systemknotenbezeichner f�r den Override-Bereich.
     */
    public void overrideValues(String context, String systemNodeName) {
        Preferences overrides = Preferences.systemRoot().node(systemNodeName);
        String[] keys;
        try {
            keys = overrides.keys();
            if (keys != null && keys.length > 0) {
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];
                    String value = overrides.get(key, get(key));
                    logger.debug("[" + context + "] Override for " + key + ": " + value);
                    setValue(key, value);
                }
            }
        } catch (BackingStoreException e) {
            logger.error("[" + context + "] Preferences-API-Zugriff", e);
        }
    }

    //
    // Object
    //
    /**
     * Diese Methode liefert eine String-Darstellung des Environments
     * f�r Debug-Zwecke.  
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("TcEnv:\n");
        for (Iterator it = this.entrySet().iterator(); it.hasNext();) {
        	Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() instanceof String[]) {
                String[] sArr = (String[]) entry.getValue();
                sb.append(entry.getKey() + " =>  (");
                for (int i = 0; i < sArr.length; i++) {
                    sb.append(" \"" + sArr[i] + "\" ");
                }
                sb.append(")\n");
            } else {
                sb.append(entry.getKey() + " => " + entry.getValue() + "\n");
            }
        }
        return sb.toString();
    }

    //
    // Schl�sselkonstanten
    //
   
    /** Prefix f�r den Key, unter dem der direkte Pfad zu einer 
        Configdatei eines Modules angegeben werden kann 
        Der gesamte key setzt sich zusammen aus: KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + modulname */
    public final static String KEY_MODULE_CONFIGFILE_LOCATION_PREFIX = "moduleConfig.";
    public final static String KEY_OMIT_SESSIONS = "omitSessions";
    public final static String KEY_PATHS_LOGFILE = "paths.logfile";
    public final static String KEY_PATHS_ROOT = "paths.root";
    public final static String KEY_LOGGING_BASELOGGER = "logging.baseLogger";
    public final static String KEY_LOGGING_LEVEL = "logging.level";
    public final static String KEY_LOGGING_PATTERN = "logging.pattern";
    public final static String KEY_LOGGING_LIMIT = "logging.limit";
    public final static String KEY_LOGGING_COUNT = "logging.count";
    public final static String KEY_LOGGING_PORT = "logging.port";
    public final static String KEY_LOG_SOAP_REQUEST_LEVEL = "logging.soap.request.level";
    public final static String KEY_USE_SOAP_NS_AS_MODULE = "useSOAPNSAsModule"; // default == true
    public final static String KEY_JMX_ENABLED = "jmxenabled"; // default == false
    public final static String KEY_RPCTUNNEL_ENABLED = "rpctunnelenabled"; // default == false
    public final static String KEY_PATHS_CONFIG_ROOT = "paths.configRoot";
    public final static String KEY_PATHS_CONFIG_FILE = "paths.configFile";
    public final static String KEY_PATHS_MODULES = "paths.modules";
    public final static String KEY_MODULES = "modules";
    public final static String KEY_MODULE_SOURCE = "source";
    public final static String KEY_DEFAULT_MODULE = "defaultModule";
    public final static String KEY_PRELOAD_MODULES = "preloadModules";
    public final static String KEY_WEBAPP_CONTEXT_PATH_NAME = "webappContextPathName";
    public final static String KEY_PATHS_TEMPLATE_ROOT = "paths.templateRoot";
    public final static String KEY_PATHS_WEB_ROOT = "paths.webRoot";
    public final static String KEY_PATHS_AXIS_CONFIGURATION = "paths.axisConfiguration";
    public final static String KEY_DEFAULT_RESPONSE_TYPE = "defaultResponseType";
    public final static String KEY_DEFAULT_ERROR_DESCRIPTION = "defaultErrorDescriptionName";
    public final static String KEY_DEFAULT_ENCODING = "defaultEncoding";
    public final static String KEY_DEFAULT_CONTENT_TYPE = "defaultContentType";
    public final static String KEY_AUTHENTICATION_TYPE = "auth.type";
    public final static String KEY_LDAP_URL = "ldapurl";
    public final static String KEY_LDAP_BASE_DN = "ldapbasedn";
    public final static String KEY_LDAP_RELATIVE = "ldaprelative";
    public final static String KEY_LDAP_AUTHORIZATION = "ldapauth";
    public final static String KEY_LDAP_USER = "ldapuser";
    public final static String KEY_LDAP_PWD = "ldappwd";
    public final static String KEY_RESPONSE_ERROR_LEVEL = "response.errorLevel";
    public final static String KEY_REQUEST_ALLOWED_TYPES = "request.allowedTypes";
    
    public final static String VALUE_RESPONSE_ERROR_LEVEL_DEVELOPMENT = "development";
    public final static String VALUE_RESPONSE_ERROR_LEVEL_RELEASE = "release";
    public final static String VALUE_REQUEST_TYPE_ANY = "ANY";
    public final static String VALUE_REQUEST_TYPE_SOAP = "SOAP";
    public final static String VALUE_REQUEST_TYPE_WEB = "WEB";
    public final static String VALUE_REQUEST_TYPE_XMLRPC = "XMLRPC";
    public final static String VALUE_REQUEST_TYPE_DIRECTCALL = "DIRECTCALL";
    public final static String VALUE_MODULE_SOURCE_SERVLET_PREFIX = "servlet:";
    public final static String VALUE_MODULE_SOURCE_FILE_PREFIX = "file:";
}
