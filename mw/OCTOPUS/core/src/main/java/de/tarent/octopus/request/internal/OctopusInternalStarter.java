package de.tarent.octopus.request.internal;
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.request.directcall.OctopusDirectCallResult;
import de.tarent.octopus.request.directcall.TcDirectCallException;
import de.tarent.octopus.request.directcall.TcDirectCallResponse;
import de.tarent.octopus.request.directcall.TcDirectCallSession;
import de.tarent.octopus.resource.Resources;

/**
 * Kapselt das Ansprechen des Octopus
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusInternalStarter implements OctopusStarter {

    private static Log logger = LogFactory.getLog(OctopusStarter.class);

    TcSession tcSession;

    Octopus octopus;

    /**
     * Creates an OctopusStarter with a new dummy Session
     */
    public OctopusInternalStarter(Octopus octopus) {
        this.octopus = octopus;
        tcSession = new TcDirectCallSession();
    }

    /**
     * Creates an OctopusStarter with the supplied session
     */
    public OctopusInternalStarter(Octopus octopus, TcSession session) {
        this.octopus = octopus;
        this.tcSession = session;
    }

    /**
     * Startet die Abarbeitung einer Anfrage
     */
    public OctopusDirectCallResult request(Map requestParams)
        throws TcDirectCallException {

        logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_PROCESSING_START"));

        try {
            TcDirectCallResponse response = new TcDirectCallResponse();
            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_RESPONSE_OBJECT_CREATED"));
            //response.setSoapEngine(soapEngine);

            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_SESSION_OBJECT_CREATED"));

            TcRequest request = new TcRequest();
            request.setRequestParameters(requestParams);
            request.setParam(TcRequest.PARAM_SESSION_ID, tcSession.getId());
            logger.trace(Resources.getInstance().get("REQUESTPROXY_LOG_REQUEST_OBJECT_CREATED"));

            octopus.dispatch(request, response, tcSession);

            response.flush();
            return new OctopusDirectCallResult(response);
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION"), e);
            throw new TcDirectCallException(Resources.getInstance().get("REQUESTPROXY_LOG_PROCESSING_EXCEPTION"), e);
        }

    }
}
