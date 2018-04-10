package de.tarent.octopus.request;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * Stellt ein Sessionobjekt dar.
 * Dies soll unabhängig von der verwendeten Umgebung (Web/Lokal) passieren.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface TcSession {
	public Object getAttribute(String name);

	public Enumeration getAttributeNames();

	public long getCreationTime();

	public String getId();

	public long getLastAccessedTime();

	public int getMaxInactiveInterval();

	public void invalidate();

	public boolean isNew();

	public void removeAttribute(java.lang.String name);

	public void setAttribute(String name, Serializable value);

	public void setMaxInactiveInterval(int interval);
}
