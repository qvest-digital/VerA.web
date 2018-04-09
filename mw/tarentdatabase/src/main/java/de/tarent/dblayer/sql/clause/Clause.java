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
 * $Id: Clause.java,v 1.9 2007/06/14 14:51:56 dgoema Exp $
 */
package de.tarent.dblayer.sql.clause;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;

/**
 * Base interface for all clauses.
 *
 * @author Wolfgang Klein
 */
public interface Clause extends SetDbContext, Cloneable {
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @deprecated use {@link #clauseToString(DBContext)} instead
     * @return string representation of the clause model
     */
	public String clauseToString();

    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method MAY as a side effect change the {@link DBContext} of this
     * {@link Clause} to the given one.<br>
     * TODO: This method should be able to throw qualified exceptions
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     */
    public String clauseToString(DBContext dbContext);

    /**
     * Returns an independent clone of this close
     * @see java.lang.Object#clone()
     */
    public Object clone();
}
