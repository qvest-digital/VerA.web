package de.tarent.dblayer.octopus;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.DB;
import de.tarent.octopus.server.Closeable;
import de.tarent.octopus.server.OctopusContext;

/**
 * This is a tarent-octopus worker which can open and close
 * database connections. It pushs or remove it from the context.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class ConnectionWorker {
	/** Private logger instance */
	private static final Log logger = LogFactory.getLog(ConnectionWorker.class);
	/** This define the octopus input/output parameter for connections. */
	public static final String CONNECTION = "CONTENT:connection";
	/** This define the octopus input parameter for an optional poolname. */
	public static final String POOLNAME = "CONTENT:connection";

	/** Input definition for {@link #openConnection(OctopusContext, Connection, String)}. */
	public static final String INPUT_openConnection[] = { CONNECTION, POOLNAME };
	/** Input definition for {@link #openConnection(OctopusContext, Connection, String)}. */
	public static final boolean MANDATORY_openConnection[] = { false, false };
	/** Output definition for {@link #openConnection(OctopusContext, Connection, String)}. */
	public static final String OUTPUT_openConnection = CONNECTION;
	/**
	 * This octopus action open a database connection and push it as
	 * "{@link #CONNECTION connection}" into the octopus-context.
	 *
	 * @see DB#getConnection(String)
	 *
	 * @param cntx The current octopus-context.
	 * @param connection Optional connection.
	 * @param poolname Optional poolname.
	 * @return A new database conection.
	 * @throws SQLException
	 */
	public Connection openConnection(OctopusContext cntx, Connection connection, String poolname) throws SQLException {
		if (connection != null && !connection.isClosed()) {
			if (logger.isDebugEnabled())
				logger.debug("Reuse old connection.");
			return connection;
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Open new connection.");
			return DB.getConnection(poolname != null ? poolname : cntx.getModuleName());
		}
	}

	/** Input definition for {@link #openConnection(OctopusContext, Connection, String)}. */
	public static final String INPUT_addToCleanup[] = { CONNECTION };
	/** Input definition for {@link #openConnection(OctopusContext, Connection, String)}. */
	public static final boolean MANDATORY_addToCleanup[] = { true };
	/**
	 * This octopus action add a database connection to the octopus cleanup
	 * mechanism which close the conncetion at the end of the current task.
	 *
	 * @see OctopusContext#addCleanupCode(Closeable)
	 *
	 * @param cntx The current octopus-context.
	 * @param connection Database connection.
	 * @throws SQLException
	 */
	public void addToCleanup(OctopusContext cntx, final Connection connection) throws SQLException {
		if (connection != null && !connection.isClosed()) {
			if (logger.isDebugEnabled())
				logger.debug("Add connection to octopus cleanup mechanism.");
			cntx.addCleanupCode(new Closeable() {
				public void close() {
					try {
						if (!connection.isClosed()) {
							connection.close();
						}
					} catch (SQLException e) {
						logger.warn("Error while closing connection at octopus cleanup.", e);
					}
				}
			});
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Can not add connection to octopus cleanup." +
						connection == null ?
						" No connection available (null)." :
						" Connection already closed.");
		}
	}

	/** Input definition for {@link #closeConnection(OctopusContext, Connection)}. */
	public static final String INPUT_closeConnection[] = { CONNECTION };
	/** Input definition for {@link #closeConnection(OctopusContext, Connection)}. */
	public static final boolean MANDATORY_closeConnection[] = { false };
	/** Output definition for {@link #closeConnection(OctopusContext, Connection)}. */
	public static final String OUTPUT_closeConnection = CONNECTION;
	/**
	 * This octopus action close a database connection and remove it
	 * from the octopus-context.
	 *
	 * @see DB#close(Connection)
	 *
	 * @param cntx The current octopus-context.
	 * @param connection Connection to close.
	 * @return null
	 * @throws SQLException
	 */
	public Connection closeConnection(OctopusContext cntx, Connection connection) throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("Close connection.");
		DB.close(connection);
		return null;
	}
}
