package de.tarent.octopus.request.servlet;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.logging.Log;
import org.apache.xmlrpc.Base64;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.soap.TcSOAPEngine;
import de.tarent.octopus.soap.TcSOAPException;
import de.tarent.octopus.xmlrpc.XmlRpcEngine;

/**
 * Diese Klasse enthält Hilfsmethoden für die Octopus-spezifische
 * Http-Request-Verarbeitung.
 *
 * @author mikel
 */
public class HttpHelper {
    private static Log logger = LogFactory.getLog(HttpHelper.class);

    //
    // * HTTP-Content-Types:
    //
    /**
     * Content-Type text/html
     */
    public static final String CONTENT_TYPE_HTML = Resources.getInstance().get("CONTENT_TYPE_HTML");
    /**
     * Content-Type text/xml
     */
    public static final String CONTENT_TYPE_XML = Resources.getInstance().get("CONTENT_TYPE_XML");
    /**
     * Content-Type application/x-gzip
     */
    public static final String CONTENT_TYPE_GZIP_SOAP = Resources.getInstance().get("CONTENT_TYPE_GZIP_SOAP");
    /**
     * Content-Type application/vnd.tarent.soap.pgp
     */
    public static final String CONTENT_TYPE_PGP_SOAP = Resources.getInstance().get("CONTENT_TYPE_PGP_SOAP");

    //
    // * HTTP-Header-Bezeichner
    //
    /**
     * Header Content-Type
     */
    public static String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * Header SOAPAction
     */
    public static String HEADER_SOAP_ACTION = "SOAPAction";

    /**
     * Expliziter Header RequestType, der angibt, von welchem Typ eine Anfrage ist
     */
    public static String HEADER_REQUEST_TYPE = "RequestType";

    /**
     * XML-RPC Wert für den Header 'RequestType'
     */
    public static String HEADER_REQUEST_TYPE_XML_RPC = "xml-rpc";
    /**
     * SOAP Wert für den Header 'RequestType'
     */
    public static String HEADER_REQUEST_TYPE_SOAP = "soap";
    /**
     * WEB Wert für den Header 'RequestType'
     */
    public static String HEADER_REQUEST_TYPE_WEB = "web";

    //
    // öffentliche statische Methoden
    //

    /**
     * @param requestType ein Anfragetyp-Wert.
     * @return eine sprechende Bezeichnung für den Anfragetyp.
     * @deprecated Use same Method in TcRequest instead
     * Diese Methode liefert einen sprechenden Bezeichner für einen Anfragetyp.
     */
    @Deprecated
    public static String getRequestTypeName(int requestType) {
        return TcRequest.getRequestTypeName(requestType);
    }

    /**
     * Diese Methode entnimmt einer HTTP-Anfrage den Octopus-Anfragetyp.
     * Das Verfahren dabei ist wie folgt:
     * <ul>
     * <li>GET-Anfragen sind <b>web</b>
     * <li>Berücksichtigung des expliziten Octopus-Http-Headers 'RequestType'
     * <li>SOAPAction-Header: <b>soap</b> mit Bestimmung der Untertypen über den mime-Type
     * <li>Default ist <b>web</b>
     * </ul>
     *
     * @param request die HTTP-Anfrage.
     * @return der zugehörige Octopus-Anfragetyp.
     */
    protected static int discoverRequestType(HttpServletRequest request) {
        String contentTypeHeader = request.getHeader(HEADER_CONTENT_TYPE);

        // SOAP-Anfragen werden nicht mit GET gestellt
        if ("GET".equals(request.getMethod()) || contentTypeHeader == null) {
            return TcRequest.REQUEST_TYPE_WEB;
        }

        String requestTypeHeader = request.getHeader(HEADER_REQUEST_TYPE);
        if (requestTypeHeader != null) {
            if (requestTypeHeader.startsWith(HEADER_REQUEST_TYPE_WEB)) {
                return TcRequest.REQUEST_TYPE_WEB;
            }

            if (requestTypeHeader.startsWith(HEADER_REQUEST_TYPE_XML_RPC)) {
                return TcRequest.REQUEST_TYPE_XML_RPC;
            }
        }

        String soapActionHeader = request.getHeader(HEADER_SOAP_ACTION);

        // Für SOAP muss der SOAPAction-Header existieren und einer von
        // bestimmten Content-Types vorliegen.
        if (soapActionHeader != null || (requestTypeHeader != null && requestTypeHeader.startsWith(HEADER_REQUEST_TYPE_SOAP))) {
            if (contentTypeHeader.startsWith(CONTENT_TYPE_GZIP_SOAP)) {
                return TcRequest.REQUEST_TYPE_GZIP_SOAP;
            }

            if (contentTypeHeader.startsWith(CONTENT_TYPE_PGP_SOAP)) {
                return TcRequest.REQUEST_TYPE_PGP_SOAP;
            }

            return TcRequest.REQUEST_TYPE_SOAP;
        }
        return TcRequest.REQUEST_TYPE_WEB;
    }

