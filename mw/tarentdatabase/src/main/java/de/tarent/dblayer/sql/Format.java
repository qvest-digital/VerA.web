package de.tarent.dblayer.sql;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.tarent.dblayer.engine.DBContext;

/**
 * This class contains static helper methods formatting values into a form
 * accepted by the PostgresQL DBMS.
 *
 * @author kleinw
 */
public final class Format {
    /**
     * This date format is used by the {@link #defaultFormat(Object)} method
     * to create a string representation of a date in PostgreSQL SQL statements.
     */
    static public final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * This method formats a value for the PostgreSQL Database System. It handels Character,
     * String, Boolean, Date and Statement, while all other objects are formatted using
     * their respective <code>toString()</code> method.
     *
     * @deprecated Use {@link SQL#format(DBContext, Object)} instead
     */
    static public final String format(Object value) {
        return defaultFormat(value);
    }

    /**
     * This method formats a value for the PostgreSQL Database System. It handels Character,
     * String, Boolean, Date and Statement, while all other objects are formatted using
     * their respective <code>toString()</code> method.
     */
    static protected final String defaultFormat(Object value) {
        if (value instanceof String || value instanceof Character) {
            String val = String.valueOf(value);
            StringBuffer buffer = new StringBuffer(val.length() + 10).append('\'');
            return Escaper.escape(buffer, val).append('\'').toString();
        } else if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue() ? "1" : "0";
        } else if (value instanceof Date) {
            return "'" + DF.format(value) + "'";
        } else if (value instanceof Statement) {
            return "(" + value.toString() + ")";
        } else {
            return value.toString();
        }
    }
}
