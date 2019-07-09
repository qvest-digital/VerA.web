package de.tarent.dblayer.sql;
import java.sql.SQLException;

/**
 * This {@link Exception} class wraps {@link SQLException} combined with the
 * statement during the execution of which they occured.
 */
public class SQLStatementException extends SQLException {
    /**
     * This constructor stores the given {@link SQLException} locally and
     * sets this exceptions description to a concatenation of the localized
     * message of the wrapped excaption and the given SQL statement.
     */
    public SQLStatementException(SQLException e, String stmnt) {
        super(e.getLocalizedMessage() + " [SQL: " + stmnt + "]");
        setStackTrace(e.getStackTrace());
        exception = e;
    }

    /**
     * This override of {@link Object#toString()} returns a concatenation
     * of the class name of the stored {@link SQLException} and the localized
     * message of this exception.
     */
    public String toString() {
        String s = exception.getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * wrapped {@link SQLException}
     */
    private final SQLException exception;

    /**
     * serialization UID
     */
    private static final long serialVersionUID = 3257848787924955442L;
}
