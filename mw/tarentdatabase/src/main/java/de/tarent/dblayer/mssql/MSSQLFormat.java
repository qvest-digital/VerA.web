package de.tarent.dblayer.mssql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.Statement;

/**
 * This class contains static helper methods formatting values into a form
 * accepted by the Oracle DBMS.
 *
 * @author Sebastian Mancke
 */
public class MSSQLFormat {
    /**
     * This date format is used by the {@link #format(Object)} method to create a string
     * representation of a date in Oracle SQL statements. This representation is used as
     * first parameter of the <code>to_date</code> function; the second parameter is a
     * Oracle-ish version of the date format String telling the DBMS how to interpret
     * our date string. Thus both formatting strings have to be kept in sync to allow
     * this to function properly.
     */
	static public final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	//MsSql fails to convert Dates after the year 9999, see static constructor
	static public final Date LARGEST_DATE = Calendar.getInstance().getTime();
	static public final Date SMALLEST_DATE = Calendar.getInstance().getTime();

	static {
		//set LARGEST_DATE to correct value
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(9999, 11, 31);
		LARGEST_DATE.setTime(cal.getTimeInMillis());
		cal.clear();
		cal.set(1753, 0, 1);
		SMALLEST_DATE.setTime(cal.getTimeInMillis());
	}

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
	} else if (value == null){
		return null;
	}else if (value instanceof Boolean){
			return ((Boolean)value).booleanValue() ? "1" : "0";
	}else if (value instanceof Date){
		Date date = (Date) value;
		//MsSql fails to convert Dates after the year 9999
		if (date.after(LARGEST_DATE)){
			date = LARGEST_DATE;
		}else if(date.before(SMALLEST_DATE)){
			date = SMALLEST_DATE;
		}
	  return "convert(datetime, '"+DF.format(date)+"', 120)";
	}else if (value instanceof Statement){
	    return "(" + value.toString() + ")";
	}else{
	    return value.toString();
	}
    }
}
