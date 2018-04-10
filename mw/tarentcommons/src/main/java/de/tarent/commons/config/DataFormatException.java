package de.tarent.commons.config;

/**
 * This exception class is catched in some places
 * where plain XML data is accessed. Since direct
 * access to XML elements is discouraged this class
 * is, too.
 *
 * A better approach is to add the neccessary accessor
 * methods in the {@link ConfigManager} class. It should
 * parse the XML and provide it as Map or other suitable
 * data structures to the user.
 *
 * @author Robert Schuster
 * @deprecated Direct access to XML data is discouraged. See class
 * documentation for details.
 *
 */
public class DataFormatException extends Exception {

    public DataFormatException( String message ) {
	super( message );
    }

}
