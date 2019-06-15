package de.tarent.dblayer.sql.statement;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.engine.ResultProcessor;
import de.tarent.dblayer.engine.ResultSetReader;
import de.tarent.dblayer.engine.SetDbContext;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.ParamHolder;
import de.tarent.dblayer.sql.SQLStatementException;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.From;
import de.tarent.dblayer.sql.clause.GroupBy;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This {@link Statement} models SQL <code>SELECT</code> statements.
 *
 * @author Wolfgang Klein
 */
public class Select extends AbstractStatement implements Clause, Cloneable {
    //
    // member variables
    //
    /**
     * flag whether the results should be distinct
     */
    boolean _distinct;
    /**
     * The DISTINCT ON columns
     */
    String distinctOn = null;
    /**
     * list of the columns requested using the <code>select*</code> methods
     */
    ArrayList _selectColumns = new ArrayList();
    /**
     * list of column descriptions requested using the {@link #add(String, Class)} method
     */
    ArrayList _listColumns = new ArrayList();
    /**
     * list of all column (alias) names
     */
    ArrayList _columnAliasList = new ArrayList();
    /**
     * {@link From} {@link Clause} listing all tables for the <code>FROM</code> part
     */
    From _fromClause = new From();
    /**
     * list of all joins requested using the <code>join*</code> methods
     */
    ArrayList _joins = new ArrayList();
    /**
     * list of all unions requested using Union
     */
    ArrayList _unions = new ArrayList();
    /**
     * {@link Clause} modelling the <code>WHERE</code> part
     */
    Clause _whereClause;
    /**
     * {@link Clause} modelling the <code>ORDER</code> part
     */
    Order _orderClause;
    /**
     * {@link Clause} modelling the <code>GROUP BY</code> part
     */
    GroupBy _groupByClause;
    /**
     * {@link Clause} modelling the <code>LIMIT</code> and <code>OFFSET</code> parts
     */
    Limit _limitClause;
    /**
     * A column of the result set, which is unique.
     * Currently this is only used from the MS-SQL Select implementation
     * to support a LIMIT,OFFSET workaroud.
     */
    String uniqueColumn;

    //
    // constructors
    //

    /**
     * This constructor sets the distinctness flag of this <code>SELECT</code>
     * statement.
     */
    public Select(boolean distinct) {
        _distinct = distinct;
    }

    /**
     * Appends the parameters of the paramHolder to the supplied list.
     * The order of the params is determined by the order of appearance
     * of the params in the holder object.
     *
     * @param list A list to take up ParamValue ebjects.
     * @see ParamHolder#getParams(List)
     */
    public void getParams(List list) {
        if (_whereClause instanceof ParamHolder) {
            ((ParamHolder) _whereClause).getParams(list);
        }
    }

    //
    // public methods
    //

    // * columns to select

    /**
     * This method adds the parameter column name to the list of columns to
     * select and adds its local part to the list of the column alias names
     * of this statement.
     *
     * @param column name of the column to select
     * @return this {@link Select} {@link Statement}
     */
    public Select select(String column) {
        _selectColumns.add(column);
        String alias = column;
        int index = alias.indexOf(".");
        if (index >= 0) {
            alias = alias.substring(index + 1);
        }
        _columnAliasList.add(alias);
        return this;
    }

    /**
     * This method adds the parameter column name to the list of columns to
     * select using the identical name as an alias and adds it also to the
     * list of column alias names of this statement.
     *
     * @param column name and alias of the column to select
     * @return this {@link Select} {@link Statement}
     */
    public Select selectAs(String column) {
        _selectColumns.add(column + " AS " + "\"" + column + "\"");
        _columnAliasList.add("\"" + column + "\"");
        return this;
    }

    /**
     * This method adds the parameter column name to the list of columns to
     * select using the parameter alias name as an alias and adds it to the
     * list of column alias names of this statement.
     *
     * @param column name of the column to select
     * @param nameas alias of the column to select
     * @return this {@link Select} {@link Statement}
     */
    public Select selectAs(String column, String nameas) {
        _selectColumns.add(column + " AS " + "\"" + nameas + "\"");
        _columnAliasList.add("\"" + nameas + "\"");
        return this;
    }

    /**
     * This method adds the parameter column name together with the parameter
     * type to the list of columns to select and list using the identical name
     * as an alias and adds it also to the list of column alias names of this
     * statement.
     *
     * @param column name of the column to select and list
     * @param type   class as which to list the column
     * @return this {@link Select} {@link Statement}
     * @see #getList(String)
     */
    public Select add(String column, Class type) {
        _listColumns.add(
          new Object[] {
            column,
            column + " AS \"" + column + "\"",
            type }
        );
        _columnAliasList.add(column);
        return this;
    }

