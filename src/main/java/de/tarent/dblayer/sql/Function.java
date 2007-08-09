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
 * $Id: Function.java,v 1.5 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql;

import de.tarent.dblayer.sql.statement.Procedure;

/**
 * Wrapper for a {@link String} instance used to prevent formatting methods from
 * escaping and quoting it.
 *
 * @deprecated use {@link de.tarent.dblayer.sql.clause.RawClause} or {@link Procedure} instead
 * @author Wolfgang Klein
 */
public class Function {
    /**
     * This constructor stores the given {@link String} parameter locally.
     */
    public Function(String function) {
        _function = function;
    }

    /**
     * This override of {@link Object#toString()} returns the {@link String}
     * stored during construction.
     */
    public String toString() {
        return _function;
    }

    /** this is the {@link String} this wrapper class is to wrap */
    private final String _function;
}
