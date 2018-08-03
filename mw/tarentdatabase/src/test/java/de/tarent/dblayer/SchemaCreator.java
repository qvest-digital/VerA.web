package de.tarent.dblayer;

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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
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

import java.sql.SQLException;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Pool;
import de.tarent.dblayer.sql.SQL;

import java.util.Properties;
import java.io.*;

import org.apache.commons.logging.Log;

public abstract class SchemaCreator {

    public static final Log logger = LogFactory.getLog(SchemaCreator.class);

    public static final String CONFIG_FILENAME = "test-connection.properties";
    public static final String TEST_POOL = "test_pool";

    public SchemaCreator() {
        super();
    }

    private boolean isSchemaSetUp = false;
    protected DBContext dbx;

    static SchemaCreator instance;
    static SchemaCreator instance_ms;

    /**
     * Returns a default instance
     */
    public static SchemaCreator getInstance()
      throws SQLException {
        if (instance == null) {
            openPool();
            DBContext dbc = DB.getDefaultContext(TEST_POOL);
            if (SQL.isPostgres(dbc)) {
                instance = new SchemaCreatorPostgres();
            } else if (SQL.isMSSQL(dbc)) {
                instance = new SchemaCreatorMSSQL();
            }
        }
        return instance;
    }

    public static void openPool()
      throws SQLException {

        Properties info = new Properties();
        try {
            File connectionConfiguration = new File(CONFIG_FILENAME);
            if (connectionConfiguration.exists()) {
                System.out.println("using connection configuration: " + connectionConfiguration.getAbsolutePath());
                info.load(new FileInputStream(connectionConfiguration));
            } else {
                System.out.println();
                System.out.println("----- using default connection configuration: postgres, 192.168.165.46/dblayer_unit_test");
                System.out.println("----- create " + CONFIG_FILENAME + " to change this");

                info.setProperty(Pool.DATASOURCE_CLASS, "org.postgresql.jdbc3.Jdbc3PoolingDataSource");
                info.setProperty("targetDBType", Pool.DB_POSTGRESQL);
                info.setProperty("serverName", "192.168.165.46");
                //			info.setProperty("portNumber", "");
                info.setProperty("databaseName", "dblayer_unit_test");
                info.setProperty("user", "postgres");
                info.setProperty("password", "postgres");

                info.setProperty("poolMaxWait", "20000");
                info.setProperty("poolMinIdle", "1");
                info.setProperty("poolMaxActive", "10");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error on loading test database pool configuration", e);
        }
        DB.openPool(TEST_POOL, info);
    }

    /**
     * Set up the tables and data in the test database;
     *
     * @param force    if force == false, this is only done once in the java-process, so multiple calls with (force == false)
     *                 have no effect.
     * @param poolname TODO
     */
    public void setUp(boolean force) throws SQLException {
        if (force || (!isSchemaSetUp)) {
            dbx = DB.getDefaultContext(TEST_POOL);
            dropSchema();
            createSchema();
            doInserts();
            isSchemaSetUp = true;
            DB.close(dbx.getDefaultConnection());
        }
    }

    protected abstract void doInserts() throws SQLException;

    protected abstract void createSchema() throws SQLException;

    protected abstract void dropSchema() throws SQLException;
}
