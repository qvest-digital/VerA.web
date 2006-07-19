/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 18.07.2006
 */

package de.tarent.octopus.rpctunnel;

import java.util.HashMap;
import java.util.Map;

public class RPCTunnel
{
	private static Map listeners;

	public static boolean registerListener(RPCListener listener, String role)
	{
		if (listeners == null)
			listeners = new HashMap();
		listeners.put(role, listener);
		return true;
	}

	public static boolean unregisterListener(RPCListener listener)
	{
		return listeners == null ? false :
			listeners.values().remove(listener);
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