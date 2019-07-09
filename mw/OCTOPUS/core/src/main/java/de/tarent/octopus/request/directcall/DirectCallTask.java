package de.tarent.octopus.request.directcall;
import de.tarent.octopus.client.OctopusCallException;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.request.internal.OctopusStarter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
                ((List) param).addLast(paramValue);
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
        if (res.errorWhileProcessing()) {
            throw new OctopusCallException(res.getErrorMessage(), res.getErrorException());
        }
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
        private static final long serialVersionUID = 8662309893416645859L;
        // nothing more than a marker
    }
}
