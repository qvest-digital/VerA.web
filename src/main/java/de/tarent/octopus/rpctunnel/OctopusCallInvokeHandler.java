/* $Id: OctopusCallInvokeHandler.java,v 1.1 2006/07/24 14:59:57 jens Exp $
 *
 * tarent-contact, Plattform-Independent Webservice-Based Contactmanagement
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
 * by Jens Neumaier. 
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.rpctunnel;

import java.util.Map;
import java.util.logging.Logger;

import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 * @author Jens Neumaier, tarent GmbH
 *
 */
public class OctopusCallInvokeHandler implements IAeInvokeHandler {
	
	private static Logger logger = Logger.getLogger(OctopusCallInvokeHandler.class.getName());
	
	public IAeWebServiceResponse handleInvoke( IAeInvoke invokeRequest, String queryData ) {
		String partnerEndpointXML = invokeRequest.getPartnerEndpointReferenceString();
		
		if (partnerEndpointXML.indexOf("octopus://") < 0)
			return null;
		
		int indexFromOctopusModule = partnerEndpointXML.indexOf("octopus://")+10;
		int indexToOctopusModule = partnerEndpointXML.indexOf("<", indexFromOctopusModule);
		String octopusModule = partnerEndpointXML.substring(indexFromOctopusModule, indexToOctopusModule);
		
		String octopusTask = invokeRequest.getOperation();
		
		Map octupusParameter = invokeRequest.getInputMessageData().getMessageData();
		// Extra query information may be delivered in the customInvokerUri as additional 
		// parameters encoded in the URI as in HTTP-GET.
		// To use this feature you have to modify the module parsing and parse the URIEncoding.
		
		Map octopusResponse = RPCTunnel.execute(DefaultRoles.ROLE_ACTIVEBPEL, DefaultRoles.ROLE_OCTOPUS, octopusModule, octopusTask, octupusParameter);
		
		AeWebServiceMessageData messageData = new AeWebServiceMessageData(invokeRequest.getPortType(), octopusResponse);
		AeInvokeResponse response = new AeInvokeResponse();
		response.setMessageData(messageData);
		
		return response;
	}
}
