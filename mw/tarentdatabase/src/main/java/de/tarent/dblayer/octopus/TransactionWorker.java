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

package de.tarent.dblayer.octopus;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.octopus.server.Closeable;
import de.tarent.octopus.server.OctopusContext;

/**
 * This is a tarent-octopus worker which extend the {@link ConnectionWorker}
 * with some simple transaction methods, like
 * {@link #start(OctopusContext, Connection, String) start transaction},
 * {@link #commit(OctopusContext, Connection) commit transaction} and
 * {@link #rollback(OctopusContext, Connection) rollback a transaction}.
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class TransactionWorker extends ConnectionWorker {
	/** Private logger instance */
	private static final Log logger = LogFactory.getLog(TransactionWorker.class);

	/** Input definition for {@link #start(OctopusContext, Connection, String)}. */
	public static final String INPUT_start[] = { CONNECTION, POOLNAME };
	/** Input definition for {@link #start(OctopusContext, Connection, String)}. */
	public static final boolean MANDATORY_start[] = { false, false };
	/** Output definition for {@link #start(OctopusContext, Connection, String)}. */
	public static final String OUTPUT_start = CONNECTION;
	/**
	 * This octopus action start a transaction. It use the
	 * {@link #openConnection(OctopusContext, Connection, String)} method
	 * from the {@link ConnectionWorker} to verify an open connection.
	 *
	 * Set {@link Connection#setAutoCommit(boolean)} to false.
	 *
	 * @param cntx The current octopus-context.
	 * @param connection Optional database connection.
	 * @param poolname Optional poolname.
	 * @return
	 * @throws SQLException
	 */
	public Connection start(OctopusContext cntx, Connection connection, String poolname) throws SQLException {
		connection = openConnection(cntx, connection, poolname);

		if (logger.isDebugEnabled())
			logger.debug("Start transaction, deactivate autocommit.");
		connection.setAutoCommit(false);
		return connection;
	}

	/**
	 * This octopus action add a transaction to the octopus cleanup
	 * mechanism which rollback and close the transaction at the end
	 * of the current task.
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
				logger.debug("Add transaction to octopus cleanup mechanism.");
			cntx.addCleanupCode(new Closeable() {
				public void close() {
					try {
						if (!connection.isClosed()) {
							if (logger.isWarnEnabled())
								logger.warn("Transaction is not closed until octopus cleanup mechanism is called." +
										" Will know automatically ROLLBACK this transaction!");
							connection.rollback();
							connection.close();
						}
					} catch (SQLException e) {
						logger.warn("Error while rollback/closing transaction at octopus cleanup.", e);
					}
				}
			});
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Can not add transaction to octopus cleanup." +
						connection == null ?
						" No transaction available (null)." :
						" Transaction already closed.");
		}
	}

	/** Input definition for {@link #commit(OctopusContext, Connection)}. */
	public static final String INPUT_commit[] = { CONNECTION, POOLNAME };
	/** Input definition for {@link #commit(OctopusContext, Connection)}. */
	public static final boolean MANDATORY_commit[] = { false, false };
	/**
	 * This octopus action commit a transaction.
	 *
	 * @see Connection#commit()
	 *
	 * @param cntx The current octopus-context.
	 * @param connection Database connection.
	 * @throws SQLException
	 */
	public void commit(OctopusContext cntx, Connection connection) throws SQLException {
		if (connection != null && !connection.isClosed()) {
			if (logger.isDebugEnabled())
				logger.debug("Commit transaction, activate autocommit.");
			connection.commit();
			connection.setAutoCommit(true);
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Can not commit transaction." +
						connection == null ?
						" No transaction available (null)." :
						" Transaction already closed.");
		}
	}

	/** Input definition for {@link #rollback(OctopusContext, Connection)}. */
	public static final String INPUT_rollback[] = { CONNECTION, POOLNAME };
	/** Input definition for {@link #rollback(OctopusContext, Connection)}. */
	public static final boolean MANDATORY_rollback[] = { false, false };
	/**
	 * This octopus action rollback a transaction.
	 *
	 * @see Connection#rollback()
	 *
	 * @param cntx The current octopus-context.
	 * @param connection Database connection.
	 * @throws SQLException
	 */
	public void rollback(OctopusContext cntx, Connection connection) throws SQLException {
		if (connection != null && !connection.isClosed()) {
			if (logger.isDebugEnabled())
				logger.debug("Rollback transaction, activate autocommit.");
			connection.rollback();
			connection.setAutoCommit(true);
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Can not rollback transaction." +
						connection == null ?
						" No transaction available (null)." :
						" Transaction already closed.");
		}
	}
}
