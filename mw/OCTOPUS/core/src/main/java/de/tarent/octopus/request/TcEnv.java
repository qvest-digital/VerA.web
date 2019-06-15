package de.tarent.octopus.request;

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

import org.apache.commons.logging.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Ein key=value Container für Einstellungswerte.
 *
 * Die Keys sollten ein Prefix besitzen, das bezeichet, für welches Modul oder Package
 * diese Einstellungen relevant sind, z.B. "tcResponse.templatePath"
 *
 * Wenn eine Einstellung für mehrere Module ist, soll das prefix "global" benutzt werden
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
import lombok.extern.log4j.Log4j2;@Log4j2
public class TcEnv extends HashMap {
    private static final long serialVersionUID = 1608152979959890490L;

    public TcEnv() {
    }

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
     * @param key   Der Key mit Prefix, z.B. "global.sessionTimeout"
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
     * @param key    Der Key ohne Prefix, z.B. "sessionTimeout"
     * @param value  Wert
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String prefix, String key, String value) {
        return this.put(prefix + "." + key, value);
    }

    /**
     * Setzt ein Einstellungsarray. Ein vorhandener Wert wird überschrieben.
     *
     * @param key    Der Key mit Prefix, z.B. "global.sessionTimeout"
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
     * @param key    Der Key ohne Prefix, z.B. "sessionTimeout"
     * @param values Werte als Stringarray
     * @return Den Wert, der vorher unter diesem Key gespeichert war
     */
    public Object setValue(String prefix, String key, String[] values) {
        return this.put(prefix + "." + key, values);
    }

    /**
     * Gibt einen Wert als String oder String[] zurück.
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Ein String oder String[] Objekt
     */
    public Object getValue(String key) {
        return this.get(key);
    }

    /**
     * Gibt einen Wert als Object zurück.
     */
    public Object getValueAsObject(String key) {
        return super.get(key);
    }

    /**
     * Gibt einen Wert als String zurück
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurückgegeben.
     */
    public String getValueAsString(String key) {
        Object value = this.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof String[] && ((String[]) value).length > 0) {
            return ((String[]) value)[0];
        } else {
            return null;
        }
    }

    /**
     * Gibt einen Wert als String zurück
     * Wrapper für getValueAsString
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurückgegeben.
     */
    public String get(String key) {
        Object value = super.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof String[] && ((String[]) value).length > 0) {
            return ((String[]) value)[0];
        } else {
            return null;
        }
    }

    /**
     * Gibt einen Wert als String[] zurück
     *
     * @param key Der Key mit Prefix, z.B. "global.sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurückgegeben.
     */
    public String[] getValueAsStringArray(String key) {
        Object value = this.get(key);
        if (value instanceof String) {
            return new String[] { (String) value };
        } else if (value instanceof String[]) {
            return (String[]) value;
        } else {
            return null;
        }
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
        if (value instanceof String) {
            stringValue = (String) value;
        } else if (value instanceof String[]) {
            stringValue = ((String[]) value)[0];
        } else {
            return false;
        }

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
        if (value instanceof String) {
            stringValue = (String) value;
        } else if (value instanceof String[]) {
            stringValue = ((String[]) value)[0];
        } else {
            return 0;
        }

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
     * @param key    Der Key ohne Prefix, z.B. "sessionTimeout"
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
     * @param key    Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurückgegeben.
     */
    public String getValueAsString(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof String[] && ((String[]) value).length > 0) {
            return ((String[]) value)[0];
        } else {
            return null;
        }
    }

    /**
     * Gibt einen Wert als String[] zurück
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key    Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurückgegeben.
     */
    public String[] getValueAsStringArray(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        if (value instanceof String) {
            return new String[] { (String) value };
        } else if (value instanceof String[]) {
            return (String[]) value;
        } else {
            return null;
        }
    }

    /**
     * Gibt einen Wert als boolean zurück
     *
     * @param prefix Das Prefix, z.B. "global"
     * @param key    Der Key ohne Prefix, z.B. "sessionTimeout"
     * @return true, wenn der String, oder der erste Eintrag im String-Array "true" ist, false sonst. Nicht case-Sensitiv.
     */
    public boolean getValueAsBoolean(String prefix, String key) {
        key = prefix + "." + key;
        Object value = this.get(key);
        String stringValue;
        if (value instanceof String) {
            stringValue = (String) value;
        } else if (value instanceof String[]) {
            stringValue = ((String[]) value)[0];
        } else {
            return false;
        }

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
        if (value instanceof String) {
            stringValue = (String) value;
        } else if (value instanceof String[]) {
            stringValue = ((String[]) value)[0];
        } else {
            return 0;
        }

        return Integer.parseInt(stringValue);
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
        for (Iterator it = this.entrySet().iterator(); it.hasNext(); ) {
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
    // Schlüsselkonstanten
    //

    /**
     * Prefix für den Key, unter dem der direkte Pfad zu einer
     * Configdatei eines Modules angegeben werden kann
     * Der gesamte key setzt sich zusammen aus: KEY_MODULE_CONFIGFILE_LOCATION_PREFIX + modulname
     */
    public final static String KEY_MODULE_CONFIGFILE_LOCATION_PREFIX = "moduleConfig.";
    public final static String KEY_OMIT_SESSIONS = "omitSessions";
    public final static String KEY_OMIT_HTTPAUTH = "omitHttpAuth";
    public final static String KEY_PATHS_ROOT = "paths.root";
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
