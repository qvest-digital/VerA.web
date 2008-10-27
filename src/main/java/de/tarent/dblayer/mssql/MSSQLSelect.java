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

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import java.util.List;

/**
 * Mssql specific extension of the {@link Select} {@link Statement} to 
 * support a limit and offset.
 *
 * In Mssql limit/offset works this way:
 * 
 *  SELECT TOP &lt;limit&gt; &lt;projection&gt;
 * 	FROM (SELECT TOP &lt;limit&gt; &lt;projection&gt; 
 * 		  FROM (SELECT TOP &lt;limit+offset&gt; &lt;projection&gt;
 *				FROM &lt;relations&gt;
 *				WHERE &lt;selection&gt; 
 *				ORDER BY &lt;normal order&gt;	
 *				)
 *		  ORDER BY &lt;reverse order&gt;
 *		  )
 *  ORDER BY &lt;normal order&gt;
 *  
 * @author Sebastian Mancke, tarent GmbH
 * @author Christian Preilowski, tarent GmbH
 * 
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

    /**
     * Append the where part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendWherePart(DBContext dbc, StringBuffer sb) {
    	//if there is no limit/offset -> normal where clause else do nothing
    	if (getLimit() == null || getLimit().getOffset() <= 0){
    		super.appendWherePart(dbc, sb);
    	}
    }
    
    protected void appendFromPart(DBContext dbc, StringBuffer sb){
    	//mssql specific limit impl
    	if (getLimit() != null && getLimit().getOffset() > 0){
    		sb.append(" FROM (");
    		//MIDDLE SELECT
    		appendSelectPart(sb);
    	    insertDistinctOnClause(sb);
    	    sb.append("TOP "+getLimit().getLimit()+" ");
    	    addColumnAliasList(sb);
    	    sb.append(" FROM (");
    			//INNER SELECT
	    		appendSelectPart(sb);
	    	    insertDistinctOnClause(sb);
	    		sb.append("TOP "+(getLimit().getOffset()+getLimit().getLimit())+" ");
	    		try {
					super.appendColumnList(sb);
				} catch (SyntaxErrorException e) {
					throw new RuntimeException(e);
				}
	    		super.appendFromPart(dbc, sb);
	    		super.appendWherePart(dbc, sb);
	    		super.appendOrder(dbc, sb);
	    		sb.append(" ) AS inner_select ");
	    		//INNER SELECT END
	    	addAliasedOrderBy(sb, true);
	    	sb.append(" ) AS middle_select ");
    		//MIDDLE SELECT END
    	}else{	//if there is no limit/offset -> normal from clause
    		super.appendFromPart(dbc, sb);
		}
    }

    protected void appendOrder(DBContext dbc, StringBuffer sb) {
    	//mssql specific limit impl
    	if (getLimit() != null && getLimit().getOffset() > 0){
    		addAliasedOrderBy(sb, false);
    	}else{
			super.appendOrder(dbc, sb);
    	}
    }

    protected void appendColumnList(StringBuffer sb) 
        throws SyntaxErrorException {
    	//mssql specific limit impl
        if (getLimit() != null) {
            sb.append("TOP ");
            sb.append(getLimit().getLimit());
            sb.append(" ");
            if (getLimit().getOffset() > 0){
            	addColumnAliasList(sb);
            }else{
            	super.appendColumnList(sb);
            }
        }else{
        	super.appendColumnList(sb);
        }
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
        for (Iterator<String> it = getColumnAliasList().iterator();it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append(", ");
        }
    }
    
    
    /**
     * Adds a order by clause to the query that uses the aliases of 
     * the order columns. if reverseOrder is set true, the order of each 
     * order column will be reversed.
     * 
     * @param sb the String buffer to add the clause
     * @param reverseOrder  reverse order switch
     */
    protected void addAliasedOrderBy(StringBuffer sb, boolean reverseOrder){
		//create a select column - alias map
		List<String> selectColumns = getSelectColumns();
		List<String> columnAliases = getColumnAliasList();
		HashMap<String, String> aliasMapping = new HashMap<String, String>();
		for (int i=0;i<selectColumns.size();i++){
			//because getSelectColumns() returns "columnname AS alias" 
			//we have to extract only the first part
			StringTokenizer st = new StringTokenizer(selectColumns.get(i),"AS");
			aliasMapping.put(st.nextToken().trim(), columnAliases.get(i));
		}
		
		if (super.getOrderClause() != null) {
			Order orderBy = super.getOrderClause();
			List<String> orderColumns = orderBy.getColumns();
    		List<Boolean> sortDirections = orderBy.getSortDirections();
    		sb.append(" ORDER BY");
    		for(int i=0;i<orderColumns.size();i++){
				if (sortDirections.get(i)==true)
					if (reverseOrder)
						sb.append(" "+aliasMapping.get(orderColumns.get(i))+" DESC");
					else
						sb.append(" "+aliasMapping.get(orderColumns.get(i))+" ASC");
				else
					if (reverseOrder)
						sb.append(" "+aliasMapping.get(orderColumns.get(i))+" ASC");
					else
						sb.append(" "+aliasMapping.get(orderColumns.get(i))+" DESC");
				if ( i < (orderColumns.size()-1) )
					sb.append(",");
    		}
		}
    }
}
