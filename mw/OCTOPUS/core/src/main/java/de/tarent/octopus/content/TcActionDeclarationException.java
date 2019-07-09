package de.tarent.octopus.content;
public class TcActionDeclarationException extends TcContentProzessException {
    /**
     * serialVersionUID = 4046678480790596852L
     */
    private static final long serialVersionUID = 4046678480790596852L;

    public TcActionDeclarationException(String msg) {
        super(msg);
    }

    public TcActionDeclarationException(String msg, Throwable t) {
        super(msg, t);
    }

    public TcActionDeclarationException(Throwable t) {
        super(t);
    }
}
