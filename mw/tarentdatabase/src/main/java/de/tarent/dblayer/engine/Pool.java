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
    /**
     * Identifier for the postgresql database
     */
    String DB_POSTGRESQL = "postgres";

    /**
     * The full class name for the
     */
    String DEBUG_POOL_CLASS = "debugPoolClass";

    /**
     * Boolean flag for usage of JDBC 2 Drivers without connection pooling
     */
    String USE_OLD_JDBC2 = "useOldJDBC2";

    /**
     * JDBC driver class for connections over an old JDBC 2 interface
     */
    String JDBC2_DRIVER_CLASS = "JDBC2DriverClass";

    /**
     * JDBC connection string for connections over an old JDBC 2 interface
     */
    String JDBC2_CONNECTION_STRING = "JDBC2ConnectionString";

    /**
     * Boolean flag for usage of JNDI in WebSphere Application Server
     */
    String USE_JNDI = "useJNDI";

    /**
     * JNDI name for usage of JNDI in WebSphere Application Server
     */
    String JNDI_NAME = "JNDIName";

    /**
     * The database username
     */
    String USER = "user";

    /**
     * The database password
     */
    String PASSWORD = "password";

    /**
     * The full class name for the db specific data source
     */
    String DATASOURCE_CLASS = "dataSourceClass";

    /**
     * The type of the target database (e.g. used for db dependend sql)
     */
    String TARGET_DB = "targetDBType";

    /**
     * The maximum number of active connections. Requests for a new Connection beyond this limit will block for POOL_MAX_WAIT
     * ms and return an exception if no connection becomes available within this time. Default: 40
     */
    String POOL_MAX_ACTIVE = "poolMaxActive";

    /**
     * Maximum time to wait for a free connection. Default 4.000 ms
     */
    String POOL_MAX_WAIT = "poolMaxWait";

    /**
     * The time im ms to wait before checking for old connectoins to remove. Default: 10.000 ms
     */
    String POOL_TIME_BETWEEN_EVICTION_RUNS = "poolTimeBetweenEvictionRuns";

    /**
     * An connection which is idle for this time will be removed from the pool. Default: 30.000 ms
     */
    String POOL_MIN_EVICTABLE_IDLE_TIME = "poolMinEvictableIdleTime";

    /**
     * Minimum Idle connection to hold in the pool. Default: 2
     */
    String POOL_MIN_IDLE = "poolMinIdle";

    /**
     * Maximum Idle connection to hold in the pool. Default: 10
     */
    String POOL_MAX_IDLE = "poolMaxIdle";

    /**
     * Returns an identifier for the target database of this context,
     * as provided in the pool configuration.
     *
     * @return this should be one of the DB_* constants of this interface
     */
    String getTargetDB();

    void init();

    /**
     * Close the current pool.
     */
    void close();

    /**
     * Get one connection.
     *
     * @return A new SQL connection.
     * @throws SQLException if no connection is available.
     */
    Connection getConnection() throws SQLException;
}
