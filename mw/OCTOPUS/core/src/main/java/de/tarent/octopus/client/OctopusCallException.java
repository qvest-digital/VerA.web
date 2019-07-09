package de.tarent.octopus.client;
/**
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusCallException extends RuntimeException {
    /**
     * serialVersionUID = -604458204528968362L
     */
    private static final long serialVersionUID = -604458204528968362L;

    protected String errorCode;
    protected String causeMessage;

    public OctopusCallException(String msg, Exception e) {
        super(msg, e);
        if (e != null) {
            causeMessage = e.getMessage();
        }
    }

    public OctopusCallException(String errorCode, String msg, Exception e) {
        super(msg, e);
        this.errorCode = errorCode;
        if (e != null) {
            causeMessage = e.getMessage();
        }
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
