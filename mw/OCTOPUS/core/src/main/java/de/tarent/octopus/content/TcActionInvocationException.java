package de.tarent.octopus.content;
public class TcActionInvocationException extends TcContentProzessException {
    /**
     * serialVersionUID = -6094698202997450080L
     */
    private static final long serialVersionUID = -6094698202997450080L;

    public TcActionInvocationException(String msg, Throwable t) {
        super(msg, t);
    }

    public TcActionInvocationException(String msg) {
        super(msg);
    }

    public TcActionInvocationException(Throwable t) {
        super(t);
    }
}
