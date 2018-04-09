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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.proxy.ConnectionProxyInvocationHandler;
import de.tarent.dblayer.resource.Resources;

/**
 * This Pool is a Frontend for the Apache dbcp-Pooling Framework.
 * It is configurable over a map with parameters. Valid parameters
 * are described by the constants in this class.
 *
 * @author Christoph Jerolimov, tarent GmbH
 * @author Sebastian Mancke, tarent GmbH
 */
public class DBPool implements Pool {
	private static final org.apache.commons.logging.Log logger = LogFactory.getLog(DBPool.class);

	private Map info;
    private GenericObjectPool connectionPool;
    private DataSource dataSource;

    boolean useOldJDBC2Connection = false;
    boolean useJNDI = false;
    String jdbc2ConnectionString = null;

    /**
     * Creates a new Pool.
     * The Pool uses the supplied Propertie Set to choose a DataSource an a Driver.
     * Every Property of the DataSource is configured by the value in the propertyset,
     * if a matching key exists.
     *
     * @param info Property set with <code>dataSourceClass</code> and one entry for each property of the DataSource
     * @throws RuntimeException if an error occures
     */
	public DBPool(Map info) {
		this.info = info;
	}

    /* (non-Javadoc)
	 * @see de.tarent.dblayer.engine.Pool#getTargetDB()
	 */
    public String getTargetDB() {
	return getProperty(TARGET_DB);
    }

	/* (non-Javadoc)
	 * @see de.tarent.dblayer.engine.Pool#init()
	 */
	public void init() {
		try {

	    // use JDBC2
	    if (null != getProperty(USE_OLD_JDBC2) && (new Boolean(getProperty(USE_OLD_JDBC2))).booleanValue()) {
		useOldJDBC2Connection = true;
		Class.forName(getProperty(JDBC2_DRIVER_CLASS));
		jdbc2ConnectionString = getProperty(JDBC2_CONNECTION_STRING);
		if(jdbc2ConnectionString == null || jdbc2ConnectionString.trim().equals(""))
			jdbc2ConnectionString = getProperty(JDBC2_CONNECITON_STRING);
	    }
	    else if(null != getProperty(USE_JNDI) && (new Boolean(getProperty(USE_JNDI).trim())).booleanValue()){
		// configure the JNDI DataSource
		useJNDI = true;
		InitialContext ctx = new InitialContext();
		dataSource = (DataSource)ctx.lookup(getProperty(JNDI_NAME));
		ctx.close();
	    }
	    else {

		// configure the JDBC 3 DataSource
		Class clazz = Class.forName(getProperty(DATASOURCE_CLASS));
		DataSource innerDataSource = (DataSource)clazz.newInstance();
		setDataSourceParameter(innerDataSource, clazz.getMethods());

		// create and configure the GenericPool
		connectionPool = new GenericObjectPool(null);
		configurePool(connectionPool, info);

		// set up the connection pool, autocommit = true by default
		ConnectionFactory connectionFactory = new DataSourceConnectionFactory(innerDataSource);
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

		dataSource = new PoolingDataSource(connectionPool);
//                ((PoolingDataSource)dataSource).setAccessToUnderlyingConnectionAllowed(true);
	    }
	} catch (Exception e) {
			logger.error(Resources.getInstance().get("ERROR_INIT_POOL"), e);
			throw new RuntimeException(Resources.getInstance().get("ERROR_INIT_POOL"), e);
		}
	}

    /* (non-Javadoc)
	 * @see de.tarent.dblayer.engine.Pool#close()
	 */
	public void close() {
		try {
			//closing an not-initialized pool shuldn't log an exception
			if (connectionPool != null)
		connectionPool.close();
		} catch (Exception e) {
			logger.warn(Resources.getInstance().get("ERROR_CLOSE_POOL"), e);
		}
	}

