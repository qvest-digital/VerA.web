package de.tarent.octopus.config;
import de.tarent.octopus.util.RootCauseException;

/**
 * Für Fehlermeldungen, die beim Einlesen der Config entstehen können.
 */
public class TcConfigException extends Exception implements RootCauseException {
    /**
     * serialVersionUID = -1986281111385526822L
     */
    private static final long serialVersionUID = -1986281111385526822L;

    /**
     * Expection, die der eigentliche Grund ist.
     */
    Throwable rootCause;

    public TcConfigException(String message) {
        super(message);
    }

    public TcConfigException(String message, Throwable rootCause) {
        super(message, rootCause);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }
}
