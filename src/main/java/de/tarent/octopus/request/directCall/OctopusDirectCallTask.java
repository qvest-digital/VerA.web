/* $Id: OctopusDirectCallTask.java,v 1.2 2006/08/09 15:39:56 christoph Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
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
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.request.directCall;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.tarent.octopus.client.OctopusCallException;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;

/** 
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallTask implements OctopusTask {
    
    OctopusStarter octopusStarter;
    String moduleName;
    Map params;

    public OctopusDirectCallTask(OctopusStarter octopusStarter) {
        this.octopusStarter = octopusStarter;
        params = new HashMap();
    }    

    public OctopusTask add(String paramName, Object paramValue) {
    	Object param = params.get(paramName);
    	if (param == null) {
    		params.put(paramName, paramValue);
    	} else {
    		if (param instanceof List) {
    			((List)param).addLast(paramValue);
    		} else {
    			List list = new List();
    			list.add(param);
    			list.addLast(paramValue);
    			params.put(paramName, list);
    		}
    	}
        return this;
    }

    public OctopusResult invoke() 
        throws OctopusCallException {

        OctopusDirectCallResult res = null;
        try {
            res = octopusStarter.request(params);
        } catch (Exception e) {
            throw new OctopusCallException("Error while calling octopus directly", e);
        }
        if (res.errorWhileProcessing())
            throw new OctopusCallException(res.getErrorMessage(), res.getErrorException());
        return res;
    }

	/* (non-Javadoc)
	 * @see de.tarent.octopus.client.OctopusTask#setConnectionTracking(boolean)
	 */
	public void setConnectionTracking(boolean contrack) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.tarent.octopus.client.OctopusTask#isConnectionTracing()
	 */
	public boolean isConnectionTracking() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * This is just a marker extension of {@link LinkedList} to tag internal
	 * parameters with more than one value.
	 */
	private static class List extends LinkedList {
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;
		// nothing more than a marker
	}
}
