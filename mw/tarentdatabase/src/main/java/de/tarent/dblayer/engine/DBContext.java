package de.tarent.dblayer.engine;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is the DBLayer Database Execution Context Interface.
 * It holds information about the pool and connections.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public interface DBContext {

    /**
     * Returns the Pool identifier for this DBContext
     */
    public String getPoolName();

    /**
     * Returns the Pool Object for this DBContext
     */
    public Pool getPool();

    /**
     * Returns the Default Connection for this DBContext.
     * @return default Connection from pool
     */
    public Connection getDefaultConnection() throws SQLException;

}
