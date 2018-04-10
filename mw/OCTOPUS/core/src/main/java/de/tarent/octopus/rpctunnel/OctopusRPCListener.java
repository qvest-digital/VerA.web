package de.tarent.octopus.rpctunnel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.request.Octopus;

class OctopusRPCListener implements RPCListener
{
    private static final String CONNECTION_NAME = "octopus";

    public OctopusRPCListener(Octopus octopus, TcCommonConfig commonconfig)
    {
    }

	public Map execute(String myRole, String partnerRole, String module, String task, Map parameters)
	{
		OctopusConnectionFactory ocConnectionFactory = OctopusConnectionFactory.getInstance();

		Map configuration = new HashMap();
		configuration.put(OctopusConnectionFactory.CONNECTION_TYPE_KEY, OctopusConnectionFactory.CONNECTION_TYPE_INTERNAL);
		configuration.put(OctopusConnectionFactory.MODULE_KEY, module);
		ocConnectionFactory.setConfiguration(CONNECTION_NAME, configuration);
		OctopusConnection ocConnection = ocConnectionFactory.getConnection(CONNECTION_NAME);

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
