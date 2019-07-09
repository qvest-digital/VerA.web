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
     * Dieser Konstruktor erhält einen Fehlertext.
     *
     * @param message Fehlertext
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Dieser Konstruktor erhält einen geschachtelten {@link Throwable}.
     *
     * @param cause geschachtelter {@link Throwable}
     */
    public MappingException(Throwable cause) {
        super(cause);
    }

    /**
     * Dieser Konstruktor erhält einen Fehlertext und einen geschachtelten {@link Throwable}.
     *
     * @param message Fehlertext
     * @param cause   geschachtelter {@link Throwable}
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    //
    // geschützt Member
    //
    /**
     * Serialisierungs-ID (um Eclipse glücklich zu machen)
     */
    private static final long serialVersionUID = 974345013719043419L;
}
