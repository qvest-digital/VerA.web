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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.InsertKeys;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.resource.Resources;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;

/**
 * This {@link Statement} models SQL <code>INSERT</code> statements.
 *
 * @author Wolfgang Klein
 */
public class Insert extends AbstractStatement {

    //
    // protected constants
    //
    /**
     * the String "<code>INSERT INTO </code>"
     */
    final static private String INSERT = "INSERT INTO ";
    /**
     * the String "<code> VALUES </code>"
     */
    final static private String VALUES = " VALUES ";

    //
    // protected member variables
    //
    /**
     * the name of the table to insert into
     */
    private String _table = null;
    /**
     * the list of column names ({@link String}) having values to insert
     */
    private final List _columns;
    /**
     * the list of value objects in sync with {@link #_columns}
     */
    private final List _values;
    /**
     * Names of the columns, which should be returned in the getGeneratedKeys() method
     */
    private List returnKeyColumns;

    //
    // constructors
    //

    /**
     * This constructor creates a simple {@link Insert} {@link Statement}.
     */
    public Insert() {
        _columns = new ArrayList();
        _values = new ArrayList();
    }

    /**
     * This constructor creates an {@link Insert} {@link Statement} initialized
     * for the given number of values to insert.
     */
    public Insert(int initialCapacity) {
        _columns = new ArrayList(initialCapacity);
        _values = new ArrayList(initialCapacity);
    }

    /**
     * Overides an sets get autogenerated keys to true.
     *
     * @param dbContext The database context in which the statenemt exists.
     */
    public ExtPreparedStatement prepare(DBContext dbContext) throws SQLException {
        ExtPreparedStatement stmt = new ExtPreparedStatement(this);
        if (returnKeyColumns != null && returnKeyColumns.size() > 0) {
            stmt.prepare(dbContext, getReturnKeyColumns());
        } else {
            stmt.prepare(dbContext, true);
        }
        return stmt;
    }

    /**
     * {@see ParamHolder#getParams(List)}
     */
    public void getParams(List paramList) {
        addParams(paramList, _values);
    }

    //
    // public methods
    //

    /**
     * This method sets the name of the table to insert into.
     */
    public Insert table(String table) {
        _table = table;
        return this;
    }

    /**
     * Adds a key for returning in getGeneratedKeys() method
     */
    public void addReturnKeyColumn(String keyColumn) {
        int pos = keyColumn.indexOf(".");
        if (pos != -1) {
            keyColumn = keyColumn.substring(pos + 1);
        }
        if (returnKeyColumns == null) {
            returnKeyColumns = new ArrayList(1);
        }
        returnKeyColumns.add(keyColumn);
    }

    /**
     * Returns the key names for returning in getGeneratedKeys() method
     */
    public String[] getReturnKeyColumns() {
        return (String[]) returnKeyColumns.toArray(new String[returnKeyColumns.size()]);
    }

    /**
     * This method sets the value for the given column of the data record to insert.<br>
     * Actually this method only appends the pair (column, value) to a list and
     * doesn't check whether there already is a pair for the same column in the list.
     * Your code will have to keep an eye on this itself.
     *
     * @param column name of the column of the value; only the local part is used
     * @param value  value to insert; this value will be formatted fitting its type
     *               and the {@link DBContext} this {@link Insert} is operated in.
     * @return this {@link Insert} instance
     * @see #remove(String)
     */
    public Insert insert(String column, Object value) {
        _columns.add(column.substring(column.lastIndexOf('.') + 1));
        _values.add(value);
        return this;
    }

    /**
     * This method removes the value of the given column from the collection of
     * values to insert.<br>
     * Actually this method removes only the first occurance of the column therein.
     * Generally this should be <em>the</em> occurance but as already mentioned
     * {@link #insert(String, Object) above} {@link Insert} doesn't guarantee it
     * keeps at most one value per column.
     *
     * @param column the column the value assignment of which is to be removed
     * @return the value until now assigned to the column
     * @see #insert(String, Object)
     */
    public Object remove(String column) {
        int index = _columns.indexOf(column);
        if (index != -1) {
            _columns.remove(index);
            return _values.remove(index);
        }
        return null;
    }

    /**
     * This method returns the number of (column, value) pairs currently set for insertion.
     */
    public int size() {
        return _columns.size();
    }

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * db layer pool with the given name and returns a Map containing the generated keys
     * or the Key "updateCount" with value as Number of affected Rows.
     *
     * @param dbx the DBContext on which to operate.
     */
    public Insert executeInsert(DBContext dbx) throws SQLException {
        DB.insert(dbx, this);
        return this;
    }

    public InsertKeys executeInsertKeys(DBContext dbx) throws SQLException {
        return DB.insertKeys(dbx, this);
    }

    public InsertKeys executeInsertKeys(String poolname) throws SQLException {
        return DB.insertKeys(poolname, this);
    }

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * db layer pool with the given name and returns a Map containing the generated keys
     * or the Key "updateCount" with value as Number of affected Rows.
     *
     * @param pool the connection pool in which to operate.
     */
    public Insert executeInsert(String pool) throws SQLException {
        DB.insert(pool, this);
        return this;
    }

    //
    // interface {@link Statement}
    //

    /**
     * This method executes the modelled <code>INSERT</code> statement within the
     * {@link DBContext} of this {@link Insert} instance.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.<br>
     * This method actually calls {@link #executeInsert(String)} using the pool name
     * of the {@link DBContext}.
     *
     * @see Statement#execute()
     */
    public Object execute() throws SQLException {
        if (getDBContext() == null) {
            throw new IllegalStateException(Resources.getInstance().get("ERROR_NO_DBCONTEXT_SET"));
        }
        return executeInsert(getDBContext());
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
        if (_table == null) {
            throw new SyntaxErrorException("A table to insert into is required for an INSERT operation.");
        }
        if (_values.size() == 0) {
            throw new SyntaxErrorException("At least one value to insert is required for an INSERT operation.");
        }

        StringBuffer sb = new StringBuffer();
        sb.append(INSERT).append(_table).append(" (");

        for (Iterator it = _columns.iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(',');
            }
        }

        sb.append(')').append(VALUES).append('(');

        for (Iterator it = _values.iterator(); it.hasNext(); ) {
            sb.append(SQL.format(getDBContext(), it.next()));
            if (it.hasNext()) {
                sb.append(',');
            }
        }

        sb.append(')');
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
