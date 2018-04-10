package de.tarent.octopus.content;
/**
 * Diese klasse kapselt Fehlermeldungen, die wärend
 * der Verarbeitung des Contents auf treten.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcContentProzessException extends Exception {
    /**
	 * serialVersionUID = 1890104261924600634L
	 */
	private static final long serialVersionUID = 1890104261924600634L;

	public TcContentProzessException(String message) {
        super(message);
    }
    public TcContentProzessException(String message, Throwable cause) {
        super(message, cause);
    }
    public TcContentProzessException(Throwable cause) {
        super(cause);
    }
}
