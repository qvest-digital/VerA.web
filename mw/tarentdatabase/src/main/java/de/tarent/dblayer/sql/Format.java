/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

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
     * to create a string representation of a date in PostgresQL SQL statements.
     */
	static public final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        
    /**
     * This method formats a value for the PostgresQL Database System. It handels Character,
     * String, Boolean, Date and Statement, while all other objects are formatted using
     * their respective <code>toString()</code> method.
     *
     * @deprecated Use {@link SQL#format(DBContext, Object)} instead
     */
    static public final String format(Object value) {
        return defaultFormat(value);
    }

    /**
     * This method formats a value for the PostgresQL Database System. It handels Character,
     * String, Boolean, Date and Statement, while all other objects are formatted using
     * their respective <code>toString()</code> method.
     */
    static protected final String defaultFormat(Object value) {
        if (value instanceof String || value instanceof Character) {
            String val = String.valueOf(value);
        	StringBuffer buffer = new StringBuffer(val.length() + 10).append('\'');
            return Escaper.escape(buffer, val).append('\'').toString();
        } 
        else if (value == null)
    			return null;
        	else if (value instanceof Boolean)
			return ((Boolean)value).booleanValue() ? "1" : "0";
        else if (value instanceof Date) 
            return "'" + DF.format(value) + "'";
        else if (value instanceof Statement)
            return "(" + value.toString() + ")";
        else
            return value.toString();
    }
}
