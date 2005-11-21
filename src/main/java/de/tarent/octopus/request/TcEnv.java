/* $Id: TcEnv.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/** 
 * Ein key=value Container für Einstellungswerte.
 *
 * Die Keys sollten ein Prefix besitzen, das bezeichet, für welches Modul oder Package diese Einstellungen relevant sind.
 * <br>z.B. "tcResponse.templatePath"
 * <br><br>
 * Wenn eine Einstellung für mehrere Module ist, soll das prefix "global" benutzt werden
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcEnv extends HashMap {
    /**
	 * serialVersionUID = -3477717156564940686L
	 */
	private static final long serialVersionUID = -3477717156564940686L;

    /**
     * Setzt die Map mit den Einstellungen. Alle vorhandenen Werte werden überschrieben.
     *
     * @param envMap Erwartet eine Map mit Strings als key und Strings und Stringarrays als Values
     */
    public void setAllValues(Map envMap) {
        this.putAll(envMap);
    }

    /**
     * Setzt eine  Einstellung. Ein vorhandener Wert wird überschrieben.
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @param value Wert
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String key, String value) {
        return this.put(key, value);
    }

    /**
     * Setzt eine  Einstellung. Ein vorhandener Wert wird überschrieben.
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
     * Setzt ein Einstellungsarray. Ein vorhandener Wert wird überschrieben.
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @param values Werte als Stringarray
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String key, String[] values) {
        return this.put(key, values);
    }

    /**
     * Setzt ein Einstellungsarray. Ein vorhandener Wert wird überschrieben.
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
     * Gibt einen Wert als Object zurück
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Ein String oder String[] Objekt
     */
    public Object getValue(String key) {
        return this.get(key);
    }

    /**
     * Gibt einen Wert als String zurück
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
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
     * Gibt einen Wert als String zurück
     * Wrapper für getValueAsString
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
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
     * Gibt einen Wert als String[] zurück
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
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
     * Gibt einen Wert als boolean zurück
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
     * Gibt einen Wert als Zahl zurück
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Zahlwert oder 0, wenn es kein gültiger Zahlwert ist.
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
     * Gibt einen Wert als Object zurück
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
     * Gibt einen Wert als String zurück
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
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
     * Gibt einen Wert als String[] zurück
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
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
     * Gibt einen Wert als boolean zurück
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
     * Gibt einen Wert als Zahl zurück
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
     * Diese Methode überschreibt Werte dieses Environments aus den Preferences.
     * 
     * @param context Kontext für Log-Ausgaben
     * @param systemNodeName Systemknotenbezeichner für den Override-Bereich.
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
                    logger.config("[" + context + "] Override for " + key + ": " + value);
                    setValue(key, value);
                }
            }
        } catch (BackingStoreException e) {
            logger.log(Level.SEVERE, "[" + context + "] Preferences-API-Zugriff", e);
        }
    }

    //
    // Object
    //
    /**
     * Diese Methode liefert eine String-Darstellung des Environments
     * für Debug-Zwecke.  
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("TcEnv:\n");
        for (Iterator e = this.keySet().iterator(); e.hasNext();) {
            String key = (String) e.next();
            Object val = this.get(key);
            if (val instanceof String[]) {
                String[] sArr = (String[]) val;
                sb.append(key + " =>  (");
                for (int i = 0; i < sArr.length; i++) {
                    sb.append(" \"" + sArr[i] + "\" ");
                }
                sb.append(")\n");
            } else
                sb.append(key + " => " + val + "\n");
        }
        return "" + sb;
    }

    //
    // Schlüsselkonstanten
    //
   
    /** Prefix für den Key, unter dem der direkte Pfad zu einer 
        Configdatei eines Modules angegeben werden kann 
        Der gesamte key setzt sich zusammen aus: KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + modulname */
    public final static String KEY_MODULE_CONFIGFILE_LOCATION_PREFIX = "moduleConfig.";
    public final static String KEY_OMIT_SESSIONS = "omitSessions";
    public final static String KEY_PATHS_LOGFILE = "paths.logfile";
    public final static String KEY_PATHS_ROOT = "paths.root";
    public final static String KEY_LOGGING_LEVEL = "logging.level";
    public final static String KEY_LOG_SOAP_REQUEST_LEVEL = "logging.soap.request.level";
    public final static String KEY_PATHS_CONFIG_ROOT = "paths.configRoot";
    public final static String KEY_PATHS_CONFIG_FILE = "paths.configFile";
    public final static String KEY_PATHS_MODULES = "paths.modules";
    public final static String KEY_DEFAULT_MODULE = "defaultModule";
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
//    public final static String KEY_ = "";
//    public final static String KEY_ = "";

    //
    // private statische Variablen
    //
    /** Logger für diese Klasse */
    private static Logger logger = Logger.getLogger(TcEnv.class.getName());
} /* end class TcRequest */
