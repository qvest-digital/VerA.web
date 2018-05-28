package de.tarent.dblayer.engine;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
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
import java.util.Properties;

import de.tarent.dblayer.persistence.AbstractDAO;
import de.tarent.dblayer.persistence.DAORegistry;

/**
 * main class to access the database from applications
 * using tarent-database
 *
 * @author Martin Pelzer, tarent GmbH
 */
public class DBAccess {

    private static String poolName;

    public static void init(Properties connectionProperties) {
        poolName = connectionProperties.getProperty("poolName");

        if (!DB.hasPool(poolName)) {
            DB.openPool(poolName, connectionProperties);
        }
    }

    /**
     * Closes the database pool. Should be called before the
     * applications ends or the database connection is not
     * needed any longer
     */
    public static void closePool() {
        if (DB.hasPool(poolName)) {
            DB.closePool(poolName);
        }
    }

    /**
     * returns a DBContext without a connection, just containing the pool name
     *
     * @return a DBContext without a database connection
     */
    public static DBContext getContextWithoutConnection() {
        DBContextImpl dbc = new DBContextImpl() {
            public Connection getDefaultConnection() throws SQLException {
                throw new RuntimeException("this context is not intended for connections to the database");
            }

            ;
        };
        dbc.setPoolName(poolName);

        return dbc;
    }

    /**
     * returns the default database context
     */
    public static DBContext getDefaultContext() {
        return DB.getDefaultContext(poolName);
    }

    /**
     * returns a DAO for a given bean
     *
     * @param bean the bean for which the DAO is requested
     * @return a DAO for the given bean
     */
    public static AbstractDAO getDAOForBean(Class bean) {
        return DAORegistry.getDAOForBean(bean);
    }

    /**
     * returns a DAO for a given bean
     *
     * @param bean the bean for which the DAO is requested
     * @return a DAO for the given bean
     */
    public static AbstractDAO getDAOForBean(Object bean) {
        return DAORegistry.getDAOForBean(bean);
    }
}
