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

package de.tarent.dblayer.engine;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is an abstract pooling interface for the tarent-database library.
 * 
 * @author Christoph Jerolimov, tarent GmbH
 * @author Sebastian Mancke, tarent GmbH
 */
public interface Pool {
	/** Identifier for the postgresql database */
	public static final String DB_POSTGRESQL = "postgres";

	/** Identifier for the oracle database */
	public static final String DB_ORACLE = "oracle";
	
	/** Identifier for Microsoft SQL-Server */
	public static final String DB_MSSQL = "mssql";

	/** The full class name for the */
	public static final String DEBUG_POOL_CLASS = "debugPoolClass";

	/** Boolean flag for usage of JDBC 2 Drivers without connection pooling */
	public static final String USE_OLD_JDBC2 = "useOldJDBC2";

	/** JDBC driver class for connections over an old JDBC 2 interface */
	public static final String JDBC2_DRIVER_CLASS = "JDBC2DriverClass";

	/** 
	 * JDBC connectoin string for connections over an old JDBC 2 interface 
	 * @deprecated typing error
	 */
	public static final String JDBC2_CONNECITON_STRING = "JDBC2ConnecitonString";
	
	/** JDBC connectoin string for connections over an old JDBC 2 interface */
	public static final String JDBC2_CONNECTION_STRING = "JDBC2ConnectionString";
	
	
	/** Boolean flag for usage of JNDI in WebSphere Application Server */
	public static final String USE_JNDI = "useJNDI";

	/** JNDI name for usage of JNDI in WebSphere Application Server */
	public static final String JNDI_NAME = "JNDIName";	
	
	/** The database username */
	public static final String USER = "user";

	/** The database password */
	public static final String PASSWORD = "password";

	/** The full class name for the db specific data source */
	public static final String DATASOURCE_CLASS = "dataSourceClass";

	/** The type of the target database (e.g. used for db dependend sql)*/
	public static final String TARGET_DB = "targetDBType";

	/** The maximum number of active connections. Requests for a new Connection beyond this limit will block for POOL_MAX_WAIT ms and return an exception if no connection becomes available within this time. Default: 40*/
	public static final String POOL_MAX_ACTIVE = "poolMaxActive";

	/** Maximum time to wait for a free connection. Default 4.000 ms*/
	public static final String POOL_MAX_WAIT = "poolMaxWait";

	/** The time im ms to wait before checking for old connectoins to remove. Default: 10.000 ms */
	public static final String POOL_TIME_BETWEEN_EVICTION_RUNS = "poolTimeBetweenEvictionRuns";

	/** An connection which is idle for this time will be removed from the pool. Default: 30.000 ms */
	public static final String POOL_MIN_EVICTABLE_IDLE_TIME = "poolMinEvictableIdleTime";

	/** Minimum Idle connection to hold in the pool. Default: 2 */
	public static final String POOL_MIN_IDLE = "poolMinIdle";

	/** Maximum Idle connection to hold in the pool. Default: 10 */
	public static final String POOL_MAX_IDLE = "poolMaxIdle";

	/**
	 * Returns an identifier for the target database of this context,
	 * as provided in the pool configuration.
	 * 
	 * @return this should be one of the DB_* constants of this interface
	 */
	public String getTargetDB();

	public void init();

	/**
	 * Close the current pool.
	 */
	public void close();

	/**
	 * Get one connection.
	 * 
	 * @return A new SQL connection.
	 * @throws SQLException if no connection is available.
	 */
	public Connection getConnection() throws SQLException;
}