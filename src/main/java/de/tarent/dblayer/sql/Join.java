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
 * $Id: Join.java,v 1.4 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.sql;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.RawClause;

/**
 * This class represents a <code>JOIN</code> part of a <code>SELECT</code>
 * statement.
 */
public class Join extends SetDbContextImpl {
    //
    // public constants
    //
    /** join type: inner join */
	public static final int INNER = 1;
    /** join type: left outer join */
	public static final int LEFT_OUTER = 2;
    /** join type: right outer join */
	public static final int RIGHT_OUTER = 3;
    /** join type: outer join */
    public static final int OUTER = 4;

    //
    // constructors
    //
    /**
     * This constructor creates a join using a custom join {@link Clause}.<br>
     * It sets the <code>joinHead</code> member to the required joining keywords
     * and the table name and saves the given clause for {@link DBContext}
     * sensitive serialization just in time.
     * 
     * @param type type of the join, one of {@link #INNER}, {@link #LEFT_OUTER}
     *  and {@link #RIGHT_OUTER}.
     * @param table name of the table to join
     * @param clause the join {@link Clause}; <code>null</code> for cross joins.
     */
    public Join(int type, String table, Clause clause) {
        StringBuffer buffer = new StringBuffer(20);
        appendJoin(buffer, type);
        buffer.append(table).append(' ');
        joinHead = buffer.toString();
        this.clause = clause;
    }

    /**
     * This constructor creates a cross join. 
     * 
     * @param type type of the join, one of {@link #INNER}, {@link #LEFT_OUTER}
     *  and {@link #RIGHT_OUTER}.
     * @param table name of the table to join
     */
    public Join(int type, String table) {
        this(type, table, null);
    }
    
    /**
     * This constructor creates a join on the two given columns. 
     * 
     * @param type type of the join, one of {@link #INNER}, {@link #LEFT_OUTER}
     *  and {@link #RIGHT_OUTER}.
     * @param table name of the table to join
     * @param leftColumn left side column name of the equality
     * @param rightColumn right side column name of the equality
     */
	public Join(int type, String table, String leftColumn, String rightColumn) {
        this(type, table, new RawClause(leftColumn + '=' + rightColumn));
	}

    //
    // class {@link Object}
    //
    /**
     * This method returns a {@link String} representation of the join. It is a
     * concatenation of the <code>joinHead</code> and a String representation of
     * the join {@link Clause} created using the current {@link DBContext}.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (clause == null)
            return joinHead;
        StringBuffer buffer = new StringBuffer(joinHead);
        buffer.append("ON (")
              .append(clause.clauseToString(getDBContext()))
              .append(") ");
        return buffer.toString();
    }
    
    //
    // protected helper methods
    //
    /**
     * This method appends the join keywords to the given {@link StringBuffer}
     * according to the given type.
     */
	protected static void appendJoin(StringBuffer buffer, int type) {
		switch (type) {
		case LEFT_OUTER:
			buffer.append(LEFT_OUTER_JOIN);
			break;
		case RIGHT_OUTER:
			buffer.append(RIGHT_OUTER_JOIN);
			break;
        case OUTER:
            buffer.append(OUTER_JOIN);
            break;
		default:
			buffer.append(SIMPLE_JOIN);
			break;
		}
	}

    //
    // private constants
    //
    /** the String "<code> JOIN </code>" */
    private static final String SIMPLE_JOIN = " JOIN ";
    /** the String "<code> LEFT OUTER JOIN </code>" */
    private static final String LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
    /** the String "<code> RIGHT OUTER JOIN </code>" */
    private static final String RIGHT_OUTER_JOIN = " RIGHT OUTER JOIN ";
    /** the String "<code> FULL JOIN </code>" */
    private static final String OUTER_JOIN = " FULL JOIN ";
    
    //
    // protected member variables
    //
    /** this is the concatenation of the appropriate join keywords, the table name and a ' ' */
    private final String joinHead;
    /** this is the join {@link Clause}; it may be <code>null</code> for cross joins */
    private final Clause clause;
}
