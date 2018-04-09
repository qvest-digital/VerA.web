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

package de.tarent.dblayer;

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
            if (SQL.isPostgres(dbc))
                instance = new SchemaCreatorPostgres();
            else if (SQL.isMSSQL(dbc))
                instance = new SchemaCreatorMSSQL();
        }
        return instance;
	}

    public static void openPool()
        throws SQLException {

        Properties info = new Properties();
        try {
	        File connectionConfiguration = new File(CONFIG_FILENAME);
	        if (connectionConfiguration.exists()) {
	            System.out.println("using connection configuration: "+connectionConfiguration.getAbsolutePath());
	            info.load(new FileInputStream(connectionConfiguration));
	        } else{
	            System.out.println();
	            System.out.println("----- using default connection configuration: postgres, 192.168.165.46/dblayer_unit_test");
	            System.out.println("----- create "+ CONFIG_FILENAME + " to change this");

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
	 * @param force if force == false, this is only done once in the java-process, so multiple calls with (force == false) have no effect.
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
