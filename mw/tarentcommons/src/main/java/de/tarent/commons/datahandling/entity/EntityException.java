package de.tarent.commons.datahandling.entity;
/**
 * this exception can be thrown by classes that implement the Entity interface
 *
 * @author Steffi Tinder, tarent GmbH
 */

public class EntityException extends Exception {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2787660724909115677L;

    public EntityException() {
        super();
    }

    public EntityException(String message) {
        super(message);
    }

    public EntityException(Throwable cause) {
        super(cause);
    }

    public EntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
