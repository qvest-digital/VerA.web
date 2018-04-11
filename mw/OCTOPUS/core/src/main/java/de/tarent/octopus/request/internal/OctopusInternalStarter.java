package de.tarent.octopus.request.internal;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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
