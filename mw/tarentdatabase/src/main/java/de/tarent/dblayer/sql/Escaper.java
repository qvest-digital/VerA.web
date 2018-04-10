/*
 * $Id: Escaper.java,v 1.6 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql;

/**
 * This utility class offers methods for generic escaping of SQL string literals.
 * This includes expecially handling of the characters "'" and "\".
 *
 * @author Wolfgang Klein
 */
public class Escaper {
    /**
     * This method escapes the given {@link String}
     */
	static public String escape(String value) {
        // If we do not have to escape something, we return directly
        if (-1 == value.indexOf("'") && -1 == value.indexOf("\\"))
            return value;

		return escape(new StringBuffer(value.length() * 2), value).toString();
	}

    /**
     * This method appends the escaped value to the supplied {@link StringBuffer}.
     *
     * @param buffer {@link StringBuffer} to append to
     * @param value value to escape and append
     * @return returns the supplied {@link StringBuffer}
     */
	static public StringBuffer escape(StringBuffer buffer, String value) {
        // TODO: It would be much faster to do the escapings during the the copy to the StringBuffer
		final int newValueStart = buffer.length();
		buffer.append(value);
        int p = newValueStart;
		while ((p = buffer.indexOf("'", p)) != -1) {
			buffer.replace(p++, p++, "''");
		}
        p = newValueStart;
		while ((p = buffer.indexOf("\\", p)) != -1) {
			buffer.replace(p++, p++, "\\\\");
		}
		return buffer;
	}
}
