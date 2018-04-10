package de.tarent.octopus.data;
/**
 * Für Fehler, die auftreten, wenn ein Baum aus einem ungültigen Postfix Ausdruck aufgebaut werden soll.
 *
 * @see TarWhereClause
 */
public class TarMalformedWhereClauseException extends Exception {
    /**
	 * serialVersionUID = -6195741158552895263L
	 */
	private static final long serialVersionUID = -6195741158552895263L;

	public TarMalformedWhereClauseException(String message) {
        super(message);
    }
}
