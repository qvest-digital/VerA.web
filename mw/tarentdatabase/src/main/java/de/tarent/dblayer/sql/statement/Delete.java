package de.tarent.dblayer.sql.statement;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
    /**
     * {@link From} {@link Clause} listing all tables for the <code>FROM</code> part
     */
    private final From _fromClause = new From();
    /**
     * {@link Clause} modelling the <code>WHERE</code> part
     */
    private Clause _whereClause;

    /**
     * {@see ParamHolder#getParams(List)}
     */
    public void getParams(List list) {
        if (_whereClause instanceof ParamHolder) {
            ((ParamHolder) _whereClause).getParams(list);
        }
    }

    //
    // public methods
    //

    /**
     * This method adds the parameter name to the tables to delete from.
     */
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
        if (_whereClause == null) {
            where(additionalClause);
        } else {
            where(Where.and(_whereClause, additionalClause));
        }
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
        if (_whereClause == null) {
            where(additionalClause);
        } else {
            where(Where.or(_whereClause, additionalClause));
        }
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
        if (_fromClause != null) {
            sb.append(_fromClause.clauseToString(getDBContext()));
        } else {
            throw new SyntaxErrorException("A table to delete from is required for a DELETE operation.");
        }
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
