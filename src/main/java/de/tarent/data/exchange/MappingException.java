/*
 * $Id: MappingException.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 23.08.2005
 */
package de.tarent.data.exchange;

/**
 * Diese Klasse stellt Ausnahmen beim Mapping von Feldern dar.
 * 
 * @author mikel
 */
public class MappingException extends Exception {
    //
    // Konstruktoren
    //
    /**
     * Der leere Konstruktor
     */
    public MappingException() {
        super();
    }

    /**
     * Dieser Konstruktor erh�lt einen Fehlertext.
     * 
     * @param message Fehlertext
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Dieser Konstruktor erh�lt einen geschachtelten {@link Throwable}. 
     * 
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(Throwable cause) {
        super(cause);
    }

    /**
     * Dieser Konstruktor erh�lt einen Fehlertext und einen geschachtelten {@link Throwable}.
     * 
     * @param message Fehlertext
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    //
    // gesch�tzt Member
    //
    /** Serialisierungs-ID (um Eclipse gl�cklich zu machen) */
    private static final long serialVersionUID = 974345013719043419L;
}
