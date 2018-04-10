package de.tarent.octopus.request;
/**
 * Exception, die geworfen werden soll, wenn
 * Probleme, wie z.B. fehlende Attribute und Anweidsungen wärend der
 * Taskabarbeitung im ActionManager auftreten.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTaskProzessingException extends Exception {
    /**
	 * serialVersionUID = -3146183468486361425L
	 */
	private static final long serialVersionUID = -3146183468486361425L;

	public TcTaskProzessingException(String message) {
        super(message);
    }
}
