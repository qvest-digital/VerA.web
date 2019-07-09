package de.tarent.octopus.data;
import de.tarent.octopus.util.RootCauseException;

/**
 * Kann benutzt werden um Fehler, die wärend des Datenzugriffes auftreten
 * weiter zu geben.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcDataAccessException extends Exception implements RootCauseException {
    /**
     * serialVersionUID = 2926994428165573435L
     */
    private static final long serialVersionUID = 2926994428165573435L;

    /**
     * Expection, die der eigentliche Grund ist.
     */
    Throwable rootCause;

    public TcDataAccessException(String message) {
        super(message);
    }

    public TcDataAccessException(String message, Throwable rootCause) {
        super(message, rootCause);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }
}