    /**
     * Diese Methode fügt den schon aus dem Inhalt des HTTP-Requests erstellten Octopus-Requests
     * Metadaten aus Header, Protokoll und URL hinzu, unter Berücksichtigung globaler Konfiguration.
     *
     * @param requests  zu erweiternde Octopus-Requests
     * @param request   HttpServletRequest, dessen Metadaten benutzt werden sollen.
     * @param requestID die Anfrage-ID
     * @param env       OctopusServlet-Umgebung (zwecks Auswertung der Konfiguration)
     */
    public static void addHttpMetaDataEx(TcRequest[] requests, HttpServletRequest request, String requestID, TcEnv env) {
        // Headerfeld 'Accept-Language' als Locale eintragen
        Locale localeValue = getHttpLanguage(request.getHeader("Accept-Language"));
        // Basic-Authentisierung oder RemoteUser eintragen
        PasswordAuthentication pwdAuth = getPasswordAuthentication(requestID, request);
        boolean skipPwdAuth = (env == null) ? false : env.getValueAsBoolean(TcEnv.KEY_OMIT_HTTPAUTH);
        // Cookie-Support ermitteln
        boolean supportCookies = request.getCookies() != null;
        // PathInfo eintragen, ggfs Modul und Task ableiten
        String module = null;
        String task = null;
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 0) {
            if (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            int slashIndex = pathInfo.indexOf('/');
            if (slashIndex < 0) {
                module = pathInfo;
                pathInfo = null;
            } else {
                module = pathInfo.substring(0, slashIndex);
                pathInfo = pathInfo.substring(slashIndex + 1);
                slashIndex = pathInfo.indexOf('/');
                if (slashIndex < 0) {
                    task = pathInfo;
                    pathInfo = null;
                } else {
                    task = pathInfo.substring(0, slashIndex);
                    pathInfo = pathInfo.substring(slashIndex + 1);
                }
            }
            if ("requestProxy".equalsIgnoreCase(module)) {
                module = null;
            }
        }

        // in allen Requests passend setzen
        for (int i = 0; i < requests.length; i++) {
            requests[i].setParam(TcRequest.PARAM_LOCALE, localeValue);
            if ((requests[i].getPasswordAuthentication() == null) && !skipPwdAuth) {
                requests[i].setPasswordAuthentication(pwdAuth);
            }
            requests[i].setSupportCookies(supportCookies);
            // adding Cookies to request
            if (supportCookies) {
                Cookie[] cookies = request.getCookies();
                Map cookiesMap = new HashMap(cookies.length);
                for (int j = 0; j < cookies.length; j++) {
                    cookiesMap.put(cookies[j].getName(), cookies[j].getValue());
                }
                requests[i].setParam(TcRequest.PARAM_COOKIES, cookiesMap);
            }

            if (pathInfo != null) {
                requests[i].setParam(TcRequest.PARAM_PATH_INFO, pathInfo);
            }
            if (requests[i].getModule() == null) {
                requests[i].setModule(module);
            }
            if (requests[i].getTask() == null) {
                requests[i].setTask(task);
            }
        }
    }

    /**
     * @param requests  zu erweiternde Octopus-Requests
     * @param request   HttpServletRequest, dessen Metadaten benutzt werden sollen.
     * @param requestID die Anfrage-ID
     * @deprecated Use addHttpMetaDataEx instead
     * Diese Methode fügt den schon aus dem Inhalt des HTTP-Requests erstellten Octopus-Requests
     * Metadaten aus Header, Protokoll und URL hinzu, ohne Berücksichtigung globaler Konfiguration.
     */
    public static void addHttpMetaData(TcRequest[] requests, HttpServletRequest request, String requestID) {
        addHttpMetaDataEx(requests, request, requestID, null);
    }

