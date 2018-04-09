package de.tarent.commons.action;

/**
 * Contains information about specific errors in any action container.
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public class ActionContainerException extends Exception {

    public ActionContainerException() {
        super();
    }

    public ActionContainerException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ActionContainerException( String message ) {
        super( message );
    }

    public ActionContainerException( Throwable cause ) {
        super( cause );
    }
}
