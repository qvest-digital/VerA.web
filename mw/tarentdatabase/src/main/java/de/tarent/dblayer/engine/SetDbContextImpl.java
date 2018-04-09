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
 * $Id: SetDbContextImpl.java,v 1.3 2007/06/14 14:51:57 dgoema Exp $
 *
 * Created on 18.04.2006
 */
package de.tarent.dblayer.engine;

import de.tarent.dblayer.sql.SQL;

/**
 * This class is a minimal {@link SetDbContext} implementation. It contains
 * public getter and setter methods and a private member variable to access
 * the db layer context.
 *
 * @author Michael Klink
 */
public class SetDbContextImpl implements SetDbContext {
    //
    // getter and setter methods
    //
    /**
     * This method returns the database execution context. A <code>null</code>
     * value must be interpreted as a sensible default environment.
     *
     * @return database execution context
     */
    public DBContext getDBContext() {
        return dbContext;
    }

    /**
     * This method injects the database execution context.
     *
     * @param dbc database execution context
     * @see de.tarent.dblayer.engine.SetDbContext#setDBContext(de.tarent.dblayer.engine.DBContext)
     */
    public void setDBContext(DBContext dbc) {
        this.dbContext = dbc;
    }

    //
    // protected inner classes
    //
    /**
     * This helper class allows to easily format a literal just in time.
     */
    protected class LiteralWrapper {
        /** The constructor stores the given literal locally. */
        public LiteralWrapper(Object literal) {
            this.literal = literal;
        }
        /** This method returns the formatted literal. */
        public String toString() {
            return SQL.format(getDBContext(), literal);
        }
        /** the literal itself */
        final Object literal;
    }

    //
    // member variables
    //
    /** database execution context */
    private DBContext dbContext = null;
}
