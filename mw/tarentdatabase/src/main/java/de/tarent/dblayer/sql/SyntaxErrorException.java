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
 * $Id: SyntaxErrorException.java,v 1.3 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql;

import java.sql.SQLException;

/**
 * This {@link Exception} class is a {@link SQLException} used for syntax
 * problems in a db layer model of a statement.
 */
public class SyntaxErrorException extends SQLException {
    /** The constructor stores an informational message. */
	public SyntaxErrorException(String msg) {
		super(msg);
	}
    
    /** serialization UID */
    private static final long serialVersionUID = 3257848787924955441L;
}
