package de.tarent.dblayer.engine;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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
    public static final String DB_POSTGRESQL = "postgres";

    /**
     * Identifier for the oracle database
     */
    public static final String DB_ORACLE = "oracle";

    /**
     * Identifier for Microsoft SQL-Server
     */
    public static final String DB_MSSQL = "mssql";

    /**
     * The full class name for the
     */
    public static final String DEBUG_POOL_CLASS = "debugPoolClass";

    /**
     * Boolean flag for usage of JDBC 2 Drivers without connection pooling
     */
    public static final String USE_OLD_JDBC2 = "useOldJDBC2";

    /**
     * JDBC driver class for connections over an old JDBC 2 interface
     */
    public static final String JDBC2_DRIVER_CLASS = "JDBC2DriverClass";

    /**
     * JDBC connectoin string for connections over an old JDBC 2 interface
     *
     * @deprecated typing error
     */
    public static final String JDBC2_CONNECITON_STRING = "JDBC2ConnecitonString";

    /**
     * JDBC connectoin string for connections over an old JDBC 2 interface
     */
    public static final String JDBC2_CONNECTION_STRING = "JDBC2ConnectionString";

    /**
     * Boolean flag for usage of JNDI in WebSphere Application Server
     */
    public static final String USE_JNDI = "useJNDI";

    /**
     * JNDI name for usage of JNDI in WebSphere Application Server
     */
    public static final String JNDI_NAME = "JNDIName";

    /**
     * The database username
     */
    public static final String USER = "user";

    /**
     * The database password
     */
    public static final String PASSWORD = "password";

    /**
     * The full class name for the db specific data source
     */
    public static final String DATASOURCE_CLASS = "dataSourceClass";

    /**
     * The type of the target database (e.g. used for db dependend sql)
     */
    public static final String TARGET_DB = "targetDBType";

    /**
     * The maximum number of active connections. Requests for a new Connection beyond this limit will block for POOL_MAX_WAIT
     * ms and return an exception if no connection becomes available within this time. Default: 40
     */
    public static final String POOL_MAX_ACTIVE = "poolMaxActive";

    /**
     * Maximum time to wait for a free connection. Default 4.000 ms
     */
    public static final String POOL_MAX_WAIT = "poolMaxWait";

    /**
     * The time im ms to wait before checking for old connectoins to remove. Default: 10.000 ms
     */
    public static final String POOL_TIME_BETWEEN_EVICTION_RUNS = "poolTimeBetweenEvictionRuns";

    /**
     * An connection which is idle for this time will be removed from the pool. Default: 30.000 ms
     */
    public static final String POOL_MIN_EVICTABLE_IDLE_TIME = "poolMinEvictableIdleTime";

    /**
     * Minimum Idle connection to hold in the pool. Default: 2
     */
    public static final String POOL_MIN_IDLE = "poolMinIdle";

    /**
     * Maximum Idle connection to hold in the pool. Default: 10
     */
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
