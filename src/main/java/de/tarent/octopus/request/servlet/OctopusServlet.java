/* $Id: OctopusServlet.java,v 1.4 2007/03/07 17:28:22 christoph Exp $
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

package de.tarent.octopus.request.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;

import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.soap.TcSOAPEngine;
import de.tarent.octopus.soap.TcSOAPException;
import de.tarent.octopus.request.directCall.OctopusDirectCallConnection;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.request.internal.OctopusInternalStarter;

/** 
 * Eintrittspunkt der Anfrage und Starter des Systems,
 * bei Verwendeung des Octopus in einem Servlet.
 * <br><br>
 * 1. Dieses Servlet startet das System indem es in seiner init() Methode 
 * die dauerhaft bestehenden Komponenten aufbaut und initialisiert.
 * <br><br>
 * 2. Es nimmt Anfragen entgegen und leitet diese an den RequestDispatcher weiter.
 * Dabei muss es unterscheiden, ob diese Anfragen normale WEB- oder SOAP-Anfrgen sind.
 * Beide werden dann in eine unabh�ngige Repr�sentaition gebracht.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusServlet extends HttpServlet {
	private static final long serialVersionUID = 3257852090755133745L;

	private Octopus octopus = null;
    private TcEnv env;

    private TcSOAPEngine soapEngine;
    static Log logger = LogFactory.getLog(OctopusServlet.class);

    // Fehler, der w�hrend des Initialisierens auftritt
    private Exception initError = null;


    /** Name dieser Webapp aus dem ContextPath (==ContextPath ohne '/' am Anfang).
     *  Wird beim ersten Request oder ueber die Servlet-Konfiguration gesetzt, da er nicht �ber den Servlet-Context abgerufen werden kann
     */
    protected String webappContextPathName = null;

    /**
     * Bit field with request types which are allowed for connections with this octopus servlet
     */
    protected int allowedRequestTypes = 0x00000000;
    
    /**
     * Inititalisiert die Komponenten, die f�r alle Aufrufe gleich sind.
     * <br><br>
    
     *   <li>Env Objekt durch createEnvObject()</li>
     *   <li>Dem RequestDispatcher</li>
     *   <li>Dem Logger.</li>
     * </ul>
     */
    public void init() throws ServletException {
        initError = null;
        try {
            env = createEnvObject();
                        
            System.setProperty(TcEnv.KEY_PATHS_ROOT, env.getValueAsString(TcEnv.KEY_PATHS_ROOT));
            LogFactory.initOctopusLogging(env);

            // set the webappContextPathName, if it was configured in the octopus-server config
            webappContextPathName = env.getValueAsString(TcEnv.KEY_WEBAPP_CONTEXT_PATH_NAME);
            
            String types = env.getValueAsString(TcEnv.KEY_REQUEST_ALLOWED_TYPES);
			if (types == null || types.length() == 0)
				types = TcEnv.VALUE_REQUEST_TYPE_ANY;
			
			if (types.indexOf(TcEnv.VALUE_REQUEST_TYPE_ANY) != -1)
				allowedRequestTypes = 0xffffffff;
			if (types.indexOf(TcEnv.VALUE_REQUEST_TYPE_WEB) != -1)
				allowedRequestTypes |= TcRequest.REQUEST_TYPE_WEB;
			if (types.indexOf(TcEnv.VALUE_REQUEST_TYPE_SOAP) != -1)
				allowedRequestTypes |= TcRequest.REQUEST_TYPE_SOAP;
			if (types.indexOf(TcEnv.VALUE_REQUEST_TYPE_XMLRPC) != -1)
				allowedRequestTypes |= TcRequest.REQUEST_TYPE_XML_RPC;
			if (types.indexOf(TcEnv.VALUE_REQUEST_TYPE_DIRECTCALL) != -1)
				allowedRequestTypes |= TcRequest.REQUEST_DIRECT_CALL;
            
            soapEngine = new TcSOAPEngine(env);
            octopus = new Octopus();
            octopus.init(env);
            octopus.init(new ServletModuleLookup(getServletContext(), this, octopus.getCommonConfig()));
            //Octopus f�r lokale Connections bekannt machen
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
        if (octopus != null)
            try {
                octopus.deInit();
            } catch (Exception e) {
                logger.warn(Resources.getInstance().get("REQUESTPROXY_LOG_CLEANUP_EXCEPTION"), e);
            }
        LogFactory.deInitOctopusLogging();
        
    }

    /**
     * Gibt die Anfrage an handleTheRequest weiter
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleTheRequest(request, response, false);
    }

    /**
     * Gibt die Anfrage an handleTheRequest weiter
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleTheRequest(request, response, true);
    }

    /**
     * Startet die Abarbeitung einer Anfrage dadurch, da� diese an den die dispatch() Methode des RequestDispatchers weiter gegeben wird.
     * <br><br>
     * Dazu werden ein TcRequest Objekt mit createRequestObject(),
     * <br>ein TcResponse Objekt,
     * <br>sowie ein TcSession mit der HttpSession initialisiert und an den RequestDispatcher �bergeben.
     * <br><br>
     * Wenn der Request den Parameter debug=true enth�lt und Debugging �ber den DeploymentDescriptor erlaubt wurde,
     * werden noch Debugausgaben aus gegeben.
     */
    public void handleTheRequest(HttpServletRequest request, HttpServletResponse response, boolean post) throws IOException {

        if (webappContextPathName == null && request.getContextPath().length() >=1 )
            webappContextPathName = request.getContextPath().substring(1);

        String requestID = TcRequest.createRequestID();
        {
            String reqString = request.getPathInfo();
            if (reqString == null)
                reqString = "";
            String params = request.getQueryString();
            if (params != null)
                reqString += '?' + params;
            logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_URI", requestID, request.getRemoteAddr(), reqString));
        }


        TcSession tcSession;
        if (!env.getValueAsBoolean(TcEnv.KEY_OMIT_SESSIONS)) {
            HttpSession session = request.getSession(true);

            // Anmeldung erzwingen wen task = login
            boolean sessionWasNew = (session == null) || session.isNew();

            if ("login".equals(request.getParameter("task"))) {
                // TODO: eigene Sessiondaten entnehmen, aber valid lassen
            	if (session != null)
            		session.invalidate();
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
                        if (value instanceof String)
                            queryString += key + '=' + value + '&';
                        else if (value instanceof String[]) {
                            String[] values = (String[]) value;
                            for (int i = 0; i < values.length; i++)
                                queryString += key + '=' + values[i] + '&';
                        }
                    }
                }
                
                // This relativ (manual) redirect fix a problem in the catalina
                // implementation of HttpServletResponse.sendRedirect which
                // use a wrong ("http://") absolut path for "https:/Immobilien Schulz/" urls.
                // It also fix a problem with forwarder-servlets which do not
                // include the orignal request url.
                String redirectURI = (String)request.getAttribute("javax.servlet.forward.request_uri");
                if (redirectURI == null)
                	redirectURI = request.getRequestURI();
                if (queryString.length() != 0)
                	redirectURI = redirectURI + '?' + queryString;
                redirectURI = response.encodeRedirectURL(redirectURI);
                
                response.setStatus(302);
                response.setHeader("Location", redirectURI);
                logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_REDIRECT_REQUEST", requestID, redirectURI));
                return;
            }
            tcSession = new TcServletSession(session);
        }
        else {//Servlet Session Ignorieren und Dummy erzeugen
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
            logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_TYPE", requestID, TcRequest.getRequestTypeName(requestType)));

            // test if request type is not allowed 
            if (! isRequstTypeAllowed(requestType) ){
            	logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_ILLEGAL_REQUEST_TYPE", new String [] { requestID, String.valueOf(requestType), String.valueOf(allowedRequestTypes),  }), null);
            	tcResponse.sendError(requestType, 
                                     requestID, 
                                     Resources.getInstance().get("ERROR_MESSAGE_ILLEGAL_REQUEST_TYPE"), 
                                     new Exception(Resources.getInstance().get("ERROR_MESSAGE_ILLEGAL_REQUEST_TYPE")));
            	return;	
        	}
            
            if (initError != null) {
                logger.warn(Resources.getInstance().get("REQUESTPROXY_LOG_INITERROR_STOP", requestID), initError);
                tcResponse.sendError(requestType, requestID, Resources.getInstance().get("REQUESTPROXY_OUT_INITERROR_STOP"), initError);
                return;
            }

            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_SESSION_OBJECT_CREATED", requestID));

            TcRequest[] octRequests = extractRequests(request, requestType, requestID);
            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_OBJECT_CREATED", requestID));
            
            for (int i = 0; i < octRequests.length; i++) {
                TcRequest octRequest = octRequests[i];
                String askForCookies = octRequest.getParamAsString(TcRequest.PARAM_ASK_FOR_COOKIES);
                octRequest.setAskForCookies(askForCookies == null ? false : askForCookies.equalsIgnoreCase("true"));

                octRequest.setParam(TcRequest.PARAM_ENCODED_URL, response.encodeURL(request.getRequestURL().toString()));
                octRequest.setParam(TcRequest.PARAM_SESSION_ID, tcSession.getId());
                octRequest.setOctopusConnection(createOctopusConnection(octRequest, tcSession));

                octopus.dispatch(octRequest, tcResponse, tcSession);

                // Debug Messages wurden seit Octopus 1.2.0 entfernt: F�r Debugging bitte Logging-Api verwenden!
                if (octRequest.getParameterAsBoolean(TcRequest.PARAM_DEBUG))
                    tcResponse.sendError(requestType,
                                         octRequest.getRequestID(),
                                         Resources.getInstance().get("REQUEST_UNKNOWN_REQUEST_PARAM", TcRequest.PARAM_DEBUG, 
                                                                     "Direkte Debug Messages wurden seit Octopus 1.2.0 entfernt: F�r Debugging bitte Logging-Api verwenden!"),
                                         null);
            }
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION", requestID), e);
            if (tcResponse != null)
                tcResponse.sendError(requestType, requestID, Resources.getInstance().get("REQUESTPROXY_OUT_PROCESSING_EXCEPTION"), e);
        }
        if (tcResponse != null)
            tcResponse.flush();
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
     * Diese Methode extrahiert aus einer Http-Anfrage die zugeh�rigen
     * Octopus-Anfragen.<br>
     * TODO: genauer testen und ggfs eine Fehlerr�ckgabe
     *  
     * @param request HTTP-Anfrage
     * @param requestType Anfragetyp
     * @param requestID ID der Gesamtanfrage
     * @return Array von Octopus-Anfragen
     * @throws TcSOAPException
     */
    private TcRequest[] extractRequests(HttpServletRequest request, int requestType, String requestID) throws TcSOAPException {
        TcRequest[] requests = null;
        if (TcRequest.isWebType(requestType))
            // Normaler WEB-Request
            requests = new TcRequest[] {HttpHelper.readWebRequest(request, requestType, requestID)};
        else if (TcRequest.isSoapType(requestType))
            // SOAP Request
            requests = HttpHelper.readSoapRequests(request, requestType, requestID, soapEngine);
        else if (TcRequest.isXmlRpcType(requestType))
            // XML-RPC Request
            requests = HttpHelper.readXmlRpcRequests(request, requestType, requestID);
        else 
            return null;
        HttpHelper.addHttpMetaData(requests, request, requestID);
        return requests;
    }

    /**
     * Erstellt ein TcEnv aus den Konfigurationsparametern Servlets
     * <br><br>Die Informationen aus dem ServletContext werden mit dem prefix 'servletContext.' abgelegt.
     * <br>Die Parameter aus dem Deployment Descriptor werden so �bernommen, wie sie sind und k�nnen die
     * des ServletContextes gegebenenfalls �berschreiben.
     */
    protected TcEnv createEnvObject() {
        // Env Objekt mit Umgebungsvariablen und Einstellungsparametern
        TcEnv env = new TcEnv();

        // Default-Module auf den ContextPath setzen. 
        // Dies kann an sp�terer Stelle �berschrieben werden.
        //env.setValue(TcEnv.KEY_DEFAULT_MODULE, getServletContext().getContextPath());

        ServletConfig config = getServletConfig();
        for (Enumeration e = config.getInitParameterNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            String val = config.getInitParameter(key);
            if (val != null)
                env.setValue(key, val);
        }

        ServletContext context = getServletContext();
        for (Enumeration e = context.getAttributeNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            env.setValue("servletContext", name, "" + context.getAttribute(name));
        }

        env.setValue(TcEnv.KEY_PATHS_ROOT, context.getRealPath("/WEB-INF") + System.getProperty("file.separator"));

        env.overrideValues("base", "/de/tarent/octopus/overrides");
        
        return env;
    }
}
