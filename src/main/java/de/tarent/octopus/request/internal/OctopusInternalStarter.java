package de.tarent.octopus.request.internal;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcSession;
import de.tarent.octopus.request.directCall.OctopusDirectCallResult;
import de.tarent.octopus.request.directCall.TcDirectCallException;
import de.tarent.octopus.request.directCall.TcDirectCallResponse;
import de.tarent.octopus.request.directCall.TcDirectCallSession;
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