    /**
     * Diese Methode analysiert eine Web-Anfrage.
     *
     * @param request     die HTTP-Anfrage
     * @param requestType der Anfragetyp
     * @param requestID   die Anfrage-ID
     * @return ein generiertes Anfrage-Objekt
     * @throws TcSOAPException
     */
    public static TcRequest readWebRequest(HttpServletRequest request, int requestType, String requestID) throws TcSOAPException {
        Map requestParams = new HashMap();

        // Übergabeparameter eintragen
        // TODO: CharacterEncoding in etwa wie im Folgenden festlegen, allerdings konfigurierbar; testen, ob bei get, post,
        // post-multipart funktioniert.
        // if (request.getCharacterEncoding() == null)
        // 	request.setCharacterEncoding("UTF-8");
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            Object val = null;
            if (request.getParameterValues(key).length == 1) {
                val = request.getParameterValues(key)[0];
            } else {
                val = request.getParameterValues(key);
            }

            requestParams.put(key, val);

            // Map übergeben
            int bracketStart = key.indexOf("[");
            if (bracketStart > -1) {
                String mapName = key.substring(0, bracketStart);
                String mapKey = key.substring(bracketStart + 1, key.length() - 1);
                // TODO: Was, wenn unter dem Namen schon was da ist...?
                if (!(requestParams.get(mapName) instanceof Map)) {
                    requestParams.put(mapName, new HashMap());
                }
                Map map = (Map) requestParams.get(mapName);
                map.put(mapKey, val);
            }
        }

        // Headerfelder als Map eintragen
        Map header = new HashMap();
        for (Enumeration headerNames = request.getHeaderNames(); headerNames.hasMoreElements(); ) {
            String key = headerNames.nextElement().toString();
            String value = request.getHeader(key);
            header.put(key, value);
        }
        requestParams.put(TcRequest.PARAM_HEADER, header);

        // Parse multipart objects
        ServletRequestContext fileuploadRequest = new ServletRequestContext(request);
        if (FileUploadBase.isMultipartContent(fileuploadRequest)) {
            try {
                // Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();
                // Sets the size threshold beyond which files are written
                // directly to disk.
                factory.setSizeThreshold(500 * 1024);
                // Sets the directory used to temporarily store files that are larger
                // than the configured size threshold.
                factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
                ServletFileUpload upload = new ServletFileUpload(factory);

                List items = upload.parseRequest(request);
                for (Iterator it = items.iterator(); it.hasNext(); ) {
                    FileItem fileItem = (FileItem) it.next();
                    if (fileItem.getContentType() == null) {
                        requestParams.put(fileItem.getFieldName(), fileItem.getString());
                    } else {
                        Map file = new HashMap();
                        file.put(TcRequest.PARAM_FILE_CONTENT_TYPE, fileItem.getContentType());
                        file.put(TcRequest.PARAM_FILE_CONTENT_NAME, fileItem.getName());
                        file.put(TcRequest.PARAM_FILE_CONTENT_SIZE, new Long(fileItem.getSize()));
                        file.put(TcRequest.PARAM_FILE_CONTENT_STREAM, fileItem.getInputStream());
                        requestParams.put(fileItem.getFieldName(), file);
                    }
                }
            } catch (FileUploadException ex) {
                logger.warn(Resources.getInstance().get("HTTPHELPER_LOG_FILE_UPLOAD_ERROR", requestID), ex);
                throw new TcSOAPException(ex);
            } catch (IOException ex) {
                logger.warn(Resources.getInstance().get("HTTPHELPER_LOG_FILE_UPLOAD_IO_ERROR", requestID), ex);
                throw new TcSOAPException(ex);
            }
        }

