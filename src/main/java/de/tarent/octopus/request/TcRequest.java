package de.tarent.octopus.request;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.net.PasswordAuthentication;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusTask;

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
    /** Anfragetyp XML-SOAP-Messages mit POST gesendet, mit GZIP komprimiert, mit PGP verschlüsselt */
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
    /** Flag: werden Cookies unterstützt? */
	private boolean supportCookies = false;
	/** Flag: werden Cookies gefordert? */
	private boolean askForCookies = false;
    /** The internal connection to same target module as the request object in the same octopus instance over the OctopusClient API */
    OctopusConnection octopusConnection = null;
    /** Headers object that may be given from the request **/
    private RequestHeaders headers;

    //
    // öffentliche statische Methoden
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
     * Diese Methode liefert einen sprechenden Bezeichner für einen Anfragetyp.
     * 
     * @param requestType ein Anfragetyp-Wert.
     * @return eine sprechende Bezeichnung für den Anfragetyp.
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
     * Diese Methode bestimmt, ob der übergebene Anfragetyp ein Web-Typ
     * (HTML) ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, falls der Parameter ein Web-Anfragetyp ist.
     */
    public static boolean isWebType(int requestType) {
        return (requestType & REQUEST_TYPE_WEB) == REQUEST_TYPE_WEB;
    }
    
    /**
     * Diese Methode bestimmt, ob der übergebene Anfragetyp ein SOAP-Typ ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, wenn Ja.
     */
    public static boolean isSoapType(int requestType) {
        return (requestType & REQUEST_TYPE_SOAP) == REQUEST_TYPE_SOAP;
    }
    
    /**
     * Diese Methode bestimmt, ob der übergebene Anfragetyp ein XML-RPC-Typ ist.
     * 
     * @param requestType ein Anfragetyp-Wert
     * @return true, wenn Ja.
     */
    public static boolean isXmlRpcType(int requestType) {
        return (requestType & REQUEST_TYPE_XML_RPC) == REQUEST_TYPE_XML_RPC;
    }

    /**
     * Diese Methode bestimmt, ob der übergebene Anfragetyp 
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
     * Der Konstruktor legt die übergebene Request-ID ab.
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
     * Diese Methode liefert das Flag: werden Cookies unterstützt? 
     * 
     * @return Flag: werden Cookies unterstützt?
     */
	public boolean supportCookies() {
		return supportCookies;
	}

    /**
     * Diese Methode setzt das Flag: werden Cookies unterstützt? 
     * 
     * @param b Flag: werden Cookies unterstützt?
     */
	public void setSupportCookies(boolean b) {
		supportCookies = b;
	}

	/**
	 * Diese Methode liefert das auszuführende Modul. Wenn keines oder <code>null</code>
	 * explizit gesetzt wurde, wird versucht, es aus den Parametern zu gewinnen.
	 * 
	 * @return auszuführendes Modul
	 */
    public String getModule() {
        return module != null ? module : getModuleFromParams();
    }
    
    /**
     * Diese Methode setzt das auszuführende Modul. 
     * 
     * @param mod auszuführendes Modul
     */
    public void setModule(String mod) {
        module = mod;
        // TODO: Nur bei entsprechend gesetztem Hack-Flag.
        // Etwa über statische Variable am TcRequest, die initial vom Octopus
        // gesetzt wird, oder etwas feiner direkt vor dem tatsächlichen Dispatchen,
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
	 * Diese Methode liefert den auszuführenden Task. Wenn keiner oder <code>null</code>
	 * explizit gesetzt wurde, wird versucht, ihn aus den Parametern zu gewinnen.
	 * 
	 * @return auszuführender Task
     */
    public String getTask() {
        return task != null ? task : getTaskFromParams();
    }
    
    /**
     * Diese Methode setzt den auszuführenden Task. 
     * 
     * @param tsk auszuführender Task
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
     * Liefert die ID des Requests für Logzwecke.
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Liefert einen Int Wert, der wiedergibt, ob die Anfrage als
     * WEB Request, SOAP, GZIP-SOAP, ... gekommen ist.
     * <br>Er kann über die Konstanten in TcRequestProxy aufgelößt werden.
     */
    public int getRequestType() {
        return requestType;
    }

    /**
     * Setzt einen Int Wert, der wiedergibt, ob die Anfrage als
     * WEB Request, SOAP, GZIP-SOAP, ... gekommen ist.
     * <br>Er kann über die Konstanten in TcRequestProxy aufgelößt werden.
     */
    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    //
    // Requestparameterverwaltung
    //
    /**
     * Gibt zurück ob der key im Request enthalten ist.
     * 
     * @param key
     */
    public boolean containsParam(String key) {
    	return requestParameters.containsKey(key);
    }

    /**
     * Gibt einen Wert als Object zurück
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
     * Gibt einen Wert als String zurück
     * Kurzform für getParamAsString
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
     */
    public String get(String key) {
        return getParamAsString(key);
    }

    /**
     * Gibt einen Wert als boolean zurück
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
     * Gibt einen Wert als int zurück
     *
     * @return Zahlwert oder null, wenn es kein gültiger Zahlwert ist.
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
     * Gibt einen Wert als String zurück
     *
     * @return Einen String. Wenn ein String[] gespeichert ist, wird das erste Element zurück gegeben.
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
     * Gibt einen Wert als String[] zurück
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
     * Gibt den Inhalt als String zurück
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
     * Diese Methode versucht, aus den Request-Parametern das auszuführende Modul zu
     * gewinnen.
     * 
     * @return Modul aus den Request-Parametern, sonst <code>null</code>.
     */
    protected String getModuleFromParams() {
        return (requestParameters != null) ? get(PARAM_MODULE) : null;
    }
    
    /**
     * Diese Methode versucht, aus den Request-Parametern den auszuführenden Task zu
     * gewinnen.
     * 
     * @return Task aus den Request-Parametern, sonst <code>null</code>.
     */
    protected String getTaskFromParams() {
        return (requestParameters != null) ? get(PARAM_TASK) : null;
    }

    /** 
     * Returns an OctopusTask instance to the supplied task in the current module over the OctopusClient API.
     * A call to this task uses the same session an therefore the same authentication. 
     * On the other hand, the request and content are fresh. 
     * @param taskName The name of the target task in this module
     * @return A callable task for the target task in the current module
     */
    public OctopusTask getTask(String taskName) {
        return getOctopusConnection().getTask(taskName);
    }

    /** Returns the internal connection to same target module as the request object in the same octopus instance over the OctopusClient API */
    public OctopusConnection getOctopusConnection() {
        return octopusConnection;
    }

    /** Sets the internal connection to same target module as the request object in the same octopus instance over the OctopusClient API */
    public void setOctopusConnection(final OctopusConnection newOctopusConnection) {
        this.octopusConnection = newOctopusConnection;
    }
    
    /**
     * Returns the header object. This object implements the {@link RequestHeader} interface.
     * 
     * @return header
     */
    public RequestHeaders getHeaders() {
    	return this.headers;
    }
    
    /**
     * Sets the header object. This object needs to be implement the {@link RequestHeader} interface.
     * 
     * @param header header to set
     */
    public void setHeaders(RequestHeaders headers) {
    	this.headers = headers;
    }
}
