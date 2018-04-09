/* $Id$
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
 * by Hendrik Helwich.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.rpctunnel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RPCTunnel
{
	private static Map listeners;
	private static Logger logger = Logger.getLogger(RPCTunnel.class.getName());

	// Registering default OctopusCallbackListener
	static {
		//RPCTunnel.registerListener(new OctopusCallbackListener(), DefaultRoles.ROLE_ACTIVEBPEL);
	}

	public static boolean registerListener(RPCListener listener, String role)
	{
		if (listeners == null)
			listeners = new HashMap();
		listeners.put(role, listener);
		logger.info("Listener "+role+" is registered by the RPC-tunnel.");
		return true;
	}

	public static boolean unregisterListener(RPCListener listener)
	{
		if (listeners == null)
			return false;
		else {
			boolean b = listeners.values().remove(listener);
			if (b)
				logger.info("Listener is unregistered by the RPC-tunnel.");
			return b;
		}
	}

	/**
	 *
	 * This method must have the same signature as the method
	 * <code>execute</code> in interface <code>RPCListener</code>.
	 *
	 * @param myRole
	 * @param partnerRole
	 * @param module
	 * @param task
	 * @param parameters
	 * @return
	 *     <code>null</code> if the addressee given by <code>partnerRole</code> is unknown.
	 */
	public static Map execute(String myRole, String partnerRole, String module, String task, Map parameters)
	{
		RPCListener addressee = (RPCListener) listeners.get(partnerRole);
		if (addressee == null)
			return null;
		return addressee.execute(myRole, partnerRole, module, task, parameters);
	}
}
