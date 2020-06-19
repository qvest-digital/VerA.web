package de.tarent.dblayer.engine;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.dblayer.engine.proxy.ConnectionProxyInvocationHandler;
import de.tarent.dblayer.resource.Resources;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * This Pool is a Frontend for the Apache dbcp-Pooling Framework.
 * It is configurable over a map with parameters. Valid parameters
 * are described by the constants in this class.
 *
 * @author Christoph Jerolimov, tarent GmbH
 * @author Sebastian Mancke, tarent GmbH
 */
@Log4j2
public class DBPool implements Pool {
    private Map info;
    private GenericObjectPool connectionPool;
    private DataSource dataSource;

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
            // configure the JDBC 3 DataSource
            Class clazz = Class.forName(getProperty(DATASOURCE_CLASS));
            DataSource innerDataSource = (DataSource) clazz.newInstance();
            setDataSourceParameter(innerDataSource, clazz.getMethods());

            // create and configure the GenericPool
            connectionPool = new GenericObjectPool(null);
            configurePool(connectionPool, info);

            // set up the connection pool, autocommit = true by default
            ConnectionFactory connectionFactory = new DataSourceConnectionFactory(innerDataSource);
            new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

            dataSource = new PoolingDataSource(connectionPool);
            //((PoolingDataSource)dataSource).setAccessToUnderlyingConnectionAllowed(true);
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
            if (connectionPool != null) {
                connectionPool.close();
            }
        } catch (Exception e) {
            logger.warn(Resources.getInstance().get("ERROR_CLOSE_POOL"), e);
        }
    }

    private Connection con;

    /* (non-Javadoc)
     * @see de.tarent.dblayer.engine.Pool#getConnection()
     */
    public Connection getConnection() throws SQLException {
        Connection result;

        try {
            result = con;
            if (result == null || result.isClosed()) {
                result = this.dataSource.getConnection();
                con = result;
            }
        } catch (Exception e) {
            throw new SQLException("Unhandled exception.", e);
        }

        logger.trace("Connection requested from pool.");
        return (Connection) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { Connection.class },
          new ConnectionProxyInvocationHandler(result));
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
            logger.debug("pool property maxActive: " + pool.getMaxActive());
            logger.debug("pool property maxWait: " + pool.getMaxWait());
            logger.debug("pool property timeBetweenEvictionRunsMillis: " + pool.getTimeBetweenEvictionRunsMillis());
            logger.debug("pool property minEvictableIdleTimeMillis: " + pool.getMinEvictableIdleTimeMillis());
            logger.debug("pool property minIdle: " + pool.getMinIdle());
            logger.debug("pool property maxIdle: " + pool.getMaxIdle());
        }
    }

    /**
     * Returns the int representation of the value for the key in the map, or the default value, this entry is not in the map
     */
    private int getIntOrDefault(Map map, Object key, int defaultValue) {
        Object value = map.get(key);
        if (value != null) {
            return Integer.parseInt(value.toString());
        }
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
        if (logger.isDebugEnabled()) {
            logger.debug("setting DataSource parameter: " + setter + " to value: " + value);
        }
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
