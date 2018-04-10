package de.tarent.octopus.client;
/**
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusTask {

    public OctopusTask add(String paramName, Object paramValue);
    public OctopusResult invoke()
        throws OctopusCallException;
    public void setConnectionTracking(boolean contrack);
    public boolean isConnectionTracking();
}
