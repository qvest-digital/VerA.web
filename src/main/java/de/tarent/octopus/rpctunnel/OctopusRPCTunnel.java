/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 19.07.2006
 */
package de.tarent.octopus.rpctunnel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.Octopus;

public class OctopusRPCTunnel 
{
    private TcCommonConfig octopusConfig = null;
    private Octopus octopus = null;
	private OctopusRPCListener listener = null;
	
    public static final String OCTOPUS_ROLE = "octopus";
	
	private static OctopusRPCTunnel octTunnel = null;
	
    private static Logger logger = Logger.getLogger(OctopusRPCTunnel.class.getName());
	
	private OctopusRPCTunnel(Octopus octopus, TcCommonConfig commonconfig) throws RPCTunnelUnavailableException
	{
        octopusConfig = commonconfig;
        this.octopus = octopus;
		listener = new OctopusRPCListener(octopus, commonconfig);
		try { //The class RPCTunnel may not be available
			RPCTunnel.registerListener(listener, OCTOPUS_ROLE);
		} catch (NoClassDefFoundError e) {
			throw new RPCTunnelUnavailableException();
		}
	}
	
	/**
	 * If class RPCTunnel is not in the classpath or this method was called before
	 * then nothing is done. 
	 * 
	 * @param octopus
	 * @param commonconfig
	 */
	public static void createInstance(Octopus octopus, TcCommonConfig commonconfig)
	{
		if (octTunnel != null) //Class is already constructed
			return;
		try { //Try to create an instance of OctopusRPCTunnel
			octTunnel = new OctopusRPCTunnel(octopus, commonconfig);
		} catch (RPCTunnelUnavailableException e) {
			octTunnel = null;
			logger.warning("RPC-Tunnel is unavailable.");
		}
	}
	
	public static OctopusRPCTunnel getInstance()
	{
		return octTunnel;
	}

	public Map execute(String myRole, String partnerRole, String module, String task, Map parameters)
	{
		//At this point it is ensured that class RPCTunnel is in the classpath
		return RPCTunnel.execute(myRole, partnerRole, module, task, parameters);
	}
}

class OctopusRPCListener implements RPCListener
{
    private TcCommonConfig octopusConfig = null;
    private Octopus octopus = null;
    
    private static Logger logger = Logger.getLogger(OctopusRPCListener.class.getName());

    public OctopusRPCListener(Octopus octopus, TcCommonConfig commonconfig)
    {
        this.octopusConfig = commonconfig;
        this.octopus = octopus;
    }

	public Map execute(String myRole, String partnerRole, String module, String task, Map parameters)
	{
		OctopusConnectionFactory ocConnectionFactory = OctopusConnectionFactory.getInstance();
		Map configuration = new HashMap();
		configuration.put(OctopusConnectionFactory.CONNECTION_TYPE_KEY, OctopusConnectionFactory.CONNECTION_TYPE_DIRECT_CALL);
		configuration.put(OctopusConnectionFactory.MODULE_KEY, module);
		// weitere Parameter in der konkreten Verbindungsklasse (z.B. OctopusRemoteConnection)
		ocConnectionFactory.setConfiguration("octopus", configuration);
		OctopusConnection ocConnection = ocConnectionFactory.getConnection(module);
		
		//OctopusTask ocTask = ocConnection.getTask(task);
		//OctopusResult ocResult = ocTask.add("paramName", "value").invoke();
		
		OctopusResult ocResult = ocConnection.callTask(task, parameters);
		Iterator iter = ocResult.getDataKeys();
		
		//Create result map
		Map data = new HashMap();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			data.put(key, ocResult.getData(key));
		}
		return data;
	}
}

class RPCTunnelUnavailableException extends Exception 
{
	public RPCTunnelUnavailableException() 
	{
		super();
	}

	public RPCTunnelUnavailableException(String message) 
	{
		super(message);
	}

	public RPCTunnelUnavailableException(Throwable cause) 
	{
		super(cause);
	}

	public RPCTunnelUnavailableException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}