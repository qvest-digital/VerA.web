package de.tarent.dblayer.engine;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.proxy.ResultSetProxyInvocationHandler;
import de.tarent.dblayer.sql.Statement;

/**
 * Result objects are holder object for a ResultSet  and the corresponding Statement, intended for closing both in one step.
 * But you should better use the getResultSet(...), instead of this mehtod, and close the Objects by close() and closeAll().
 *
 * @author Wolfgang Klein
 */
public class Result {
	private java.sql.Connection connection;
	private java.sql.Statement statement;
	private java.sql.ResultSet result;

    private static final org.apache.commons.logging.Log logger = LogFactory.getLog(Result.class);

    boolean statementIsClosed;

	protected Result(DBContext dbx, String sql) throws SQLException {
        this.statement = DB.getStatement(dbx);
		initialise(sql);
	}

	protected Result(String poolname, String sql) throws SQLException {
		this.statement = DB.getStatement(poolname);
		initialise(sql);
	}

	private void initialise(String sql) throws SQLException {
		this.result = this.statement.executeQuery(sql);
        this.connection = statement.getConnection();
        statementIsClosed = false;
	}

	protected Result(DBContext dbx, Statement sql) throws SQLException {
		this(dbx, sql.statementToString());
	}

	protected Result(String poolname, Statement sql) throws SQLException {
		this(poolname, sql.statementToString());
	}

	public ResultSet resultSet() {
		return ( ResultSet ) Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] { ResultSet.class }, new ResultSetProxyInvocationHandler( result ) );
	}

    /**
     * Iterates over the result set and calles the process method for each row.
     * Afterwards, the result set will be closed.
     *
     * @returns the number of iterations
     */
    public int iterate(ResultProcessor processor) throws SQLException {
        int i = 0;
        try {
            while (result.next()) {
                processor.process(result);
                i++;
            }
        } finally {
            close();
        }
        return i;
    }

	public void close() {
        if (statementIsClosed)
            return;

		try {
			statement.close();
            statementIsClosed = true;
            statement = null;
            this.result = null;
		} catch (SQLException e) {
            logger.warn("Error on closing connection", e);
		}
	}

    /**
     * Close from the Statement up to the Connection
     */
	public void closeAll() {
        if (statementIsClosed)
            DB.close(connection);
        else
            DB.closeAll(statement);
        statementIsClosed = true;
        this.result = null;
        connection = null;
        statement = null;
    }
}
