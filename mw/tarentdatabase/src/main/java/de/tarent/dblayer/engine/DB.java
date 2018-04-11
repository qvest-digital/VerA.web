package de.tarent.dblayer.engine;

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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.resource.Resources;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SQLStatementException;
import de.tarent.dblayer.sql.statement.Insert;

/*
 * 2009-10-04 cklein, tarent-database-1.5.4
 *
 * This version incorporates temporary fixes to a major problem where underlying
 * resultsets will be prematurely closed when OCTOPUS makes the transition from
 * controller execution to view rendering.
 *
 * In order to fix this, all close() Statements on either the pool or the underlying
 * statements or resultsets have been disabled. This should result in no problem
 * whatsoever, since after GC of the controller and the view all of the resultset
 * instances should be GC'ed, too.
 */

/**
 * This class ist is responsible for maniging the database pools and executing statements.
 *
 * @author Wolfgang Klein, tarent GmbH  2002-2004
 * @author Christoph Jerolimov, tarent GmbH
 * @author Sebastian Mancke, tarent GmbH
 * @author Robert Linden (r.linden@tarent.de)
 */
public class DB {

    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(DB.class);

    /**
     * The pools
     */
    static private Map pools = new HashMap();

    /**
     * Configures a new Pool
     *
     * @param poolname String identifier for the pool
     * @param info     Property Set for configuring the pool, see {@link de.tarent.dblayer.engine.Pool#Pool(Map)} for details.
     * @throws RuntimeException if an error occures
     */
    static public void openPool(String poolname, Map info) {
        if (hasPool(poolname)) {
            return;
        }
        Pool pool = new DBPool(info);
        if (info.containsKey(Pool.DEBUG_POOL_CLASS)) {
            try {
                pool = (Pool) Class.forName((String) info.get(Pool.DEBUG_POOL_CLASS))
                        .getConstructor(new Class[] { Pool.class })
                        .newInstance(new Object[] { pool });
            } catch (Exception e) {
                logger.error(Resources.getInstance().get("ERROR_INIT_POOL_PROXY", (String) info.get(Pool.DEBUG_POOL_CLASS)), e);
            }
        }

        /* check if the target-db-type is really defined in order to avoid problems on db-specific functions */
        Object dbType = info.get(Pool.TARGET_DB);
        if (dbType != null) {
            if (!(dbType.equals(Pool.DB_POSTGRESQL) || dbType.equals(Pool.DB_MSSQL) || dbType.equals(Pool.DB_ORACLE))) {
                throw new RuntimeException("The target DB-type \"" + dbType + "\" is unknown!");
            }
        } else {
            throw new RuntimeException("No target DB-type has been defined!");
        }

        pool.init();
        pools.put(poolname, pool);
    }

    /**
     * Closes and removes the pool
     *
     * @return returns true, if a pool with the given name existed, false otherwise
     */
    static public boolean closePool(String poolname) {
        if (!hasPool(poolname)) {
            return false;
        }
        Pool pool = (Pool) pools.remove(poolname);
        pool.close();
        return true;
    }

    /**
     * Tests, if the pool exists
     */
    static public boolean hasPool(String poolname) {
        if (poolname == null) {
            return false; //hashMap permits null
        }
        return pools.containsKey(poolname);
    }

    /**
     * Returns the pool for the specified poolname or null, if no such pool is exists
     */
    static public Pool getPool(String poolname) {
        if (poolname == null) {
            return null; //hashMap permits null
        }
        return (Pool) pools.get(poolname);
    }

    /**
     * Returns a default implementation for the db execution context of the supplied pool.
     */
    static public DBContext getDefaultContext(String poolname) {
        DBContextImpl dbc = new DBContextImpl();
        dbc.setPoolName(poolname);
        return dbc;
    }

    /**
     * Returns a java.sql.Connection for a given DBContext
     */
    static public Connection getConnection(DBContext dbx) throws SQLException {
        return dbx.getDefaultConnection();
    }

    /**
     * Returns a java.sql.Connection for a given pool
     */
    static public Connection getConnection(String poolname) throws SQLException {
        Pool pool = getPool(poolname);
        if (pool == null) {
            throw new SQLException(Resources.getInstance().get("ERROR_NO_POOL", poolname));
        }
        return pool.getConnection();
    }

