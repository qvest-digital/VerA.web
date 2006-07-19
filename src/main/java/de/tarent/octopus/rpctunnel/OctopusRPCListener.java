package de.tarent.octopus.rpctunnel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.Octopus;


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