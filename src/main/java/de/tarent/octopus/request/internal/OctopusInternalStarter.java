/* $Id: OctopusInternalStarter.java,v 1.5 2007/03/07 12:17:52 christoph Exp $
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
package de.tarent.octopus.request.internal;

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
