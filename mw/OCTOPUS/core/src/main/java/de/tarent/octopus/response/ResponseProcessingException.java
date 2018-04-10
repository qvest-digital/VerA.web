package de.tarent.octopus.response;
import de.tarent.octopus.util.RootCauseException;

/**
 * Für Fehler, die wärend der Ausgabeverarbeitung auf treten.
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, tarent GmbH
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, tarent GmbH
 */
public class ResponseProcessingException extends Exception implements RootCauseException {
    /**
	 * serialVersionUID = -1283546477850825557L
	 */
	private static final long serialVersionUID = -1283546477850825557L;

	/**
     * Expection, die der eigentliche Grund ist.
     */
    Throwable rootCause;

    public ResponseProcessingException(String message) {
        super(message);
    }

    public ResponseProcessingException(String message, Throwable rootCause) {
        super(message, rootCause);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }
}