    /**
     * This method clears the list of selected columns and their aliases.
     */
    public void clearColumnSelection() {
        _selectColumns.clear();
        _listColumns.clear();
        _columnAliasList.clear();
    }

    /**
     * This method change the behavior of the select. If the given parameter
     * is true this select will be use an <code>SELECT DISTINCT</code> for
     * catch data, <code>SELECT</code> otherwise.
     *
     * @param newValue
     */
    public Select setDistinct(boolean newValue) {
        _distinct = newValue;
        return this;
    }

    /**
     * This methods sets the distinct funtionality for one or more rows.
     * Attention: This is an DB-dependent feature, known from PostgreSQL.
     * Make sure that the database layer has implemented a workaround
     * for your target database system.
     */
    public Select setDistinctOn(String newValue) {
        setDistinct(true);
        distinctOn = newValue;
        return this;
    }

    /**
     * This method returns the list of alias names of the selected columns.
     */
    public List<String> getColumnAliasList() {
        return _columnAliasList;
    }

    /**
     * This method returns the list of the selected columns.
     */
    public List<String> getSelectColumns() {
        return _selectColumns;
    }

    // * tables from which to select

    /**
     * This method adds the parameter name to the tables to select from.
     */
    public Select from(String table) {
        _fromClause.addTable(table);
        return this;
    }

    /**
     * This method adds the parameter name to the tables to select from
     * and a label for this table.
     */
    public Select from(String table, String label) {
        _fromClause.addTable(table, label);
        return this;
    }

    /**
     * This method adds the given join to the table joins from which to select.
     */
    public Select join(Join join) {
        _joins.add(join);
        return this;
    }

    /**
     * This method adds the inner join of given table on the given columns
     * to the table joins from which to select.
     */
    public Select join(String table, String leftColumn, String rightColumn) {
        _joins.add(new Join(Join.INNER, table, leftColumn, rightColumn));
        return this;
    }

    /**
     * This method adds the left outer join of given table on the given columns
     * to the table joins from which to select.
     */
    public Select joinLeftOuter(String table, String leftColumn, String rightColumn) {
        _joins.add(new Join(Join.LEFT_OUTER, table, leftColumn, rightColumn));
        return this;
    }

    /**
     * This method adds the right outer join of given table on the given columns
     * to the table joins from which to select.
     */
    public Select joinRightOuter(String table, String leftColumn, String rightColumn) {
        _joins.add(new Join(Join.RIGHT_OUTER, table, leftColumn, rightColumn));
        return this;
    }

    /**
     * This method adds the outer join of given table on the given columns
     * to the table joins from which to select.
     */
    public Select joinOuter(String table, String leftColumn, String rightColumn) {
        _joins.add(new Join(Join.OUTER, table, leftColumn, rightColumn));
        return this;
    }

    //* Unions

    /**
     * This Method adds a union-Statement.
     * Regardless of which columns are selected, the select-columns will be overridden
     * by the ones from this select.
     *
     * @param select - Select to be included in _union
     * @throws SQLStatementException
     */
    public Select union(Select select) throws SQLStatementException {
        if (!_columnAliasList.equals(_columnAliasList)) {
            throw new SQLStatementException(null, "column aliases have to be identical!");
        }
        _unions.add(select);
        return this;
    }

    // * conditions on the data records to select

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>SELECT</code> statement.
     */
    public Select where(Clause clause) {
        _whereClause = clause;
        return this;
    }

    /**
     * This method returns the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>SELECT</code> statement.
     */
    public Clause getWhere() {
        return _whereClause;
    }

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>SELECT</code> statement to "(current where clause) AND
     * (additional clause)", or to "additional clause" if the current where clause
     * is <code>null</code>.
     */
    public Select whereAnd(Clause additionalClause) {
        if (_whereClause == null) {
            where(additionalClause);
        } else {
            where(Where.and(_whereClause, additionalClause));
        }
        return this;
    }

    /**
     * This method adds an equals expression to the current where list,
     * connected by an AND operator.
     * It is the same as .whereAnd(Expr.equal(columnName, value))
     */
    public Select whereAndEq(String columnName, Object value) {
        whereAnd(Expr.equal(columnName, value));
        return this;
    }

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>SELECT</code> statement to "(current where clause) OR
     * (additional clause)", or to "additional clause" if the current where clause
     * is <code>null</code>.
     */
    public Select whereOr(Clause additionalClause) {
        if (_whereClause == null) {
            where(additionalClause);
        } else {
            where(Where.or(_whereClause, additionalClause));
        }
        return this;
    }

