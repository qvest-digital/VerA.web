package de.tarent.octopus.request.servlet;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.request.directcall.OctopusDirectCallConnection;
import de.tarent.octopus.request.internal.OctopusInternalStarter;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.soap.TcSOAPEngine;
import de.tarent.octopus.soap.TcSOAPException;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Eintrittspunkt der Anfrage und Starter des Systems,
 * bei Verwendung des Octopus in einem Servlet.
 *
 * 1. Dieses Servlet startet das System indem es in seiner init() Methode
 * die dauerhaft bestehenden Komponenten aufbaut und initialisiert.
 *
 * 2. Es nimmt Anfragen entgegen und leitet diese an den RequestDispatcher weiter.
 * Dabei muss es unterscheiden, ob diese Anfragen normale WEB- oder SOAP-Anfrgen sind.
 * Beide werden dann in eine unabhängige Repräsentaition gebracht.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
@Log4j2
public class OctopusServlet extends HttpServlet {
    private static final long serialVersionUID = 35776100380066984L;

    private static Octopus octopus = null;
    private static TcEnv env;

    private static TcSOAPEngine soapEngine;

    // Fehler, der während des Initialisierens auftritt
    private static Exception initError = null;

    /**
     * Name dieser Webapp aus dem ContextPath (==ContextPath ohne '/' am Anfang).
     * Wird beim ersten Request oder ueber die Servlet-Konfiguration gesetzt, da er nicht über den Servlet-Context
     * abgerufen werden kann
     */
    protected static String webappContextPathName = null;

    /**
     * Bit field with request types which are allowed for connections with this octopus servlet
     */
    protected static int allowedRequestTypes = 0x00000000;

    /**
     * Inititalisiert die Komponenten, die für alle Aufrufe gleich sind.
     *
     * <ul></ul>
     * <li>Env Objekt durch createEnvObject()</li>
     * <li>Dem RequestDispatcher</li>
     * <li>Dem Logger.</li>
     * </ul>
     */
    public void init() throws ServletException {
        initError = null;
        try {
            env = createEnvObject();

            System.setProperty(TcEnv.KEY_PATHS_ROOT, env.getValueAsString(TcEnv.KEY_PATHS_ROOT));

            // set the webappContextPathName, if it was configured in the octopus-server config
            webappContextPathName = env.getValueAsString(TcEnv.KEY_WEBAPP_CONTEXT_PATH_NAME);

            String types = env.getValueAsString(TcEnv.KEY_REQUEST_ALLOWED_TYPES);
            if (types == null || types.length() == 0) {
                types = TcEnv.VALUE_REQUEST_TYPE_ANY;
            }

            if (types.contains(TcEnv.VALUE_REQUEST_TYPE_ANY)) {
                allowedRequestTypes = 0xFFFFFFFF;
            }
            if (types.contains(TcEnv.VALUE_REQUEST_TYPE_WEB)) {
                allowedRequestTypes |= TcRequest.REQUEST_TYPE_WEB;
            }
            if (types.contains(TcEnv.VALUE_REQUEST_TYPE_SOAP)) {
                allowedRequestTypes |= TcRequest.REQUEST_TYPE_SOAP;
            }
            if (types.contains(TcEnv.VALUE_REQUEST_TYPE_XMLRPC)) {
                allowedRequestTypes |= TcRequest.REQUEST_TYPE_XML_RPC;
            }
            if (types.contains(TcEnv.VALUE_REQUEST_TYPE_DIRECTCALL)) {
                allowedRequestTypes |= TcRequest.REQUEST_DIRECT_CALL;
            }

            soapEngine = new TcSOAPEngine(env);
            octopus = new Octopus();
            octopus.init(env);
            octopus.init(new ServletModuleLookup(getServletContext(), this, octopus.getCommonConfig()));
            // Octopus für lokale Connections bekannt machen
            OctopusConnectionFactory.getInstance().setInternalOctopusInstance(octopus);
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_INIT_EXCEPTION"), e);
            initError = e;
        }

        logger.trace("TcRequextProxy init");
    }

