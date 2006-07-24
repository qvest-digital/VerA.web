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
    private static final String CONNECTION_NAME = "octopus";
    
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