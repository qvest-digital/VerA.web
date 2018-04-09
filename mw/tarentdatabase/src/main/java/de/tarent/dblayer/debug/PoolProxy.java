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

package de.tarent.dblayer.debug;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.Pool;

/**
 * <p>
 * This pooling proxy start a new thread per new database connection.
 * It checks if the connection is alive ot not every 100 miliseconds
 * the first 10 seconds, after that every second.
 * It shows the status message on the standard output. Every five
 * seconds it also print out a full stacktrace of the moment where
 * this connection is opened.
 * </p>
 * 
 * <p>
 * <strong>Note:</strong>
 * Use this only for debugging, not for logging. Its really slow.
 * </p>
 * 
 * @author Christoph Jerolimov, tarent GmbH
 */
public class PoolProxy implements Pool {
	/** Logger instance */
	private static final Log logger = LogFactory.getLog(PoolProxy.class);

	/** Delegate instance */
	private Pool delegate;
	
	/** Connection counter for all connections */
	private long allConnectionCount = 0L;

	/** Connection counter for opened connections */
	private long inUseConnectionCount = 0L;

	public PoolProxy(Pool delegate) {
		this.delegate = delegate;
	}

	public String getTargetDB() {
		return delegate.getTargetDB();
	}

	public void init() {
		delegate.init();
	}

	public void close() {
		delegate.close();
	}

	public Connection getConnection() throws SQLException {
		// Debug informations
		final long currentCC = incrementConnectionCount();
		final long currentTime = System.currentTimeMillis();
		final Connection con = delegate.getConnection();
		final StackTraceElement trace[] = new Exception().getStackTrace();
		
		// Start watching thread.
		new Thread(new Runnable() {
			public void run() {
				long sleep = 0L;
				try {
					while (sleep == 0L || !con.isClosed()) {
						print("opened for " + (System.currentTimeMillis() - currentTime) + "ms.",
								((sleep % 5000L) == 0L) ? trace : null);
						Thread.sleep(sleep);
						sleep += (sleep < 10000L) ? 100L : 1000L;
					}
					decrementConnectionCount();
					print("CLOSED after " + (System.currentTimeMillis() - currentTime) + "ms.", null);
				} catch (Exception e) {
					decrementConnectionCount();
					logger.warn("Error while watching a database connection.", e);
				}
			}
			
			private void print(String message, StackTraceElement trace[]) {
				StringBuffer buffer = new StringBuffer(500);
				buffer.append("Connection #").append(currentCC).append(": ");
				buffer.append(message);
				buffer.append("\t[Open: ").append(inUseConnectionCount).append("]\n");
				if (trace != null)
					for (int i = 0; i < trace.length; i++)
						buffer.append("\tat ").append(trace[i]).append("\n");
				PoolProxy.print(buffer.toString());
			}
		}).start();
		
		// Return delegate connection.
		return con;
	}

	synchronized private long incrementConnectionCount() {
		inUseConnectionCount++;
		return allConnectionCount++;
	}

	synchronized private void decrementConnectionCount() {
		inUseConnectionCount--;
	}

	synchronized static private void print(String message) {
		System.out.print(message);
	}
}