        // Request-Objekt erzeugen
        TcRequest tcRequest = new TcRequest(requestID);
        tcRequest.setRequestType(requestType);
        tcRequest.setRequestParameters(requestParams);
        return tcRequest;
    }

    /**
     * Diese Methode delegiert die Analyse einer SOAP-Anfrage an die SOAPEngine.
     *
     * @param request     die HTTP-Anfrage
     * @param requestType der Anfragetyp
     * @param requestID   die Anfrage-ID
     * @param soapEngine  die zu verwendende SOAP-Engine
     * @return ein generiertes Anfrage-Objekt
     * @throws TcSOAPException
     */
    public static TcRequest[] readSoapRequests(HttpServletRequest request, int requestType, String requestID,
      TcSOAPEngine soapEngine) throws TcSOAPException {
        logger.trace(HttpHelper.class.getName() + " readSoapRequests " +
          new Object[] { request, new Integer(requestType), requestID, soapEngine });
        try {
            InputStream inStream = request.getInputStream();

            if (requestType == TcRequest.REQUEST_TYPE_PGP_SOAP) {
                inStream = TcSOAPEngine.addPGPFilterToInputStream(inStream);
            }

            // PGP_SOAP Nachrichten wurden vor dem PGP auch nochmal mit GZIP komprimiert.
            if (requestType == TcRequest.REQUEST_TYPE_GZIP_SOAP || requestType == TcRequest.REQUEST_TYPE_PGP_SOAP) {
                inStream = TcSOAPEngine.addGZIPFilterToInputStream(inStream);
            }

            return soapEngine.readSoapRequests(inStream, requestType, requestID);
        } catch (IOException e) {
            throw new TcSOAPException(e);
        }
    }

    /**
     * Diese Methode analysiert eine XML-RPC-Anfrage.
     *
     * @param request     die HTTP-Anfrage
     * @param requestType der Anfragetyp
     * @param requestID   die Anfrage-ID
     * @return ein generiertes Anfrage-Objekt
     * @throws TcSOAPException
     */
    public static TcRequest[] readXmlRpcRequests(HttpServletRequest request, int requestType, String requestID)
      throws TcSOAPException {
        logger.trace(HttpHelper.class.getName() + " readXmlRpcRequests " +
          new Object[] { request, new Integer(requestType), requestID });

        try {
            InputStream inStream = logInput(request.getInputStream(), "INFO", "HTTPHELPER_LOG_XML_RPC_INPUT");
            return XmlRpcEngine.readXmlRpcRequests(inStream, requestType, requestID);
        } catch (IOException e) {
            throw new TcSOAPException(e);
        } finally {
            logger.trace(HttpHelper.class.getName() + " readXmlRpcRequests");
        }
    }

    /**
     * @param requestType ein Anfragetyp-Wert
     * @return true, falls der Parameter ein Web-Anfragetyp ist.
     * @deprecated Use same Method in TcRequest instead
     * Diese Methode bestimmt, ob der übergebene Anfragetyp ein Web-Typ
     * (HTML) ist.
     */
    public static boolean isWebType(int requestType) {
        return TcRequest.isWebType(requestType);
    }

    /**
     * @param requestType ein Anfragetyp-Wert
     * @return true, falls der Parameter ein Web-Anfragetyp ist.
     * @deprecated Use same Method in TcRequest instead
     * Diese Methode bestimmt, ob der übergebene Anfragetyp ein SOAP-Typ ist.
     */
    public static boolean isSoapType(int requestType) {
        return TcRequest.isSoapType(requestType);
    }

    /**
     * @param requestType ein Anfragetyp-Wert
     * @return true, falls der Parameter ein Web-Anfragetyp ist.
     * @deprecated Use same Method in TcRequest instead
     * Diese Methode bestimmt, ob der übergebene Anfragetyp ein XML-RPC-Typ ist.
     */
    public static boolean isXmlRpcType(int requestType) {
        return TcRequest.isXmlRpcType(requestType);
    }

    /**
     * Liefert zu einem HTTP-Accept-Language-String
     * die entsprechende Locale mit Language und Country.
     *
     * @param acceptLanguage
     * @return userLocale oder <code>Locale.getDefault()</code>
     * @see java.util.Locale
     */
    public final static Locale getHttpLanguage(String acceptLanguage) {
        // TODO: Default-Sprache nicht durch VM, sondern durch config definieren?!
        if (acceptLanguage == null || acceptLanguage.length() == 0) {
            return Locale.getDefault();
        }
        String[] language = acceptLanguage.split("[,]");
        for (int i = 0; i < language.length; i++) {
            int d1 = language[i].indexOf('-');
            int d2 = language[i].indexOf('_');
            int d3 = language[i].indexOf(';');
            if (d2 != -1 && d1 < d2) {
                d1 = d2;
            }
            if (d3 == -1) {
                d3 = language[i].length();
            }
            if (d1 == -1) {
                return new Locale(language[i].substring(0, d3));
            } else {
                return new Locale(language[i].substring(0, d1), language[i].substring(d1 + 1, d3));
            }
        }
        return Locale.getDefault();
    }

    /**
     * Diese Methode liefert eine Passwort-Authentifizierung aus einem HttpServletRequest.
     *
     * @param request ein HttpServletRequest
     * @return eine Passwort-Authentifizierung oder <code>null</code>.
     */
    public final static PasswordAuthentication getPasswordAuthentication(String requestID, HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        if (authorization != null && authorization.startsWith("Basic ")) {
            authorization = authorization.substring("Basic ".length());
            authorization = new String(Base64.decode(authorization.getBytes()));
            String[] authParts = authorization.split("[:]", 2);
            if (authParts.length == 2) {
                logger.debug("Authorisierung aus Header übernommen: " + Arrays.asList(authParts));
                return new PasswordAuthentication(authParts[0], authParts[1].toCharArray());
            }
        }
        if (request.getRemoteUser() != null) {
            logger.info(Resources.getInstance().get("HTTPHELPER_LOG_REMOTE_USER", requestID, request.getRemoteUser()));
            return new PasswordAuthentication(request.getRemoteUser(), new char[0]);
        }
        return null;
    }

    /**
     * Diese Methode gibt je nach LogLevel-Angabe die Mitteilung in das
     * Log aus und liefert einen InputStream zurück, der wieder auslesbar ist.
     *
     * TODO: Schlechte Effizienz:
     * Sobald das Log-Level != null ist muss die gesamte Nachricht in
     * einen neuen InputStream Kopiert werden.
     * Besser wäre es, den Stream nur dann zu kopieren,
     * wenn er auch wirklich geloggt wurde.
     *
     * @param message     die Eingabe
     * @param logLevel    der Level, mit dem die Eingabe gelogt werden soll. Bei
     *                    null und "" wird nichts getan.
     * @param logResource Schlüssel zum Ressourcen-Eintrag, der als Mitteilung
     *                    geloggt wird. {0} im Eintrag wird durch die Mitteilung ersetzt.
     * @return ein wieder verwendbarer Mitteilungs-Eingabe-Strom.
     * @throws IOException
     */
    public static InputStream logInput(InputStream message, String logLevel, String logResource) throws IOException {
        if (logLevel != null && logLevel.length() > 0 && HttpHelper.isLoggable(logLevel)) {
            StringBuffer sb = new StringBuffer();
            int c;
            while (0 <= (c = message.read())) {
                sb.append((char) c);
            }
            HttpHelper.log(logLevel, Resources.getInstance().get(logResource, sb));
            message = new ByteArrayInputStream(sb.toString().getBytes());
        }
        return message;
    }

    public static boolean isLoggable(String logLevel) {
        if (logLevel == "SEVERE") {
            return logger.isErrorEnabled();
        } else if (logLevel == "WARNING") {
            return logger.isWarnEnabled();
        } else if (logLevel == "INFO") {
            return logger.isInfoEnabled();
        } else if (logLevel == "CONFIG") {
            return logger.isDebugEnabled();
        } else if (logLevel == "FINE") {
            return logger.isDebugEnabled();
        } else if (logLevel == "FINER") {
            return logger.isDebugEnabled();
        } else if (logLevel == "FINEST") {
            return logger.isTraceEnabled();
        }
        return false;
    }

    public static void log(String level, String msg) {
        if (level == "SEVERE") {
            logger.error(msg);
        } else if (level == "WARNING") {
            logger.warn(msg);
        } else if (level == "INFO") {
            logger.info(msg);
        } else if (level == "CONFIG") {
            logger.debug(msg);
        } else if (level == "FINE") {
            logger.debug(msg);
        } else if (level == "FINER") {
            logger.debug(msg);
        } else if (level == "FINEST") {
            logger.trace(msg);
        }
    }
}
