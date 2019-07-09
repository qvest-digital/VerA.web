package de.tarent.octopus.request.directcall;
import de.tarent.octopus.util.RootCauseException;

/**
 * Exception, die geworfen werden soll, wenn
 * Probleme, beim direkten Aufruf des Octopus durch OctopusStarter auftreten.
 * Sie kapseln die eigentliche Exceptiopn.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcDirectCallException extends Exception implements RootCauseException {
    private static final long serialVersionUID = -1238002490264696356L;

    /**
     * Expection, die der eigentliche Grund ist.
     */
    Throwable rootCause;

    public TcDirectCallException(String message) {
        super(message);
    }

    public TcDirectCallException(String message, Throwable rootCause) {
        super(message, rootCause);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }
}
