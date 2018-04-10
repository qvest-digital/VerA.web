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