	private Connection con;
	/* (non-Javadoc)
	 * @see de.tarent.dblayer.engine.Pool#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		Connection result = null;

		if ( useOldJDBC2Connection )
		{
			logger.trace("using jdbc2ConnectionString instead of pooling for creation." );
			result = con;
			if ( result == null || result.isClosed() )
			{
				if (null != getProperty(USER))
					con = DriverManager.getConnection(jdbc2ConnectionString, getProperty(USER), getProperty(PASSWORD));
				else
					con = DriverManager.getConnection(jdbc2ConnectionString);
				result = con;
			}
		}
		else if ( useJNDI )
		{
			logger.trace("using JNDI dataSource instead of pooling for creation." );
			result = con;
			if ( result == null || result.isClosed() )
			{
				con = dataSource.getConnection();
				result = con;
			}
		}
		else
		{
			try
			{
				result = con;
				if ( result == null || result.isClosed() )
				{
					result = ( Connection ) this.dataSource.getConnection();
					con = result;
				}
			}
			catch ( Exception e )
			{
				throw new SQLException( "Unhandled exception.", e );
			}
		}

		logger.trace("Connection requested from pool." );
		return ( Connection ) Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] { Connection.class }, new ConnectionProxyInvocationHandler( result ) );
	}

    protected void configurePool(GenericObjectPool pool, Map properties) {
	pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

	pool.setMaxActive(getIntOrDefault(properties, POOL_MAX_ACTIVE, 40));
	pool.setMaxWait(getIntOrDefault(properties, POOL_MAX_WAIT, 4000));
	pool.setTimeBetweenEvictionRunsMillis(getIntOrDefault(properties, POOL_TIME_BETWEEN_EVICTION_RUNS, 10000));
	pool.setMinEvictableIdleTimeMillis(getIntOrDefault(properties, POOL_MIN_EVICTABLE_IDLE_TIME, 30000));
	pool.setMinIdle(getIntOrDefault(properties, POOL_MIN_IDLE, 2));
	pool.setMaxIdle(getIntOrDefault(properties, POOL_MAX_IDLE, 10));

	if (logger.isDebugEnabled()) {
	    logger.debug("pool property maxActive: "+pool.getMaxActive());
	    logger.debug("pool property maxWait: "+pool.getMaxWait());
	    logger.debug("pool property timeBetweenEvictionRunsMillis: "+pool.getTimeBetweenEvictionRunsMillis());
	    logger.debug("pool property minEvictableIdleTimeMillis: "+pool.getMinEvictableIdleTimeMillis());
	    logger.debug("pool property minIdle: "+pool.getMinIdle());
	    logger.debug("pool property maxIdle: "+pool.getMaxIdle());
	}
    }

    /**
     * Returns the int representation of the value for the key in the map, or the default value, this entry is not in the map
     */
    int getIntOrDefault(Map map, Object key, int defaultValue) {
	Object value = map.get(key);
	if (value != null)
	    return Integer.parseInt(value.toString());
	return defaultValue;
    }

    /**
     * @return the value for the key in this pools property set.
     */
    public String getProperty(String key) {
	Object v = info.get(key);
	return (v == null) ? null : v.toString();
    }

    /**
     * Finds all setter with an associates property int the property set.
     */
	private void setDataSourceParameter(DataSource dataSource, Method method[]) {
		for (int i = 0; i < method.length; i++) {
			String name = method[i].getName();
			if (name.startsWith("set")) {
				String value = getProperty(name.substring(3));
				if (value == null) {
					value = getProperty(name.substring(3, 4).toLowerCase() + name.substring(4));
				}
				if (value != null && value.length() != 0) {
					setDataSourceParameter(dataSource, method[i], value);
				}
			}
		}
	}

    /**
     * Sets one Property of the DataSource
     */
	private void setDataSourceParameter(DataSource dataSource, Method setter, String value) {
	if (logger.isDebugEnabled())
	    logger.debug("setting DataSource parameter: "+ setter +" to value: "+ value);
		try {
			Class types[] = setter.getParameterTypes();
			if (types.length == 1 && types[0].equals(String.class)) {
				setter.invoke(dataSource, new Object[] { value });
			} else if (types.length == 1 && types[0].equals(Integer.TYPE)) {
				setter.invoke(dataSource, new Object[] { Integer.valueOf(value) });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
