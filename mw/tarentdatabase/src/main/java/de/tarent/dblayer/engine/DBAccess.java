package de.tarent.dblayer.engine;
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
