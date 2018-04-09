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
 * $Id: Sequence.java,v 1.7 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql.statement;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Statement;

/**
 * This {@link Statement} models SQL sequence value queries.<br>
 * It differs quite a bit from most other {@link Statement} implementations
 * as it is not designed to be used on a single sequence only.
 *
 * @author Wolfgang Klein
 */
public class Sequence extends AbstractStatement {
    //
    // public methods
    //
    /**
     * This method sets this {@link Sequence} to return the current value
     * of the sequence having the given name.
     *
     * @param sequence name of the sequence
     * @return this {@link Sequence} {@link Statement} instance
     */
	public Sequence currVal(String sequence) {
		_sequence = new StringBuffer("SELECT CURRVAL ('")
            .append(sequence)
            .append("')")
            .toString();
		return this;
	}

    /**
     * This method sets this {@link Sequence} to proceed to the next value
     * of the sequence having the given name and return it.
     *
     * @param sequence name of the sequence
     * @return this {@link Sequence} {@link Statement} instance
     */
	public Sequence nextVal(String sequence) {
		_sequence = new StringBuffer("SELECT NEXTVAL ('")
			.append(sequence)
			.append("')")
			.toString();
		return this;
	}

    /**
     * This method returns the next value of the given sequence as an Integer.
     *
     * @param sequence name of the sequence
     * @return the next value of the sequence
     */
	public Integer nextVal(String pool, String sequence) throws SQLException {
	    Sequence seq = new Sequence().nextVal(sequence);
	    ResultSet rs = null;
	    try {
	        rs = DB.getResultSet(pool, seq);
            Integer nextVal = null;
            if (rs.next())
                nextVal = new Integer(rs.getInt("nextVal"));
            return nextVal;
        } finally {
            if (rs != null)
                rs.close();
        }
	}

    /**
     * This method returns the next value of the given sequence as an Integer.
     *
     * @param sequence name of the sequence
     * @return the next value of the sequence
     */
	public Integer nextVal(DBContext dbx, String sequence) throws SQLException {
	    Sequence seq = new Sequence().nextVal(sequence);
	    ResultSet rs = null;
	    try {
	        rs = DB.getResultSet(dbx, seq);
            Integer nextVal = null;
            if (rs.next())
                nextVal = new Integer(rs.getInt("nextVal"));
            return nextVal;
        } finally {
            if (rs != null)
                rs.close();
        }
	}
    //
    // interface {@link Statement}
    //
    /**
     * This method executes the modelled sequence statement within the
     * {@link DBContext} of this {@link Sequence} instance and returns
     * a {@link Result}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see Statement#execute()
     */
	public Object execute() throws SQLException {
	    return DB.result(getDBContext(), this);
    }

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see Statement#statementToString()
     */
    public String statementToString() {
        return _sequence;
    }

    //
    // class {@link Object}
    //
    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement} using the method {@link #statementToString()}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
	    return _sequence;
	}

    //
    // protected member variables
    //
    /**
     * The current statement of this {@link Sequence}; it is set using the
     * methods {@link #currVal(String)} or {@link #nextVal(String)}.
     */
    private String _sequence = null;
}
