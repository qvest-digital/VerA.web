package de.tarent.dblayer;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Pool;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;

public abstract class SchemaCreator {
    public static final String CONFIG_FILENAME = "test-connection.properties";
    public static final String TEST_POOL = "test_pool";

    public SchemaCreator() {
        super();
    }

    private boolean isSchemaSetUp = false;
    DBContext dbx;

    static SchemaCreator instance;

    /**
     * Returns a default instance
     */
    public static SchemaCreator getInstance() {
        if (instance == null) {
            openPool();
            instance = new SchemaCreatorPostgres();
        }
        return instance;
    }

    public static void openPool() {
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
     * @param force if force == false, this is only done once in the java-process, so multiple calls with (force == false)
     *              have no effect.
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