    /**
     * Deinitialisiert das Servlet.
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        if (octopus != null) {
            try {
                octopus.deInit();
            } catch (Exception e) {
                logger.warn(Resources.getInstance().get("REQUESTPROXY_LOG_CLEANUP_EXCEPTION"), e);
            }
        }
    }

    /**
     * Gibt die Anfrage an handleTheRequest weiter
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
        handleTheRequest(request, response, false);
    }

    /**
     * Gibt die Anfrage an handleTheRequest weiter
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
        handleTheRequest(request, response, true);
    }

    /**
     * Startet die Abarbeitung einer Anfrage dadurch, daß diese an den die dispatch() Methode des RequestDispatchers
     * weiter gegeben wird.
     *
     * Dazu werden ein TcRequest Objekt mit createRequestObject(),
     * ein TcResponse Objekt,
     * sowie ein TcSession mit der HttpSession initialisiert und an den RequestDispatcher übergeben.
     *
     * Wenn der Request den Parameter debug=true enthält und Debugging über den DeploymentDescriptor erlaubt wurde,
     * werden noch Debugausgaben aus gegeben.
     */
    public void handleTheRequest(HttpServletRequest request, HttpServletResponse response, boolean post)
      throws IOException {
        if (webappContextPathName == null && request.getContextPath().length() >= 1) {
            webappContextPathName = request.getContextPath().substring(1);
        }

        String requestID = TcRequest.createRequestID();
        {
            String reqString = request.getPathInfo();
            if (reqString == null) {
                reqString = "";
            }
            String params = request.getQueryString();
            if (params != null) {
                reqString += '?' + params;
            }
            logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_URI",
              requestID, request.getRemoteAddr(), reqString));
        }

        TcSession tcSession;
        if (!env.getValueAsBoolean(TcEnv.KEY_OMIT_SESSIONS)) {
            HttpSession session = request.getSession(true);

            // Anmeldung erzwingen wenn task == login
            boolean sessionWasNew = (session == null) || session.isNew();

            if ("login".equals(request.getParameter("task"))) {
                // TODO: eigene Sessiondaten entnehmen, aber valid lassen
                if (session != null) {
                    session.invalidate();
                }
                session = request.getSession(true);
            }

            if (sessionWasNew && !post) {
                String queryString = request.getQueryString();
                if (queryString == null) {
                    queryString = "";
                    Iterator params = request.getParameterMap().entrySet().iterator();
                    while (params.hasNext()) {
                        Map.Entry param = (Map.Entry) params.next();
                        String key = param.getKey().toString();
                        Object value = param.getValue();
                        if (value instanceof String) {
                            queryString += key + '=' + value + '&';
                        } else if (value instanceof String[]) {
                            String[] values = (String[]) value;
                            for (int i = 0; i < values.length; i++) {
                                queryString += key + '=' + values[i] + '&';
                            }
                        }
                    }
                }

                // This relative (manual) redirect fixes a problem in the catalina
                // implementation of HttpServletResponse.sendRedirect which
                // uses a wrong ("http://") absolute path for "https:/Immobilien Schulz/" URLs.
                // It also fix a problem with forwarder servlets that do not include the orignal request URL.
                String redirectURI = (String) request.getAttribute("javax.servlet.forward.request_uri");
                if (redirectURI == null) {
                    redirectURI = request.getRequestURI();
                }
                if (queryString.length() != 0) {
                    redirectURI = redirectURI + '?' + queryString;
                }
                redirectURI = response.encodeRedirectURL(redirectURI);

                response.setStatus(302);
                response.setHeader("Location", redirectURI);
                logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_REDIRECT_REQUEST",
                  requestID, redirectURI));
                return;
            }
            tcSession = new TcServletSession(session);
        } else {
            // Servlet-Session ignorieren und Dummy erzeugen
            tcSession = new TcServletDummySession();
        }

        logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_PROCESSING_START", requestID));

        TcResponse tcResponse = null;
        int requestType = TcRequest.REQUEST_TYPE_WEB;
        try {
            tcResponse = new TcServletResponse(response);
            tcResponse.setSoapEngine(soapEngine);
            tcResponse.setErrorLevel(env.getValueAsString(TcEnv.KEY_RESPONSE_ERROR_LEVEL));
            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_RESPONSE_OBJECT_CREATED", requestID));

            requestType = HttpHelper.discoverRequestType(request);
            logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_TYPE", requestID,
              TcRequest.getRequestTypeName(requestType)));

            // test if request type is not allowed
            if (!isRequstTypeAllowed(requestType)) {
                logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_ILLEGAL_REQUEST_TYPE",
                  new String[] {
                    requestID,
                    String.valueOf(requestType),
                    String.valueOf(allowedRequestTypes),
                  }));
                tcResponse.sendError(requestType,
                  requestID,
                  Resources.getInstance().get("ERROR_MESSAGE_ILLEGAL_REQUEST_TYPE"),
                  new Exception(Resources.getInstance().get("ERROR_MESSAGE_ILLEGAL_REQUEST_TYPE")));
                return;
            }

            if (initError != null) {
                logger.warn(Resources.getInstance().get("REQUESTPROXY_LOG_INITERROR_STOP", requestID),
                  initError);
                tcResponse.sendError(requestType, requestID,
                  Resources.getInstance().get("REQUESTPROXY_OUT_INITERROR_STOP"), initError);
                return;
            }

            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_SESSION_OBJECT_CREATED", requestID));

            TcRequest[] octRequests = extractRequests(request, requestType, requestID);
            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_OBJECT_CREATED", requestID));

            for (int i = 0; i < octRequests.length; i++) {
                TcRequest octRequest = octRequests[i];
                String askForCookies = octRequest.getParamAsString(TcRequest.PARAM_ASK_FOR_COOKIES);
                octRequest.setAskForCookies(askForCookies != null && askForCookies.equalsIgnoreCase("true"));

                octRequest.setParam(TcRequest.PARAM_ENCODED_URL,
                  response.encodeURL(request.getRequestURL().toString()));
                octRequest.setParam(TcRequest.PARAM_SESSION_ID, tcSession.getId());
                octRequest.setOctopusConnection(createOctopusConnection(octRequest, tcSession));

                octopus.dispatch(octRequest, tcResponse, tcSession);
            }
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION", requestID), e);
            if (tcResponse != null) {
                tcResponse.sendError(requestType, requestID,
                  Resources.getInstance().get("REQUESTPROXY_OUT_PROCESSING_EXCEPTION"), e);
            }
        }
        if (tcResponse != null) {
            tcResponse.flush();
        }
    }

    protected boolean isRequstTypeAllowed(int requestType) {
        return (allowedRequestTypes & requestType) == requestType;
    }

    /**
     * Creates an internal OctopusConnection to the same target module as in the request with the same session.
     */
    protected OctopusConnection createOctopusConnection(TcRequest request, TcSession session) {
        OctopusDirectCallConnection con = new OctopusDirectCallConnection();
        con.setModuleName(request.getModule());
        con.setOctopusStarter(new OctopusInternalStarter(octopus, session));
        return con;
    }

    /**
     * Diese Methode extrahiert aus einer Http-Anfrage die zugehörigen
     * Octopus-Anfragen.<br>
     * TODO: genauer testen und ggfs eine Fehlerrückgabe
     *
     * @param request     HTTP-Anfrage
     * @param requestType Anfragetyp
     * @param requestID   ID der Gesamtanfrage
     * @return Array von Octopus-Anfragen
     */
    private TcRequest[] extractRequests(HttpServletRequest request, int requestType, String requestID)
      throws TcSOAPException {
        TcRequest[] requests;
        if (TcRequest.isWebType(requestType)) {
            // Normaler WEB-Request
            requests = new TcRequest[] { HttpHelper.readWebRequest(request, requestType, requestID) };
        } else if (TcRequest.isSoapType(requestType)) {
            // SOAP Request
            requests = HttpHelper.readSoapRequests(request, requestType, requestID, soapEngine);
        } else if (TcRequest.isXmlRpcType(requestType)) {
            // XML-RPC Request
            requests = HttpHelper.readXmlRpcRequests(request, requestType, requestID);
        } else {
            return null;
        }
        HttpHelper.addHttpMetaDataEx(requests, request, requestID, env);
        return requests;
    }

    /**
     * Erstellt ein TcEnv aus den Konfigurationsparametern Servlets
     *
     * Die Informationen aus dem ServletContext werden mit dem prefix 'servletContext.' abgelegt.
     * Die Parameter aus dem Deployment Descriptor werden so übernommen, wie sie sind und können die
     * des ServletContextes gegebenenfalls überschreiben.
     */
    protected TcEnv createEnvObject() {
        // Env Objekt mit Umgebungsvariablen und Einstellungsparametern
        TcEnv env = new TcEnv();

        // Default-Module auf den ContextPath setzen.
        // Dies kann an späterer Stelle überschrieben werden.
        //env.setValue(TcEnv.KEY_DEFAULT_MODULE, getServletContext().getContextPath());

        ServletConfig config = getServletConfig();
        for (Enumeration e = config.getInitParameterNames(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String val = config.getInitParameter(key);
            if (val != null) {
                env.setValue(key, val);
            }
        }

        ServletContext context = getServletContext();
        for (Enumeration e = context.getAttributeNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            env.setValue("servletContext", name, "" + context.getAttribute(name));
        }

        env.setValue(TcEnv.KEY_PATHS_ROOT,
          context.getRealPath("/WEB-INF") + System.getProperty("file.separator"));

        return env;
    }
}
