package de.tarent.octopus.request.servlet;
import de.tarent.octopus.request.TcSession;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Implementierung einer Session als Dummy ohne Session-Funktionalität
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcServletDummySession implements TcSession {
    private Hashtable dummyData = new Hashtable();

    public Object getAttribute(String name) {
        return dummyData.get(name);
    }

    public Enumeration getAttributeNames() {
        return dummyData.keys();
    }

    public long getCreationTime() {
        return -1;
    }

    public String getId() {
        return "NO-DUMMYDATA";
    }

    public long getLastAccessedTime() {
        return -1;
    }

    public int getMaxInactiveInterval() {
        return -1;
    }

    public void invalidate() {
    }

    public boolean isNew() {
        return true;
    }

    public void removeAttribute(java.lang.String name) {
        dummyData.remove(name);
    }

    public void setAttribute(String name, Serializable value) {
        dummyData.put(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
    }
}
