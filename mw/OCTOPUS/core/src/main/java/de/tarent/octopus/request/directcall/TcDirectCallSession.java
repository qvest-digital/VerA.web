package de.tarent.octopus.request.directcall;
import de.tarent.octopus.request.TcSession;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Stellt ein Sessionobjekt eines direkten Aufrufes dar.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcDirectCallSession implements TcSession {
    Hashtable attributes = new Hashtable();
    long creationTime;
    String id;

    public TcDirectCallSession() {
        this.creationTime = System.currentTimeMillis();
        this.id = (new StringBuffer(Long.toHexString(System.currentTimeMillis())).reverse()).toString();
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void setAttribute(String name, Serializable value) {
        attributes.put(name, value);
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getId() {
        return id;
    }

    public long getLastAccessedTime() {
        return -1;
    }

    public int getMaxInactiveInterval() {
        return -1;
    }

    public void invalidate() {
        // do nothing
    }

    public boolean isNew() {
        return false;
    }

    public void setMaxInactiveInterval(int interval) {
        // do nothing
    }
}