    static public Statement getStatement(String poolname) throws SQLException {
        return getStatement(getDefaultContext(poolname));
    }

    /**
     * Returns a Statement from a specific @link {@link DBContext}s default connection
     *
     * @param dbx DBContext to retrieve default connection
     * @return Statement
     * @throws SQLException
     */
    static public Statement getStatement(DBContext dbx) throws SQLException {
        if (SQL.isMSSQL(dbx)) {
            return dbx.getDefaultConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } else {
            return dbx.getDefaultConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }
    }

    /**
     * Returns a ResultSet for the Statement and logs it if this is enabled.
     */
    static public ResultSet getResultSet(DBContext dbx, String sql) throws SQLException {
        try {
            Log.logStatement(sql);
            return getStatement(dbx).executeQuery(sql);
        } catch (SQLException e) {
            throw new SQLStatementException(e, sql);
        }
    }

    /**
     * Returns a ResultSet for the Statement and logs it if this is enabled.
     */
    static public ResultSet getResultSet(String poolname, String sql) throws SQLException {
        try {
            Log.logStatement(sql);
            return getStatement(poolname).executeQuery(sql);
        } catch (SQLException e) {
            throw new SQLStatementException(e, sql);
        }
    }

    /**
     * Returns a ResultSet for the Statement and logs it if this is enabled.
     */
    final static public ResultSet getResultSet(DBContext dbx, de.tarent.dblayer.sql.Statement sql) throws SQLException {
        return getResultSet(dbx, sql.statementToString());
    }

    /**
     * Returns a ResultSet for the Statement and logs it if this is enabled.
     */
    static public ResultSet getResultSet(String poolname, de.tarent.dblayer.sql.Statement sql) throws SQLException {
        return getResultSet(poolname, sql.statementToString());
    }

    /**
     * Returns a ResultSet for the Statement contained in the file pointed
     * to by the SQLFile.
     */
    static public ResultSet getResultSet(String poolname, SQLFile sqlfile)
            throws SQLException {
        String sql = "";
        try {
            sql = SQLCache.getSQLFromFile(poolname, sqlfile);
            Log.logStatement(sql);
            return getStatement(poolname).executeQuery(sql);
        } catch (SQLException ex) {
            throw new SQLStatementException(ex, sql);
        }
    }

    /**
     * Returns a ResultSet for the Statement contained in the file pointed
     * to by the SQLFile.
     */
    static public ResultSet getResultSet(DBContext dbx, SQLFile sqlfile)
            throws SQLException {
        String sql = "";
        try {
            sql = SQLCache.getSQLFromFile(dbx, sqlfile);
            Log.logStatement(sql);
            return getStatement(dbx).executeQuery(sql);
        } catch (SQLException ex) {
            throw new SQLStatementException(ex, sql);
        }
    }

