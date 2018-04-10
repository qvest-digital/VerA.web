package de.tarent.octopus.server;
/**
 * Wrapper um ein CallByReference in Java zu realisieren.
 * TODO: Mit JDK 1.5 durch einen Generic-Type ersetzen, um nicht mehr Casten zu müssen.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public interface InOutParam {
	public Object get();
	public void set(Object newData);
}
