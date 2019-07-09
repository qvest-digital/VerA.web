package de.tarent.octopus.request.internal;
import java.util.Map;

import de.tarent.octopus.request.directcall.OctopusDirectCallResult;
import de.tarent.octopus.request.directcall.TcDirectCallException;

/**
 * Kapselt das Ansprechen des Octopus
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusStarter {

    /**
     * Startet die Abarbeitung einer Anfrage
     *
     * @throws TcDirectCallException
     */
    public OctopusDirectCallResult request(Map requestParams) throws TcDirectCallException;
}
