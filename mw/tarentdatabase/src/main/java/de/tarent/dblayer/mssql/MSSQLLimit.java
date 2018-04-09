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

package de.tarent.dblayer.mssql;

import de.tarent.dblayer.sql.clause.Limit;

public class MSSQLLimit extends Limit {

	public MSSQLLimit(Limit limit){
		this(new Integer(limit.getLimit()), new Integer(limit.getOffset()));
	}
	
	public MSSQLLimit(int limit, int offset) {
		this(new Integer(limit), new Integer(offset));
	}
	
	public MSSQLLimit(Integer limit, Integer offset){
		super(limit, offset);
	}
	
    /**
     * This method attaches the actual <code>LIMIT</code> and <code>OFFSET</code>
     * clauses to a {@link StringBuffer}.
     */
	public void clauseToString(StringBuffer sb) {
	    if (getLimit()> 0) {
		    sb.append("TOP ");
		    sb.append(getLimit());
		    sb.append(" ");
		}
	    if(getOffset()!=0){
	    	throw new RuntimeException("Offsets other than zero are not supportet on MSSQL.");
	    }
	}

}
