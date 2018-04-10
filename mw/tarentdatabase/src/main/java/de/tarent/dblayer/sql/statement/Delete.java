package de.tarent.dblayer.sql.statement;

import java.sql.SQLException;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.From;
import de.tarent.dblayer.sql.clause.Where;
import java.util.List;
import de.tarent.dblayer.sql.ParamHolder;

/**
 * This {@link Statement} models SQL <code>DELETE</code> statements.
 *
 * @author Wolfgang Klein
 */
public class Delete extends AbstractStatement {

    //
    // protected member variables
    //
    /** {@link From} {@link Clause} listing all tables for the <code>FROM</code> part */
    private final From _fromClause = new From();
    /** {@link Clause} modelling the <code>WHERE</code> part */
    private Clause _whereClause;

    /**
     * {@see ParamHolder#getParams(List)}
     */
    public void getParams(List list) {
        if (_whereClause instanceof ParamHolder)
            ((ParamHolder)_whereClause).getParams(list);
    }

    //
    // public methods
    //
    /** This method adds the parameter name to the tables to delete from. */
	public Delete from(String table) {
		_fromClause.addTable(table);
		return this;
	}

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>DELETE</code> statement.
     */
	public Delete where(Clause clause) {
		_whereClause = clause;
		return this;
	}

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>UPDATE</code> statement to "(current where clause) AND
     * (additional clause)", or to "additional clause" if the current where clause
     * is <code>null</code>.
     */
	public Delete whereAnd(Clause additionalClause) {
        if (_whereClause == null)
            where(additionalClause);
        else
            where(Where.and(_whereClause, additionalClause));
        return this;
	}

    /**
     * This method adds an equals expression to the current where list, connected by an ANT operator.
     * It is the same as .whereAnd(Expr.equal(columnName, value))
     */
	public Delete whereAndEq(String columnName, Object value) {
        whereAnd(Expr.equal(columnName, value));
        return this;
	}

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>UPDATE</code> statement to "(current where clause) OR
     * (additional clause)", or to "additional clause" if the current where clause
     * is <code>null</code>.
     */
	public Delete whereOr(Clause additionalClause) {
        if (_whereClause == null)
            where(additionalClause);
        else
            where(Where.or(_whereClause, additionalClause));
        return this;
	}

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>DELETE</code> statement to equality of the given column
     * and the given value.
     */
	public Delete byId(String column, Object value) {
	    _whereClause = new Where(column, value, Expr.EQUAL);
	    return this;
	}

    /**
     * This method executes the modelled <code>DELETE</code> statement within the
     * db layer pool with the given name and returns the {@link Delete} itself.
     *
     * @param pool the connection pool in which to operate.
     */
    public Delete executeDelete(String pool) throws SQLException {
        DB.update(pool, this);
        return this;
    }

    /**
     * This method executes the modelled <code>DELETE</code> statement within the
     * db layer pool with the given name and returns the {@link Delete} itself.
     *
     * @param dbx the DBContext on which to operate
     */
    public Delete executeDelete(DBContext dbx) throws SQLException {
        DB.update(dbx, this);
        return this;
    }

    //
    // interface {@link Statement}
    //
    /**
     * This method executes the modelled <code>DELETE</code> statement within the
     * {@link DBContext} of this {@link Delete} instance.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.<br>
     * This method actually calls {@link #executeDelete(String)} using the pool name
     * of the {@link DBContext}.
     *
     * @see Statement#execute()
     */
	public Object execute() throws SQLException {
        return executeDelete(getDBContext());
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
	public String statementToString() throws SyntaxErrorException {
		StringBuffer sb = new StringBuffer();
		sb.append(DELETE);
		if (_fromClause != null)
			sb.append(_fromClause.clauseToString(getDBContext()));
		else
		    throw new SyntaxErrorException("A table to delete from is required for a DELETE operation.");
		if (_whereClause != null) {
			sb.append(Where.WHERE);
		    sb.append(_whereClause.clauseToString(getDBContext()));
		}
		return sb.toString();
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
	    try {
            return statementToString();
        } catch (SyntaxErrorException e) {
            return e.toString();
        }
	}
}
