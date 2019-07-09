package de.tarent.octopus.request.servlet;
import de.tarent.octopus.request.TcSession;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * Kapselt das Sessionobjekt des Servletkontainers, um eine eventuelle spätere
 * Umstellung leichter zu machen und eine Unabhängigkeit von der Umgebung zu ereichen.
 *
 * Zur Zeit werden keine weiteren Methoden als die der javax.servlet.http.HttpSession bereit gestellt.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcServletSession implements TcSession {
    protected HttpSession session;

    public HttpSession getHttpSession() {
        return session;
    }

    public TcServletSession(HttpSession session) {
        this.session = session;
    }

    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }

    public long getCreationTime() {
        return session.getCreationTime();
    }

    public String getId() {
        return session.getId();
    }

    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    public void invalidate() {
        session.invalidate();
    }

    public boolean isNew() {
        return session.isNew();
    }

    public void removeAttribute(java.lang.String name) {
        session.removeAttribute(name);
    }

    public void setAttribute(String name, Serializable value) {
        session.setAttribute(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }
}
