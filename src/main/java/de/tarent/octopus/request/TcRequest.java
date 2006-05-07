/* $Id: TcRequest.java,v 1.3 2006/05/07 23:05:57 jens Exp $
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
package de.tarent.octopus.request;

import java.net.PasswordAuthentication;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import de.tarent.octopus.resource.Resources;

/** 
 * Datenkontainer zur Kapselung der Parameter einer Anfrage.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcRequest {
    //
    // Konstanten
    //
    // * Octopus-Anfragetypen
    //
    /** Anfragetyp WEB-Inhalt. */
    public static final int REQUEST_TYPE_WEB = 1;
    /** Anfragetypenflag GZIP */
    public static final int REQUEST_TYPE_FLAG_GZIP = 2;
    /** Anfragetypenflag PGP */
    public static final int REQUEST_TYPE_FLAG_PGP = 4;
    /** Anfragetyp XML-SOAP-Message mit POST gesendet. */
    public static final int REQUEST_TYPE_SOAP = 8;
    /** Anfragetyp XML-SOAP-Messages mit POST gesendet, mit GZIP komprimiert. */
    public static final int REQUEST_TYPE_GZIP_SOAP = REQUEST_TYPE_SOAP | REQUEST_TYPE_FLAG_GZIP;
    /** Anfragetyp XML-SOAP-Messages mit POST gesendet, mit GZIP komprimiert, mit PGP verschl�sselt */
    public static final int REQUEST_TYPE_PGP_SOAP = REQUEST_TYPE_GZIP_SOAP | REQUEST_TYPE_FLAG_PGP;
    /** Anfragetyp XML-RPC-Message mit POST gesendet */
    public static final int REQUEST_TYPE_XML_RPC = 16;
    /** Anfragetyp Direkte Anfrage aus einer Java-Anwendung heraus */
    public static final int REQUEST_DIRECT_CALL = 32;

    //
    // * Parameternamen
    //

    public final static String PARAM_PATH_INFO = "pathInfo";
    public final static String PARAM_HEADER = "header";
    public final static String PARAM_LOCALE = "locale";
    public final static String PARAM_FILE_CONTENT_TYPE = "ContentType";
    public final static String PARAM_FILE_CONTENT_NAME = "ContentName";
    public final static String PARAM_FILE_CONTENT_SIZE = "ContentSize";
    public final static String PARAM_FILE_CONTENT_STREAM = "ContentStream";
    public final static String PARAM_ENCODED_URL = "encodedUrl";
    public final static String PARAM_SESSION_ID = "jsessionid";
    public final static String PARAM_ASK_FOR_COOKIES = "askForCookies";
    public final static String PARAM_DEBUG = "debug";
    private final static String PARAM_TASK = "task";
    private final static String PARAM_MODULE = "module";
    public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_USERNAME = "username";
    public static final String PARAM_COOKIES = "cookies";


    //
    // �ffentliche statische Methoden
    //
    /**
     * Diese statische Methode erzeugt eine RequestID.
     * 
     * @return neue RequestID.
     */
    public static String createRequestID() {
        long idValue = (long) (System.currentTimeMillis() * ((1.0 + Math.random()) / 2));
        return new StringBuffer(Long.toHexString(idValue)).reverse().toString();
    }

    /**
     * Diese Methode liefert einen sprechenden Bezeichner f�r einen Anfragetyp.
     * 
     * @param requestType ein Anfragetyp-Wert.
     * @return eine sprechende Bezeichnung f�r den Anfragetyp.
     */
    public static String getRequestTypeName(int requestType) {
        String resourceName = null;
        switch(requestType) {
            case REQUEST_TYPE_WEB:          resourceName = "REQUEST_TYPE_WEB"; break;
            case REQUEST_TYPE_SOAP:         resourceName = "REQUEST_TYPE_SOAP"; break;
            case REQUEST_TYPE_GZIP_SOAP:    resourceName = "REQUEST_TYPE_GZIP_SOAP"; break;
            case REQUEST_TYPE_PGP_SOAP:     resourceName = "REQUEST_TYPE_PGP_SOAP"; break;
            default:                        resourceName = "REQUEST_TYPE_OTHER";                        
        }
        return Resources.getInstance().get(resourceName, new Integer(requestType));
    }

    /**
     * Diese Methode bestimmt, ob der �bergebene Anfragetyp ein Web-Typ
     * (HTML) ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, falls der Parameter ein Web-Anfragetyp ist.
     */
    public static boolean isWebType(int requestType) {
        return (requestType & REQUEST_TYPE_WEB) == REQUEST_TYPE_WEB;
    }
    
    /**
     * Diese Methode bestimmt, ob der �bergebene Anfragetyp ein SOAP-Typ ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, wenn Ja.
     */
    public static boolean isSoapType(int requestType) {
        return (requestType & REQUEST_TYPE_SOAP) == REQUEST_TYPE_SOAP;
    }
    
    /**
     * Diese Methode bestimmt, ob der �bergebene Anfragetyp ein XML-RPC-Typ ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, wenn Ja.
     */
    public static boolean isXmlRpcType(int requestType) {
        return (requestType & REQUEST_TYPE_XML_RPC) == REQUEST_TYPE_XML_RPC;
    }

    /**
     * Diese Methode bestimmt, ob der �bergebene Anfragetyp 
     * ein REQUEST_DIRECT_CALL-Typ ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, wenn Ja.
     */
    public static boolean isDirectCallType(int requestType) {
        return (requestType & REQUEST_DIRECT_CALL) == REQUEST_DIRECT_CALL;
    }
    
    //
    // Konstruktoren
    //
    /**
     * Der Konstruktor generiert eine neue Request-ID.
     */
    public TcRequest() {
        this.requestID = createRequestID();
    }

    /**
     * Der Konstruktor legt die �bergebene Request-ID ab.
     */
    public TcRequest(String requestID) {
        this.requestID = requestID;
    }

    //
    // Getter und Setter
    //
	/**
	 * Diese Methode liefert das Flag: werden Cookies gefordert?
	 * 
	 * @return Flag: werden Cookies gefordert?
	 */
	public boolean askForCookies() {
		return askForCookies;
	}

    /**
     * Diese Methode setzt das Flag: werden Cookies gefordert? 
     * 
     * @param b Flag: werden Cookies gefordert?
     */
	public void setAskForCookies(boolean b) {
		askForCookies = b;
	}

    /**
     * Diese Methode liefert das Flag: werden Cookies unterst�tzt? 
     * 
     * @return Flag: werden Cookies unterst�tzt?
     */
	public boolean supportCookies() {
		return supportCookies;
	}

    /**
     * Diese Methode setzt das Flag: werden Cookies unterst�tzt? 
     * 
     * @param b Flag: werden Cookies unterst�tzt?
     */
	public void setSupportCookies(boolean b) {
		supportCookies = b;
	}

	/**
	 * Diese Methode liefert das auszuf�hrende Modul. Wenn keines oder <code>null</code>
	 * explizit gesetzt wurde, wird versucht, es aus den Parametern zu gewinnen.
	 * 
	 * @return auszuf�hrendes Modul
	 */
    public String getModule() {
        return module != null ? module : getModuleFromParams();
    }
    
    /**
     * Diese Methode setzt das auszuf�hrende Modul. 
     * 
     * @param mod auszuf�hrendes Modul
     */
    public void setModule(String mod) {
        module = mod;
        // TODO: Nur bei entsprechend gesetztem Hack-Flag.
        // Etwa �ber statische Variable am TcRequest, die initial vom Octopus
        // gesetzt wird, oder etwas feiner direkt vor dem tats�chlichen Dispatchen,
        // wo eine Modul-spezifische Einstellung abgefragt werden kann.
        if (mod != null)
            setParam(PARAM_MODULE, mod);
    }

    /**
     * Diese Methode liefert die Passwort-Authentifizierung des Requests. Wenn
     * keine oder <code>null</code> explizit gesetzt wurde, wird versucht, diese
     * aus den Parametern zu gewinnen.
     * 
     * @return Passwort-Authentifizierung des Requests
     */
    public PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication != null ? passwordAuthentication : getAuthenticationFromParams();
    }

    /**
     * Diese Methode setzt die Passwort-Authentifizierung des Requests. 
     * 
     * @param pwdAuth Passwort-Authentifizierung des Requests
     */
    public void setPasswordAuthentication(PasswordAuthentication pwdAuth) {
        passwordAuthentication = pwdAuth;
    }
    
    /**
	 * Diese Methode liefert den auszuf�hrenden Task. Wenn keiner oder <code>null</code>
	 * explizit gesetzt wurde, wird versucht, ihn aus den Parametern zu gewinnen.
	 * 
	 * @return auszuf�hrender Task
     */
    public String getTask() {
        return task != null ? task : getTaskFromParams();
    }
    
    /**
     * Diese Methode setzt den auszuf�hrenden Task. 
     * 
     * @param tsk auszuf�hrender Task
     */
    public void setTask(String tsk) {
        task = tsk;
    }

    /**
     * Diese Methode liefert die Map der Request-Parameter.
     *
     * @return Map der Request-Parameter
     */
    public Map getRequestParameters() {
        return Collections.unmodifiableMap(requestParameters);
    }

    /**
     * Diese Methode setzt die Map der Request-Parameter.
     *
     * @param paramMap Map der Request-Parameter
     */
    public void setRequestParameters(Map paramMap) {
        requestParameters = paramMap;
    }

    /**
     * Liefert die ID des Requests f�r Logzwecke.
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Liefert einen Int Wert, der wiedergibt, ob die Anfrage als
     * WEB Request, SOAP, GZIP-SOAP, ... gekommen ist.
     * <br>Er kann �ber die Konstanten in TcRequestProxy aufgel��t werden.
     */
    public int getRequestType() {
        return requestType;
    }

    /**
     * Setzt einen Int Wert, der wiedergibt, ob die Anfrage als
     * WEB Request, SOAP, GZIP-SOAP, ... gekommen ist.
     * <br>Er kann �ber die Konstanten in TcRequestProxy aufgel��t werden.
     */
    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    //
    // Requestparameterverwaltung
    //
    /**
     * Gibt zur�ck ob der key im Request enthalten ist.
     * 
     * @param key
     */
    public boolean containsParam(String key) {
    	return requestParameters.containsKey(key);
    }

    /**
     * Gibt einen Wert als Object zur�ck
     *
     * @return Ein String oder String[] Objekt
     */
    public Object getParam(String key) {
        return requestParameters.get(key);
    }

    /**
     * Setzt ein Feld mit einem String
     */
    public void setParam(String key, Object value) {
        requestParameters.put(key, value);
    }

    /**
     * Gibt einen Wert als String zur�ck
     * Kurzform f�r getParamAsString
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String get(String key) {
        return getParamAsString(key);
    }

    /**
     * Gibt einen Wert als boolean zur�ck
     *
     * @return true, wenn der String, oder der erste Eintrag im String-Array "true" ist, false sonst. Nicht case-Sensitiv.
     */
    public boolean getParameterAsBoolean(String key) {
        Object value = getParam(key);
        String stringValue;
        if (value instanceof String)
            stringValue = (String) value;
        else if (value instanceof String[] && ((String[]) value).length > 0)
            stringValue = ((String[]) value)[0];
        else if (value instanceof Boolean)
            return ((Boolean) value).booleanValue();
        else
            return false;

        return "true".equalsIgnoreCase(stringValue);
    }

    /**
     * Gibt einen Wert als int zur�ck
     *
     * @return Zahlwert oder null, wenn es kein g�ltiger Zahlwert ist.
     */
    public int getParamAsInt(String key) {
        Object value = getParam(key);
        String stringValue;
        if (value instanceof String)
            stringValue = (String) value;
        else if (value instanceof String[] && ((String[]) value).length > 0)
            stringValue = ((String[]) value)[0];
        else if (value instanceof Number)
            return ((Number) value).intValue();
        else
            return 0;

        try {
            return Integer.parseInt(stringValue);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gibt einen Wert als String zur�ck
     *
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zur�ck gegeben.
     */
    public String getParamAsString(String key) {
        Object value = getParam(key);
        if (value instanceof String)
            return (String) value;
        else if (value instanceof String[])
            return ((String[]) value).length > 0 ? ((String[]) value)[0] : "";
        else if (value != null)
            return value.toString();
        else
        	return null;
    }

    /**
     * Gibt einen Wert als String[] zur�ck
     *
     * @return Ein String[], ggfs als Wrapper eines einzelnen Elements. 
     */
    public String[] getParameterAsStringArray(String key) {
        Object value = getParam(key);
        if (value instanceof String)
            return new String[] {(String) value };
        else if (value instanceof String[])
            return (String[]) value;
        else if (value != null)
            return new String[] {value.toString()};
        else
        	return null;
    }

    //
    // Klasse Object
    //
    /**
     * Gibt den Inhalt als String zur�ck
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("TcRequest: ")
          .append(getModule())
          .append('.')
          .append(getTask())
          .append(" by ")
          .append(getPasswordAuthentication() != null ? getPasswordAuthentication().getUserName() : "?")
          .append('\n');

        for (Iterator e = requestParameters.keySet().iterator(); e.hasNext();) {
            String key = (String) e.next();
            Object val = getParam(key);
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
        return sb.toString();
    }

    //
    // private Hilfsmethoden
    //
    /**
     * Diese Methode versucht, aus den Request-Parametern eine Passwort-Authentifizierung
     * zu gewinnen.
     * 
     * @return Passwort-Authentifizierung aus den Request-Parametern, falls zumindest
     *  der Benutzername gefunden wurde, sonst <code>null</code>.
     */
    protected PasswordAuthentication getAuthenticationFromParams() {
        if (requestParameters != null) {
            String username = get(PARAM_USERNAME);
            String password = get(PARAM_PASSWORD);
            if (username != null)
                return new PasswordAuthentication(username, password != null ? password.toCharArray() : new char[0]);
        }
        return null;
    }
    
    /**
     * Diese Methode versucht, aus den Request-Parametern das auszuf�hrende Modul zu
     * gewinnen.
     * 
     * @return Modul aus den Request-Parametern, sonst <code>null</code>.
     */
    protected String getModuleFromParams() {
        return (requestParameters != null) ? get(PARAM_MODULE) : null;
    }
    
    /**
     * Diese Methode versucht, aus den Request-Parametern den auszuf�hrenden Task zu
     * gewinnen.
     * 
     * @return Task aus den Request-Parametern, sonst <code>null</code>.
     */
    protected String getTaskFromParams() {
        return (requestParameters != null) ? get(PARAM_TASK) : null;
    }
    
    //
    // Variablen
    //
    /** Request-ID, wichtig insbesondere beim Loggen */
    private String requestID = "";
	/** Passwort-Authentifizierung */
    private PasswordAuthentication passwordAuthentication = null;
    /** Request-Modul */
    private String module = null;
    /** Request-Task */
    private String task = null;
    /** Requestparameter */
    private Map requestParameters = null;
    /** Requesttyp, vergleiche REQUEST_TYPE_* */
    private int requestType;
    /** Flag: werden Cookies unterst�tzt? */
	private boolean supportCookies = false;
	/** Flag: werden Cookies gefordert? */
	private boolean askForCookies = false;
}
