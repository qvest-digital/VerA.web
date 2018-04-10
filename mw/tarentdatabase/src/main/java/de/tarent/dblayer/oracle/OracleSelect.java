/*
 * $Id: OracleSelect.java,v 1.3 2007/06/14 14:51:57 dgoema Exp $
 */
package de.tarent.dblayer.oracle;

import java.util.Iterator;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.statement.Select;

/**
 * Oracle specific extension of the {@link Select} {@link Statement}.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class OracleSelect extends Select {
    //
    // constructors
    //
    /**
     * This constructor sets the distinctness flag of this <code>SELECT</code>
     * statement.
     *
     * @see Select#Select(boolean)
     */
    public OracleSelect(boolean distinct) {
        super(distinct);
    }

    //
    // interface {@link Statement}
    //
    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.<br>
     * This Oracle specific version serializes the {@link Limit} using
     * <code>rownum</code> magic as there is no better way to do this.
     * As a side effect the alias <code>dbLayerRownumber</code> must
     * not be used as a name alias of a selected column when using a
     * limit or offset.
     *
     * @see Select#statementToString()
     * @see Statement#statementToString()
     */
	public String statementToString() throws SyntaxErrorException {
        String baseStatement = super.statementToString();
        if (getLimit() == null)
            return baseStatement;

        // emulate the limit and offset using a filter on the oracle rownum
        StringBuffer sb = new StringBuffer(baseStatement.length() + 100);
        sb.append("SELECT ");
        addColumnAliasList(sb);
        sb.append(" FROM (");
        sb.append(" SELECT rownum as dbLayerRownumber,");
        addColumnAliasList(sb);
        sb.append(" FROM (");
        sb.append(baseStatement);
        sb.append(") WHERE rownum <= ");
        sb.append(getLimit().getOffset() + getLimit().getLimit());
        sb.append(") WHERE dbLayerRownumber > ");
        sb.append(getLimit().getOffset());

        return sb.toString();
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
