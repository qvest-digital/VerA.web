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

/* $Id: Limit.java,v 1.9 2007/06/14 14:51:56 dgoema Exp $
 * Created on 18.11.2004
 */
package de.tarent.dblayer.sql.clause;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This {@link Clause} represents the <code>LIMIT</code> and
 * <code>OFFSET</code> parts of a <code>SELECT</code> statement.
 * 
 * @author Simon B�hler
 */
public class Limit extends SetDbContextImpl implements Clause {
    //
    // public constants
    //
    /** the String "<code> LIMIT </code>" */
    final static private String LIMIT = " LIMIT ";
    /** the String "<code> OFFSET </code>" */
    final static private String OFFSET = " OFFSET ";

    //
    // protected members
    //
    /** the <code>LIMIT</code> number */
    private Integer _limit;
    /** the <code>OFFSET</code> number */
    private Integer _offset;

    //
    // constructors
    //
    /**
     * This constructor takes limit and offset as primitive <code>int</code>s.
     */
	public Limit(int limit, int offset) {
        this(new Integer(limit), new Integer(offset));
    }

    /**
     * This constructor takes limit and offset as {@link Integer Integers}.
     */
	public Limit(Integer limit,Integer offset) {
	    _limit = limit;
	    _offset = offset;
	}

    //
    // public getters and setters
    //
	/** the limit */
    public int getLimit() {
        return _limit.intValue();
    }

    /** the offset */
    public int getOffset() {
        return _offset.intValue();
    }

    //
    // interface {@link Clause}
    //
    /**
     * This method generates a string representation of the clause model
     * for use in SQL statements.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     * 
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
     * @deprecated use {@link #clauseToString(DBContext)} instead
     */
	final public String clauseToString() {
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
    	setDBContext(dbContext);
		StringBuffer sb = new StringBuffer();
		clauseToString(sb);
		return sb.toString();
	}

    /**
     * This method attaches the actual <code>LIMIT</code> and <code>OFFSET</code>
     * clauses to a {@link StringBuffer}.
     */
	public void clauseToString(StringBuffer sb) {
	    if (_limit.intValue()> 0) {
		    sb.append(LIMIT);
		    sb.append(_limit);
		}
	    if (_offset.intValue() > 0) {
		    sb.append(OFFSET);
		    sb.append(_offset);
		}
	}


    public Object clone() {
        try {
            return super.clone();
        }
        catch(CloneNotSupportedException e) {
        	throw new InternalError();
        }
    }

}
