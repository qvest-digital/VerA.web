/* $Id: OctopusCallbackListener.java,v 1.1 2006/07/24 14:59:57 jens Exp $
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
 * interest in the program 'tarent-contact'
 * (which makes passes at compilers) written
 * by Jens Neumaier. 
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.rpctunnel;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.engine.AeMessageQueue;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.activebpel.wsio.receive.AeRequestException;

/**
 * @author Jens Neumaier, tarent GmbH
 *
 */
public class OctopusCallbackListener implements RPCListener {
	
	private static Logger logger = Logger.getLogger(OctopusCallbackListener.class.getName());
	
	public Map execute(String myRole, String partnerRole, String module, String task, Map parameters) {
		AeWebServiceMessageData messageData = new WebServiceMessageData(parameters);
//		messageData.setName(new QName(module));

		AeMessageContext context = new AeMessageContext();
	    context.setServiceName(module);
	    context.setOperation(task);
		
	    IAeWebServiceResponse response = null;
	    try {
			response = AeMessageQueue.getInstance().queueReceiveData(messageData, context);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AeRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response.getMessageData().getMessageData();
	}
}
