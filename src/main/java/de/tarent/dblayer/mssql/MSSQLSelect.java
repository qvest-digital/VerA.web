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
 * $Id: MSSQLSelect.java,v 1.4 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.mssql;

import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Where;
import java.util.List;

/**
 * Mssql specific extension of the {@link Select} {@link Statement} to support a limit and offset.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class MSSQLSelect extends Select {
    //
    // constructors
    //
    /**
     * This constructor sets the distinctness flag of this <code>SELECT</code>
     * statement.
     * 
     * @see Select#Select(boolean)
     */
    public MSSQLSelect(boolean distinct) {
        super(distinct);
    }

    public void getParams(List list) {
        super.getParams(list);

        // HACK: append them twice, if we have the subselect, for paging
        if (getLimit() != null && getLimit().getOffset() > 0)
            super.getParams(list);
    }

    //
    // interface {@link Statement}
    //
    /**
     * Append the where part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendWherePart(DBContext dbc, StringBuffer sb) {
        super.appendWherePart(dbc, sb);
        if (getLimit() != null && getLimit().getOffset() > 0) {
            String uColumn = getUniqueColumn();
            if (uColumn == null)
                throw new IllegalStateException("For usage of limit and offset in MSSQL, a UniqueColumn has to be set to the Select.");
            
            if (isWhereEmpty(dbc))
                sb.append(Where.WHERE);
            else
                sb.append("AND ");
            
            sb.append(uColumn);
            sb.append(" NOT IN (");
            appendSelectPart(sb);
            insertDistinctOnClause(sb);
            sb.append("TOP ");
            sb.append(getLimit().getOffset());
            sb.append(" ");
            sb.append(uColumn); // select the uniqueColumn instead of all columns
            appendFromPart(dbc, sb);
            
            // doing the same as super.appendWherePart, but with a clone
            if (getWhere() != null) {
                String clauseString = ((Clause)getWhere().clone()).clauseToString(dbc);
                if (clauseString.length() != 0) {
                    sb.append(Where.WHERE);
                    sb.append(clauseString);
                }
            }
            appendGroupBy(sb);
            appendOrder(dbc, sb);
            appendLimit(dbc, sb);
            sb.append(")");
            
        }
    }



    protected void appendColumnList(StringBuffer sb) 
        throws SyntaxErrorException {

        if (getLimit() != null) {
            sb.append("TOP ");
            sb.append(getLimit().getLimit());
            sb.append(" ");            
        }
        super.appendColumnList(sb);
    }

    /**
     * Append the limit, offset ant the end of the statement part to the statement.
     * MSSQL does nothing here.
     */
    protected void appendLimit(DBContext dbc, StringBuffer sb) {
        // MSSQL does nothing here.
    }

    
    //
    // class {@link Select}
    //
    /**
     * This method appends a <code>LIMIT</code> and/or an <code>OFFSET</code>
     * part of the <code>SELECT</code> statement to the given {@link StringBuffer}.<br>
     * This Oracle specific version serializes the {@link Limit} using
     * <code>rownum</code> magic in {@link #statementToString()} as there
     * is no better way to do this, and thus this method should do nothing.
     */
    protected void appendLimitStatement(StringBuffer sb) {
        // do nothing here
    }
    
    //
    // protected helper methods
    //
    /**
     * This method appends the aliases of the columns to select in the innermost
     * <code>SELECT</code> statement to the given {@link StringBuffer}.<br> 
     * As we have to do some <code>rownum</code> magic on Oracle DBMS to emulate
     * <code>LIMIT</code> and <code>OFFSET</code> and this magic includes nested
     * <code>SELECT</code> statements the outer ones need this list to select
     * the requested data columns from the inner ones.
     */
    protected void addColumnAliasList(StringBuffer sb) {
        for (Iterator it = getColumnAliasList().iterator();it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append(", ");
        }
    }
}
