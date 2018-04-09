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

/*
 * $Id: SQLStatementException.java,v 1.5 2007/06/14 14:51:57 dgoema Exp $
 */
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

    /** wrapped {@link SQLException} */
    private final SQLException exception;

    /** serialization UID */
    private static final long serialVersionUID = 3257848787924955442L;
}