    /**
     * This method sets the condition {@link Clause} for the <code>WHERE</code>
     * part of the <code>SELECT</code> statement to equality of the given column
     * and the given value.
     */
    public Select byId(String column, Object value) {
        _whereClause = new Where(column, value, Expr.EQUAL);
        return this;
    }

    // * additional parameters for the data records to select

    /**
     * This method sets the {@link Order} for the <code>SELECT</code> statement.
     */
    public Select orderBy(Order order) {
        _orderClause = order;
        return this;
    }

    /**
     * This method adds an {@link Order} to the existing orders for the <code>SELECT</code> statement.
     */
    public Select addOrderBy(Order order) {
        if (_orderClause == null || _orderClause.getColumns().size() == 0) {
            _orderClause = order;
        } else {
            List sortDirections = order.getSortDirections();

            Iterator directionsIterator = sortDirections.iterator();
            for (Iterator columnsIterator = order.getColumns().iterator(); columnsIterator.hasNext(); ) {

                Boolean sortDirection = (Boolean) directionsIterator.next();
                if (sortDirection.booleanValue()) {
                    _orderClause.andAsc(columnsIterator.next().toString());
                } else {
                    _orderClause.andDesc(columnsIterator.next().toString());
                }
            }
        }
        return this;
    }

    /**
     * This method sets the {@link GroupBy} for the <code>SELECT</code> statement.
     */
    public Select groupBy(GroupBy groupBy) {
        _groupByClause = groupBy;
        return this;
    }

    /**
     * This method sets the {@link Limit} for the <code>SELECT</code> statement.
     */
    public Select Limit(Limit limit) {
        _limitClause = limit;
        return this;
    }

    /**
     * This method returns the {@link Limit} for the <code>SELECT</code> statement.
     */
    public Limit getLimit() {
        return _limitClause;
    }

    /**
     * Returns a column of the result set which is unique.
     * Currently this is only used from the MS-SQL Select implementation
     * to support a LIMIT,OFFSET workaroud.
     */
    public String getUniqueColumn() {
        return uniqueColumn;
    }

    /**
     * Sets a column of the result set which is unique.
     * Currently this is only used from the MS-SQL Select implementation
     * to support a LIMIT,OFFSET workaroud.
     */
    public void setUniqueColumn(String newUniqueColumn) {
        this.uniqueColumn = newUniqueColumn;
    }

    /**
     * Returns the ORDER BY clause
     */
    public Order getOrderClause() {
        return _orderClause;
    }

    // * executing the select

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * db layer pool with the given name and returns a {@link Result}.
     *
     * @param pool the connection pool in which to operate.
     */
    public Result executeSelect(String pool) throws SQLException {
        return DB.result(pool, this);
    }

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * db layer pool with the given name and returns a {@link Result}.
     *
     * @param dbx the DBContext on which to operate.
     */
    public Result executeSelect(DBContext dbx) throws SQLException {
        return DB.result(dbx, this);
    }

    /**
     * Iterates over the result set and calles the process method for each row.
     * Afterwards, the result set will be closed.
     * For the iteration, the Result class will be used.
     *
     * For execution, the previous set DBContext will be used.
     *
     * @return the number of iterations
     * @throws IllegalStateException if no DBContext was set.
     */
    public int iterate(ResultProcessor processor) throws SQLException {
        if (getDBContext() == null) {
            throw new IllegalStateException("No DBContext was set on the statement");
        }
        return executeSelect(getDBContext()).iterate(processor);
    }

