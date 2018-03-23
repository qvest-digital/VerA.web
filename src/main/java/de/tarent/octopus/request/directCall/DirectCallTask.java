
/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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
package de.tarent.octopus.request.directCall;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.tarent.octopus.client.OctopusCallException;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.request.internal.OctopusStarter;

/**
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class DirectCallTask implements OctopusTask {

    OctopusStarter octopusStarter;
    String moduleName;
    Map params;

    public DirectCallTask(OctopusStarter octopusStarter) {
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
