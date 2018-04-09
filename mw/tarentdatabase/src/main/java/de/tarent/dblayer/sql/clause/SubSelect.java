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

/**
 *
 */
package de.tarent.dblayer.sql.clause;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.ParamHolder;
import java.util.List;

/**
 * Clause for SubSelects.
 * Example: Expr.in(columnname, new SubSelect(dbx, select))
 *
 * @author kirchner
 */
public class SubSelect extends SetDbContextImpl implements Clause, ParamHolder {

	Select select;

	/**
	 * @see #SubSelect(DBContext, Select)
	 *
	 * @param select
	 * @deprecated please use {@link #SubSelect(DBContext, Select)}
	 */
	public SubSelect(Select select){
		this.select = select;
	}

	/**
	 * Constructs a new SubSelect based on the given Select-Statement.
	 *
	 * @param context
	 * @param select
	 */
	public SubSelect(DBContext context, Select select){
		this.select = select;
		setDBContext(context);
	}

    public void getParams(List list) {
	select.getParams(list);
    }

	/**
	 * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
	 * @deprecated use {@link #clauseToString(DBContext)} instead
	 */
	public String clauseToString() {
		return clauseToString(getDBContext());
	}

	/**
	 * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
	 */
	public String clauseToString(DBContext dbContext) {
		StringBuffer buf = new StringBuffer();
		buf.append("(")
			.append(select)
			.append(")");
		return buf.toString();
	}

    /**
     * Returns an independent clone of this statement.
     * @see java.lang.Object#clone()
     */
    public Object clone() {
	try {
	    SubSelect theClone = (SubSelect)super.clone();
	    theClone.select = (Select)select.clone();
	    return theClone;
	}
	catch(CloneNotSupportedException e) {
		throw new InternalError();
	}
    }

}