    /**
     * Iterates over the result set and calles the process method for each row.
     * Afterwards, the result set will be closed.
     * For the iteration, the Result class will be used.
     *
     * For execution, the supplied DBContext will be used.
     *
     * @return the number of iterations
     */
    public int iterate(DBContext dbc, ResultProcessor processor) throws SQLException {
        return executeSelect(dbc).iterate(processor);
    }

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * db layer pool with the given name and returns a {@link ResultSet}.
     *
     * @param dbx the DBContext on which to operate.
     */
    public ResultSet getResultSet(DBContext dbx) throws SQLException {
        return DB.getResultSet(dbx, this);
    }

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * db layer pool with the given name and returns a {@link ResultSet}.
     *
     * @param pool the connection pool in which to operate.
     */
    public ResultSet getResultSet(String pool) throws SQLException {
        return DB.getResultSet(pool, this);
    }

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * {@link DBContext} of this {@link Select} instance and returns a {@link ResultSet}.
     *
     * This method actually calls {@link #getResultSet(String)} using the pool name
     * of the {@link DBContext}.
     */
    public ResultSet getResultSet() throws SQLException {
        return getResultSet(getDBContext());
    }

    /**
     * Returns the first cell of the first row as a string,
     * or null if the ResultSet is empty.
     */
    public String getFirstCellAsString() throws SQLException {
        return DB.fetchFirstCellAsString(getDBContext(), statementToString());
    }

    /**
     * Returns the first cell of the first row as a timestamp,
     * or null if the ResultSet is empty.
     */
    public Timestamp getFirstCellAsTimestamp() throws SQLException {
        return DB.fetchFirstCellAsTimestamp(getDBContext(), statementToString());
    }

    /**
     * Returns the first cell of the first row as a integer,
     * or null if the ResultSet is empty.
     */
    public Integer getFirstCellAsInteger() throws SQLException {
        return DB.fetchFirstCellAsInteger(getDBContext(), statementToString());
    }

    /**
     * Checks if the ResultSet is empty.
     *
     * @return <code>true</code> if the ResultSet is empty.
     * @throws SQLException
     */
    public boolean isEmpty() throws SQLException {
        return DB.isEmpty(getDBContext(), statementToString());
    }

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * db layer pool with the given name and returns a {@link List} containing a
     * {@link java.util.Map} for each selected data record which in turn contains
     * an entry for each column requested using the {@link #add(String, Class)}
     * method.
     *
     * @param pool the connection pool in which to operate.
     * @return a {@link List} of the found data records
     */
    public List getList(String pool) throws SQLException {
        Result result = DB.result(pool, this);
        try {
            return ResultSetReader.list(_listColumns, result);
        } finally {
            result.closeAll();
        }
    }

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * db layer pool with the given name and returns a {@link List} containing a
     * {@link java.util.Map} for each selected data record which in turn contains
     * an entry for each column requested using the {@link #add(String, Class)}
     * method.
     *
     * @param dbx the DBContext on which to operate.
     * @return a {@link List} of the found data records
     */
    public List getList(DBContext dbx) throws SQLException {
        Result result = DB.result(dbx, this);
        try {
            return ResultSetReader.list(_listColumns, result);
        } finally {
            result.closeAll();
        }
    }
    //
    // interface {@link Statement}
    //

    /**
     * This method executes the modelled <code>SELECT</code> statement within the
     * {@link DBContext} of this {@link Select} instance and returns a {@link Result}.
     *
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * This method actually calls {@link #executeSelect(String)} using the pool name
     * of the {@link DBContext}.
     *
     * @see de.tarent.dblayer.sql.Statement#execute()
     */
    public Object execute() throws SQLException {
        return executeSelect(getDBContext());
    }

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement}.
     *
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @see de.tarent.dblayer.sql.Statement#statementToString()
     */
    public String statementToString() throws SyntaxErrorException {
        DBContext dbc = getDBContext();
        StringBuffer sb = new StringBuffer();
        appendSelectPart(sb);
        insertDistinctOnClause(sb);
        appendColumnList(sb);
        appendFromPart(dbc, sb);
        appendWherePart(dbc, sb);
        appendGroupBy(sb);
        appendOrder(dbc, sb);
        appendLimit(dbc, sb);

        if (!_unions.isEmpty()) {
            Iterator it = _unions.iterator();
            while (it.hasNext()) {
                Select select = (Select) it.next();
                sb.append(" UNION ");
                sb.append(select.clauseToString(dbc));
            }
        }
        return sb.toString();
    }

    protected void appendSelectPart(StringBuffer sb) {
        sb.append(_distinct ? SELECTDISTINCT : SELECT);
    }

    /**
     * Append the from an join part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendFromPart(DBContext dbc, StringBuffer sb) {
        if (_fromClause.size() > 0) {
            sb.append(_fromClause.clauseToString(dbc));
        }
        for (Iterator it = _joins.iterator(); it.hasNext(); ) {
            Join join = (Join) it.next();
            join.setDBContext(dbc);
            sb.append(join);
            sb.append(" ");
        }
    }

    /**
     * Append the where part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendWherePart(DBContext dbc, StringBuffer sb) {
        if (_whereClause != null) {
            String clauseString = _whereClause.clauseToString(dbc);
            if (clauseString.length() != 0) {
                sb.append(Where.WHERE);
                sb.append(" (");
                sb.append(clauseString);
                sb.append(") ");
            }
        }
    }

    protected boolean isWhereEmpty(DBContext dbc) {
        return (_whereClause == null || _whereClause.clauseToString(dbc).length() == 0);
    }

    /**
     * Append the group by part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendGroupBy(StringBuffer sb) {
        if (_groupByClause != null) {
            _groupByClause.clauseToString(sb); // column (alias) names only --> context insensitive
        }
    }

    /**
     * Append the order part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendOrder(DBContext dbc, StringBuffer sb) {
        if (_orderClause != null) {
            _orderClause.setDBContext(dbc);
            _orderClause.clauseToString(sb);
        }
    }

    /**
     * Append the limit, offset and the end of the statement part to the statement.
     * This is extracted into a method for overidding
     */
    protected void appendLimit(DBContext dbc, StringBuffer sb) {
        appendLimitStatement(sb);
    }

    //
    // protected helper methods
    //

    /**
     * This method appends a <code>LIMIT</code> and/or an <code>OFFSET</code>
     * part of the <code>SELECT</code> statement to the given {@link StringBuffer}.
     */
    protected void appendLimitStatement(StringBuffer sb) {
        if (_limitClause != null) {
            _limitClause.setDBContext(getDBContext());
            _limitClause.clauseToString(sb);
        }
    }

    /**
     * Insert the database dependent code ON-Clause for DISTINCT ON
     */
    protected void insertDistinctOnClause(StringBuffer sb) {
        if (distinctOn != null) {
            sb.append("ON (");
            if (_orderClause != null) {
                for (Iterator iter = _orderClause.getColumns().iterator(); iter.hasNext(); ) {
                    sb.append(iter.next());
                    sb.append(", ");
                }
            }

            sb.append(distinctOn).append(") ");
        }
    }

    /**
     * This method appends the columns to select in the <code>SELECT</code> statement
     * to the given {@link StringBuffer}.
     */
    protected void appendColumnList(StringBuffer sb)
      throws SyntaxErrorException {

        if (_selectColumns.isEmpty() && _listColumns.isEmpty()) {
            throw new SyntaxErrorException("Es muss mindestens eine Spalte selektiert werden.");
        }

        for (Iterator it = _selectColumns.iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        for (Iterator it = _listColumns.iterator(); it.hasNext(); ) {
            sb.append(((Object[]) it.next())[1]);
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
    }

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

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement} using the method {@link #statementToString()}.
     * As this statement is seen as a {@link Clause} here the statement is enclosed
     * in round brackets.<br>
     * This method should only be used after setting the {@link DBContext}
     * using the {@link SetDbContext#setDBContext(DBContext)} method.
     * Otherwise a default db layer context is assumed which for now is
     * a PostgresQL DBMS.
     *
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString()
     * @deprecated use {@link #clauseToString(DBContext)} instead
     */
    public String clauseToString() {
        return clauseToString(getDBContext());
    }

    /**
     * This method creates the {@link DBContext} sensitive {@link String} representation
     * of the modelled SQL {@link Statement} using the method {@link #statementToString()}.
     * As this statement is seen as a {@link Clause} here the statement is enclosed
     * in round brackets.<br>
     * This method as a side effect changes the {@link DBContext} of this
     * {@link Clause} to the given one.<br>
     * TODO: This method should be able to throw qualified exceptions
     *
     * @param dbContext the db layer context to use for formatting parameters
     * @return string representation of the clause model
     * @see de.tarent.dblayer.sql.clause.Clause#clauseToString(de.tarent.dblayer.engine.DBContext)
     */
    public String clauseToString(DBContext dbContext) {
        setDBContext(dbContext);
        return new StringBuffer()
          .append("(")
          .append(toString())
          .append(")").toString();
    }

    /**
     * Returns an independent clone of this statement.
     *
     * Use this method with care: Because of the complex structure of a complete SELECT
     * statement there may be situations where the both objects reference the same object.
     * This is the case for the object values in the Clauses Where, Operator and ParamValue.
     *
     * If, for example, a select <code>s1</code> has a where clause with a reference to a
     * mutable object as value (e.g. a String Buffer), then a clone
     * <code>s2 = s1.clone()</code> will have a reference to same object.
     * In this example a modification to the StringBuffer will modify the where clause of
     * <code>s1</code> as well as <code>s2</code>.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Select theClone = (Select) super.clone();
            theClone._selectColumns = (ArrayList) _selectColumns.clone();
            theClone._listColumns = (ArrayList) _listColumns.clone();
            theClone._columnAliasList = (ArrayList) _columnAliasList.clone();
            theClone._joins = (ArrayList) _joins.clone();
            if (_fromClause != null) {
                theClone._fromClause = (From) _fromClause.clone();
            }
            if (_whereClause != null) {
                theClone._whereClause = (Clause) _whereClause.clone();
            }
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
