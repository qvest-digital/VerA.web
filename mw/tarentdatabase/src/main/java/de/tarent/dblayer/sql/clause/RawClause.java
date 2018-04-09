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

/* $Id: RawClause.java,v 1.6 2007/06/14 14:51:56 dgoema Exp $
 * Created on 22.07.2004
 */
package de.tarent.dblayer.sql.clause;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This {@link Clause} represents SQL statement parts that are given
 * in raw SQL text instead of modelled in a structured way.<br>
 * Because of the danger of SQL injection (or simply sloppy written
 * SQL) this class should only be used to support features not (yet)
 * explicitely modelled by the db layer classes.
 */
public class RawClause extends SetDbContextImpl implements Clause {

    //
    // protected members
    //
    /** raw SQL in a {@link String} */
    String string;
    /** raw SQL in a {@link StringBuffer} */
    StringBuffer buffer;

    //
    // constructors
    //
    /** This constructor accepts the raw SQL from a {@link String} object. */
	public RawClause(String string) {
        assert string != null;
        this.buffer = null;
		this.string = string;
	}

    /**
     * This constructor accepts the raw SQL from a {@link StringBuffer} object.
     * The buffer is not fixed yet, it can be manipulated until finally a
     * {@link #clauseToString()} version is called.
     */
	public RawClause(StringBuffer buffer) {
		this.buffer = buffer;
        this.string = null;
	}

    //
    // interface {@link Clause}
    //
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     * 
     * @deprecated use {@link #clauseToString(DBContext)} instead
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
     */
	public String clauseToString() {
        return clauseToString(getDBContext());
    }
    
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     * 
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
     */
    public String clauseToString(DBContext dbContext) {
		return buffer != null ? buffer.toString() : string;
	}

    //
    // class {@link Object}
    //
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.
     * 
     * @return string representation of the clause model
     * @see java.lang.Object#toString()
     */
	public String toString() {
		return clauseToString(getDBContext());
	}

    /**
     * Returns an independent clone of this statement.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            RawClause theClone = (RawClause)super.clone();
            if (buffer != null)
                theClone.buffer = new StringBuffer(buffer.toString());
            return theClone;
        }
        catch(CloneNotSupportedException e) {
        	throw new InternalError();
        }
      }    
}
