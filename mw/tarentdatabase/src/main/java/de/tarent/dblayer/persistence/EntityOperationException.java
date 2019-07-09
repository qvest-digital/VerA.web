package de.tarent.dblayer.persistence;
/**
 * exception that will be thrown if there is a problem during
 * operations (insert, update, delete) on entities that are not
 * SQL related
 *
 * @author Martin Pelzer, tarent GmbH
 */
public class EntityOperationException extends Exception {

    private static final long serialVersionUID = -3529977685324692869L;

    public EntityOperationException() {
        super();
    }

    public EntityOperationException(String message) {
        super(message);
    }
}
