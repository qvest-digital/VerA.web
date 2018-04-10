/*
 * $Id: OracleFormat.java,v 1.4 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.oracle;

import java.text.SimpleDateFormat;
import java.util.Date;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.Statement;

/**
 * This class contains static helper methods formatting values into a form
 * accepted by the Oracle DBMS.
 *
 * @author Sebastian Mancke
 */
public class OracleFormat {
    /**
     * This date format is used by the {@link #format(Object)} method to create a string
     * representation of a date in Oracle SQL statements. This representation is used as
     * first parameter of the <code>to_date</code> function; the second parameter is a
     * Oracle-ish version of the date format String telling the DBMS how to interpret
     * our date string. Thus both formatting strings have to be kept in sync to allow
     * this to function properly.
     */
	static public final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * This method formats a value for the Oracle Database System. It handels Character,
     * String, Boolean, Date and Statement, while all other objects are formatted using
     * their respective <code>toString()</code> method.
     */
    static public final String format(Object value) {
	if (value instanceof String || value instanceof Character) {
	    String val = String.valueOf(value);
	    StringBuffer buffer = new StringBuffer(val.length() + 10).append('\'');
	    return Escaper.escape(buffer, val).append('\'').toString();
	} else if (value == null)
		return null;
	else if (value instanceof Boolean)
			return ((Boolean)value).booleanValue() ? "'Y'" : "'N'";
	else if (value instanceof Date)
	    return "to_date('" + DF.format(value) + "', 'YYYY-MM-DD HH24:MI:SS')";
	else if (value instanceof Statement)
	    return "(" + value.toString() + ")";
	else
	    return value.toString();
    }
}