    /**
     * Returns a ResultSet for the Statement contained in the file pointed
     * to by the SQLFile including variable-substitution. A variable is a string
     * enclosed by {}. Please note that no special consideration is given to
     * string values that might contain { or } in the sql or escaped braces or
     * anything like that.
     *
     * @param subst A map of key-value-pairs where each {key} will be
     *              replaced with its value prior to sql-execution. The key
     *              must not contain these {}, they will be added automaticaly.
     */
    static public ResultSet getResultSet(String poolname, SQLFile sqlfile, Map subst)
            throws SQLStatementException {
        String sql = "";
        sql = SQLCache.getSQLFromFile(poolname, sqlfile);
        // replace all defined variables
        // TODO: find out if a StringBuffer would allow for more efficient replacing
        String[] keys = (String[]) subst.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            sql = sql.replaceAll("\\{" + keys[i] + "\\}", (String) subst.get(keys[i]));
            if (logger.isTraceEnabled()) {
                logger.trace("Replacing {" + keys[i] + "} with " + (String) subst.get(keys[i]));
            }
        }
        Log.logStatement(sql);
        try {
            return getStatement(poolname).executeQuery(sql.toString());
        } catch (SQLException ex) {
            throw new SQLStatementException(ex, sql);
        }
    }

    /**
     * Returns a ResultSet for the Statement contained in the file pointed
     * to by the SQLFile including variable-substitution. A variable is a string
     * enclosed by {}. Please note that no special consideration is given to
     * string values that might contain { or } in the sql or escaped braces or
     * anything like that.
     *
     * @param subst A map of key-value-pairs where each {key} will be
     *              replaced with its value prior to sql-execution. The key
     *              must not contain these {}, they will be added automaticaly.
     */
    static public ResultSet getResultSet(DBContext dbx, SQLFile sqlfile, Map subst)
            throws SQLStatementException {
        String sql = "";
        sql = SQLCache.getSQLFromFile(dbx, sqlfile);
        // replace all defined variables
        // TODO: find out if a StringBuffer would allow for more efficient replacing
        String[] keys = (String[]) subst.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            sql = sql.replaceAll("\\{" + keys[i] + "\\}", (String) subst.get(keys[i]));
            if (logger.isTraceEnabled()) {
                logger.trace("Replacing {" + keys[i] + "} with " + (String) subst.get(keys[i]));
            }
        }
        Log.logStatement(sql);
        try {
            return getStatement(dbx).executeQuery(sql.toString());
        } catch (SQLException ex) {
            throw new SQLStatementException(ex, sql);
        }
    }

    /**
     * Returns the first cell of the first row as a string,
     * or null, if the ResultSet is empty.
     *
     * @param poolname  The database Pool
     * @param statement The Statement
     */
    public static String fetchFirstCellAsString(String poolname, String statement) throws SQLException {
        ResultSet res = null;
        try {
            res = DB.getResultSet(poolname, statement);

            if (res.next()) {
                return res.getString(1);
            }
            return null;
        } finally {
            DB.closeAll(res);
        }
    }

    /**
     * Returns the first cell of the first row as a string,
     * or null, if the ResultSet is empty.
     *
     * @param dbx       Database Context
     * @param statement The Statement
     */
    public static String fetchFirstCellAsString(DBContext dbx, String statement) throws SQLException {
        ResultSet res = null;
        try {
            res = DB.getResultSet(dbx, statement);

            if (res.next()) {
                return res.getString(1);
            }
            return null;
        } finally {
            DB.closeStatement(res);
        }
    }

    /**
     * Returns the first cell of the first row as a timestamp,
     * or null, if the ResultSet is empty.
     *
     * @param poolname  The database Pool
     * @param statement The Statement
     */
    public static Timestamp fetchFirstCellAsTimestamp(String poolname, String statement) throws SQLException {
        ResultSet res = null;
        try {
            res = DB.getResultSet(poolname, statement);

            if (res.next()) {
                return res.getTimestamp(1);
            }
            return null;
        } finally {
            DB.closeAll(res);
        }
    }

    /**
     * Returns the first cell of the first row as a timestamp,
     * or null, if the ResultSet is empty.
     *
     * @param dbx       Database Context
     * @param statement The Statement
     */
    public static Timestamp fetchFirstCellAsTimestamp(DBContext dbx, String statement) throws SQLException {
        ResultSet res = null;
        try {
            res = DB.getResultSet(dbx, statement);

            if (res.next()) {
                return res.getTimestamp(1);
            }
            return null;
        } finally {
            DB.closeStatement(res);
        }
    }

    /**
     * Returns the first cell of the first row as an Integer,
     * or null, if the ResultSet is empty.
     *
     * @param poolname  The database Pool
     * @param statement The Statement
     */
    public static Integer fetchFirstCellAsInteger(DBContext dbx, String statement) throws SQLException {
        ResultSet res = null;
        try {
            res = DB.getResultSet(dbx, statement);

            if (res.next()) {
                return new Integer(res.getInt(1));
            }
            return null;
        } finally {
            DB.closeStatement(res);
        }
    }

    /**
     * Returns the first cell of the first row as an Integer,
     * or null, if the ResultSet is empty.
     *
     * @param poolname  The database Pool
     * @param statement The Statement
     */
    public static Integer fetchFirstCellAsInteger(String poolname, String statement) throws SQLException {
        ResultSet res = null;
        try {
            res = DB.getResultSet(poolname, statement);

            if (res.next()) {
                return new Integer(res.getInt(1));
            }
            return null;
        } finally {
            DB.closeAll(res);
        }
    }

    /**
     * Closes the ResultSet if it is not null.
     * On errors no exception is thrown, but logged.
     */
    static public void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.warn(Resources.getInstance().get("ERROR_CLOSING_RESULT_SET"), e);
        }
    }

    /**
     * Closes the Statement if it is not null.
     * On errors no exception is thrown, but logged.
     */
    static public void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.warn(Resources.getInstance().get("ERROR_CLOSING_STATEMENT"), e);
        }
    }

    /**
     * Closes the statement if it is not null and if the resultset is not null.
     * On errors no exception is thrown, but logged.
     *
     * @throws SQLException
     */
    static public void closeStatement(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            close(resultSet.getStatement());
        }
        ;
        ;
    }

    /**
     * Closes the Connection if it is not null.
     * On errors no exception is thrown, but logged.
     */
    static public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                logger.trace("closed connection");
            } else {
                logger.trace("connection was null");
            }
        } catch (SQLException e) {
            logger.warn(Resources.getInstance().get("ERROR_CLOSING_CONNECTION"), e);
        }
    }

    /**
     * Closes the parent Connection of the ResultSet and thereby the ResultSet it self, if the ResultSet is not null.
     * On errors no exception is thrown, but logged.
     */
    static public void closeAll(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                closeAll(resultSet.getStatement());
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.warn(Resources.getInstance().get("ERROR_CLOSING_RESULT_SET"), e);
        }
    }

    /**
     * Closes the parent Connection of the Statement and thereby the Statement it self, if the Statement is not null.
     * On errors no exception is thrown, but logged.
     */
    static public void closeAll(Statement statement) {
        try {
            if (statement != null) {
                //Connection con = statement.getConnection();
                statement.close();
                //close(con);
                logger.trace("closed statement");
            } else {
                logger.trace("statement was null");
            }

        } catch (SQLException e) {
            logger.warn(Resources.getInstance().get("ERROR_CLOSING_STATEMENT"), e);
        }
    }

    public static InsertKeys returnGeneratedKeys(DBContext dbx, Statement stmt, String insert) throws SQLException {
        InsertKeys result = new InsertKeys();
        Connection con = stmt.getConnection();
        if (con.getMetaData()
                .supportsGetGeneratedKeys()) {//checks whether the function getGeneratedKeys is supported by the JDBC driver
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    result.put(md.getTableName(i) + "." + md.getColumnName(i), rs.getObject(i));
                }
            }
            rs.close();
        } else if (SQL.isPostgres(dbx)) {
            DatabaseMetaData dmd = con.getMetaData();
            int into = insert.indexOf("INTO ", 0) + 5;
            int ende = insert.indexOf(" ", into);
            String temp = insert.substring(into, ende);
            String schema = null;
            String table = null;
            int pos = temp.indexOf(".");
            if (pos == -1) {// SERIAL:
                table = temp;
            } else {
                table = temp.substring(pos + 1).toLowerCase(); //Postgres seems to handle only lowercase table + schema
                schema = temp.substring(0, pos).toLowerCase();
            }
            ResultSet rs = dmd.getPrimaryKeys(null, schema, table);
            while (rs.next()) {
                String column_name = rs.getString("column_name");
                String table_name = rs.getString("table_name");
                String schema_name = rs.getString("table_schem");
                String sql = "SELECT currval(pg_get_serial_sequence('" + schema_name + "." + table_name + "','" + column_name +
                        "')) AS value";
                //ResultSet rs2 = stmt.executeQuery(sql);
                //getResultSet(dbx, sql);
                ResultSet rs2 = DB.getResultSet(dbx, sql);
                if (rs2.next()) {
                    result.put(table_name + "." + column_name, new Integer(rs2.getInt("value")));
                }
                rs2.close();
            }
            rs.close();
        } else {
            logger.warn("The DB neither supports 'getGeneratedKeys' nor is an alternative defined");
        }
        return result;
    }

    /**
     * Executes the given Insert on the DBContext, closes the statment afterwards.
     *
     * @param dbx    DBContext
     * @param insert Insert-Statement
     * @return Map with Name of Keys as Keys, value as value
     * @throws SQLException If any Problem apears
     */
    static public InsertKeys insertKeys(DBContext dbx, String insert) throws SQLException {
        return insertKeys(dbx, insert, null);
    }

    /**
     * Executes the given Insert on the DBContext, closes the statment afterwards.
     *
     * @param dbx              DBContext
     * @param insert           Insert-Statement
     * @param returnKeyColumns Fields passed to the JDBC driver for returned keys, may be null
     * @return Map with Name of Keys as Keys, value as value
     * @throws SQLException If any Problem apears
     */
    static public InsertKeys insertKeys(DBContext dbx, String insert, String[] returnKeyColumns) throws SQLException {
        InsertKeys result = new InsertKeys();
        Statement statement = null;
        try {
            Log.logStatement(insert);
            statement = getStatement(dbx);
            boolean supportsGetGeneratedKeys = (!SQL.isPostgres(dbx))
                    && statement.getConnection().getMetaData().supportsGetGeneratedKeys();
            if (!supportsGetGeneratedKeys) {
                statement.executeUpdate(insert);
            } else {
                if (returnKeyColumns != null) {
                    statement.executeUpdate(insert, returnKeyColumns);
                } else {
                    statement.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
                }
            }
            result.putAll(returnGeneratedKeys(dbx, statement, insert));
        } catch (SQLException e) {
            throw new SQLStatementException(e, insert);
        } finally {
            close(statement);
        }
        return result;
    }

    /**
     * Executes the given Insert on the DBContext, closes the statment afterwards.
     *
     * @param dbx    DBContext
     * @param insert Insert-Statement
     * @return Map with Name of Keys as Keys, value as value or the Key updatecount and value is "Number of affected rows" if
     * there are no autogenerated keys.
     * @throws SQLException If any Problem apears
     */
    static public int insert(DBContext dbx, String insert) throws SQLException {
        int result = 0;
        Statement statement = null;
        try {
            Log.logStatement(insert);
            statement = getStatement(dbx);
            result = statement.executeUpdate(insert);
        } catch (SQLException e) {
            throw new SQLStatementException(e, insert);
        } finally {
            close(statement);
        }
        return result;
    }

    /**
     * @see #insertKeys(String, Insert)
     */
    static public InsertKeys insertKeys(String pool, String insert) throws SQLException {
        DBContext dbx = getDefaultContext(pool);
        return insertKeys(dbx, insert);
    }

    /**
     * Executes the given Insert on the Pool, closes the statment afterwards.
     *
     * @param pool   Name of Pool
     * @param insert Insert-Statement
     * @return Map with Name of Keys as Keys, value as value
     * @throws SQLException If any Problem apears
     */
    static public InsertKeys insertKeys(String pool, Insert insert) throws SQLException {
        DBContext dbx = getDefaultContext(pool);
        return insertKeys(dbx, insert);
    }

    /**
     * Executes the given Insert on the Pool, closes the statment afterwards.
     *
     * @param pool   Name of Pool
     * @param insert Insert-Statement
     * @return Map with Name of Keys as Keys, value as value or the Key updatecount and value is "Number of affected rows" if
     * there are no autogenerated keys.
     * @throws SQLException If any Problem apears
     */
    static public int insert(String pool, String insert) throws SQLException {
        DBContext dbx = getDefaultContext(pool);
        return insert(dbx, insert);
    }

    /**
     * Executes the given Insert on the DBContext, closes the statment afterwards.
     *
     * @param dbx    DBContext
     * @param insert Insert-Statement
     * @return Map with Name of Keys as Keys, value as value or the Key updatecount and value is "Number of affected rows" if
     * there are no autogenerated keys.
     * @throws SQLException If any Problem apears
     */
    static public int insert(DBContext dbx, Insert insert) throws SQLException {
        return insert(dbx, insert.statementToString());
    }

    /**
     * @see #insertKeys(DBContext, String)
     */
    static public InsertKeys insertKeys(DBContext dbx, Insert insert) throws SQLException {
        return insertKeys(dbx, insert.statementToString());
    }

    /**
     * Executes the given Insert on the Pool, closes the statment afterwards.
     *
     * @param pool   Name of Pool
     * @param insert Insert-Statement
     * @return Map with Name of Keys as Keys, value as value or the Key updatecount and value is "Number of affected rows" if
     * there are no autogenerated keys.
     * @throws SQLException If any Problem apears
     */
    static public int insert(String pool, Insert insert) throws SQLException {
        return insert(pool, insert.statementToString());
    }

    /**
     * Executes the update on the DBContext, closes the statement afterwards.
     *
     * @return the affected rows
     */
    static public int update(DBContext dbx, String sql) throws SQLException {
        Statement statement = null;
        int result = 0;
        try {
            Log.logStatement(sql);
            statement = getStatement(dbx);
            result = statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLStatementException(e, sql);
        } finally {
            close(statement);
        }
        return result;
    }

    /**
     * Executes the update with an new Connection and closes this connection afterwards.
     *
     * @return the affected rows
     */
    static public int update(String poolname, String sql) throws SQLException {
        Statement statement = null;
        int result = 0;
        try {
            Log.logStatement(sql);
            statement = getStatement(poolname);
            result = statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLStatementException(e, sql);
        } finally {
            closeAll(statement);
        }
        return result;
    }

    /**
     * Executes the update on the DBContext, closes the statement afterwards.
     *
     * @return the affected rows
     */
    static public int update(DBContext dbx, de.tarent.dblayer.sql.Statement sql) throws SQLException {
        return update(dbx, sql.statementToString());
    }

    /**
     * Executes the update with an new Connection and closes this connection afterwards.
     *
     * @return the affected rows
     */
    static public int update(String poolname, de.tarent.dblayer.sql.Statement sql) throws SQLException {
        return update(poolname, sql.statementToString());
    }

    /**
     * Returns a Result Object for the Statement and logs it if this is enabled.
     * Result objects are holder object for a ResultSet  and the corresponding Statement, intended for closing both in one step.
     * But you should better use the getResultSet(...), instead of this mehtod, and close the Objects by close() and closeAll().
     */
    public static Result result(DBContext dbx, String sql) throws SQLException {
        try {
            Log.logStatement(sql);
            return new Result(dbx, sql);
        } catch (SQLException e) {
            throw new SQLStatementException(e, sql);
        }
    }

    /**
     * Returns a Result Object for the Statement and logs it if this is enabled.
     * Result objects are holder object for a ResultSet  and the corresponding Statement, intended for closing both in one step.
     * But you should better use the getResultSet(...), instead of this mehtod, and close the Objects by close() and closeAll().
     */
    public static Result result(String poolname, String sql) throws SQLException {
        try {
            Log.logStatement(sql);
            return new Result(poolname, sql);
        } catch (SQLException e) {
            throw new SQLStatementException(e, sql);
        }
    }

    /**
     * Returns a Result Object for the Statement and logs it if this is enabled.
     * Result objects are holder object for a ResultSet  and the corresponding Statement, intended for closing both in one step.
     * But you should better use the getResultSet(...), instead of this mehtod, and close the Objects by close() and closeAll().
     */
    final static public Result result(DBContext dbx, de.tarent.dblayer.sql.Statement sql) throws SQLException {
        return result(dbx, sql.statementToString());
    }

    /**
     * Returns a Result Object for the Statement and logs it if this is enabled.
     * Result objects are holder object for a ResultSet  and the corresponding Statement, intended for closing both in one step.
     * But you should better use the getResultSet(...), instead of this mehtod, and close the Objects by close() and closeAll().
     */
    final static public Result result(String poolname, de.tarent.dblayer.sql.Statement sql) throws SQLException {
        return result(poolname, sql.statementToString());
    }

    /**
     * Checks if the resultset is empty.
     *
     * @param poolname  The poolname
     * @param statement The statement
     * @return <code>true</code> if the resultset is empty.
     * @throws SQLException
     */
    public static boolean isEmpty(String poolname, String statement) throws SQLException {
        ResultSet res = null;
        boolean empty = false;
        try {
            res = DB.getResultSet(poolname, statement);
            empty = !res.next();
        } finally {
            DB.closeAll(res);
        }
        return empty;
    }

    /**
     * Checks if the resultset is empty.
     *
     * @param dbx       Database context
     * @param statement The statement
     * @return <code>true</code> if the resultset is empty.
     * @throws SQLException
     */
    public static boolean isEmpty(DBContext dbx, String statement) throws SQLException {
        ResultSet res = null;
        boolean empty = false;
        try {
            res = DB.getResultSet(dbx, statement);
            empty = !res.next();
        } finally {
            DB.closeStatement(res);
        }
        return empty;
    }
}
