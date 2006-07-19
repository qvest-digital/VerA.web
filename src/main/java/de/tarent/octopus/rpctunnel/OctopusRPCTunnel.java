/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 19.07.2006
 */
package de.tarent.octopus.rpctunnel;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.Octopus;

/** Wrapper singleton class for class <code>de.tarent.octopus.rpctunnel.RPCTunnel</code> which
 * is part of a different library and must not be available.
 * 
 * @author hendrik
 *
 */
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
		try
		{
			listener = new OctopusRPCListener(octopus, commonconfig);

			try
			{
				//Registration of the listener-implementation.
				//The class RPCTunnel may not be available so it's necessary to use reflection.
				//The call RPCTunnel.registerListener(listener, OCTOPUS_ROLE)
				//wouldn't allow to catch exeptions inside this class.
				
				Class tunnelc = Class.forName("de.tarent.octopus.rpctunnel.RPCTunnel");
				Class listc = Class.forName("de.tarent.octopus.rpctunnel.RPCListener");
				Method reg = tunnelc.getMethod("registerListener", new Class[] {listc, String.class});
				reg.invoke(this, new Object[] {listener, OCTOPUS_ROLE});
			} catch (Exception e) {
				throw new RPCTunnelUnavailableException();
			}
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
		if (!isAvailable()) 
		{
			try 
			{ //Try to create an instance of OctopusRPCTunnel
				octTunnel = new OctopusRPCTunnel(octopus, commonconfig);
			} catch (RPCTunnelUnavailableException e) {
				octTunnel = null;
				logger.warning("RPC-tunnel is unavailable.");
			}
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
	
	public static boolean isAvailable() {
		return octTunnel != null;
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