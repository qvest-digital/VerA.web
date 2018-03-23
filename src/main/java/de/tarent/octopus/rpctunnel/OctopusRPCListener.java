package de.tarent.octopus.rpctunnel;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
