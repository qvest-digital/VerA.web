package de.tarent.dblayer;

import java.sql.*;
import de.tarent.dblayer.engine.*;
import de.tarent.dblayer.sql.SQL;
import java.text.DateFormat;
import java.util.*;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Creates the initial Schema for the tests.
 * It is organised as a singleton.
 */
public class SchemaCreator {
    //
    // variables and constants
    //
    /** Logger of this class */
    public static final Logger logger = Logger.getLogger(SchemaCreator.class.getName());

    /** filename (may contain path) of the db layer properties */
    public static final String CONFIG_FILENAME = "test-connection.properties";

    /** name of the test db layer pool */
    public static final String TEST_POOL = "test_pool";

    /** instance of this singleton */
    static SchemaCreator instance;

    /** flag: true iff db schema has been set up by this singleton instance */
    boolean isSchemaSetUp = false;

    /** flag: true iff the test database supports serial fields */
    boolean supportingSerials = false;

    /** db layer properties */
    final Properties info = new Properties();

    //
    // singleton pattern methods
    //
    /**
     * protected constructor to enforce singleton pattern
     */
    protected SchemaCreator() {
    }

    /**
     * This method returns the singleton instance of this class.
     */
    public static synchronized SchemaCreator getInstance() {
	String enabled = System.getProperty("beans.test.enabled");
	if (!Boolean.parseBoolean(enabled)) {
		System.err.println("\t\tTEST DISABLED! To enable this test set the system property " +
				"\"beans.test.enabled\" to \"true\". ");
		return null;
	}

	if (instance == null)
	    instance = new SchemaCreator();
	return instance;
    }

    //
    // public methods
    //
    /**
     * This method sets up the tables and data in the test database.
     *
     * @param force if false, this is only done once by this singleton.
     */
    public void setUp(boolean force)
	throws SQLException {
	if (force || (!isSchemaSetUp)) {
	    openPool();
	    dropSchema();
	    createSchema();
	    doInserts();
	    isSchemaSetUp = true;
	}
    }

    /**
     * This method initialises the {@link #info db layer properties} and the
     * {@link #supportingSerials} flag and then opens the test db layer pool.
     *
     * @throws SQLException
     */
    public void openPool()  {

	info.clear();
	try {
	    File connectionConfiguration = new File(CONFIG_FILENAME);
	    if (connectionConfiguration.exists()) {
		System.out.println("using connection configuration: "+connectionConfiguration.getAbsolutePath());
		info.load(new FileInputStream(connectionConfiguration));
	    } else {
		System.out.println();
		System.out.println("----- using default connection configuration: postgres, 192.168.165.46/dblayer_unit_test");
		System.out.println("----- create "+ CONFIG_FILENAME +" to change this");

		info.setProperty(Pool.DATASOURCE_CLASS, "org.postgresql.jdbc3.Jdbc3PoolingDataSource");
		info.setProperty("targetDBType", "postgres");
		info.setProperty("serverName", "192.168.165.46");
		//			info.setProperty("portNumber", "");
		info.setProperty("databaseName", "dblayer_unit_test");
		info.setProperty("user", "postgres");
		info.setProperty("password", "postgres");

		info.setProperty("poolMaxWait", "20000");
		info.setProperty("poolMinIdle", "1");
		info.setProperty("poolMaxActive", "10");
	    }
	    supportingSerials = !Pool.DB_ORACLE.equals(info.get(Pool.TARGET_DB));
	} catch (Exception e) {
	    throw new RuntimeException("Error on loading test database pool configuration", e);
	}
	DB.openPool(TEST_POOL, info);
    }

    /**
     * This flag is true iff the test database supports serials and thus especially
     * whether or not the table "produkt" having a serial primary key exists.
     *
     * @return true iff the test database supports serials
     */
    public boolean isSupportingSerials() {
	return supportingSerials;
    }

    //
    // helper methods
    //
    /**
     * This method drops the test tables in the test database. Exceptions are
     * ignored, thus this can also be executed when there are no test tables.
     */
    protected void dropSchema() {
	try {
	    DB.update(TEST_POOL, "DROP TABLE person");
	} catch (SQLException e) {
	    logger.log(Level.FINE, "Error on dropping table person", e);
	    // ignore, because table may not exist
	}

	try {
	    DB.update(TEST_POOL, "DROP TABLE produkt");
	} catch (SQLException e) {
	    logger.log(Level.FINE, "Error on dropping table produkt", e);
	    // ignore, because table may not exist
	}

	try {
	    DB.update(TEST_POOL, "DROP TABLE firma");
	} catch (SQLException e) {
	    logger.log(Level.FINE, "Error on dropping table firma", e);
	    // ignore, because table may not exist
	}
    }

    /**
     * This method creates the tables "firma", "person" and (if the test
     * database supports serials) "produkt".
     *
     * @see #isSupportingSerials()
     * @throws SQLException
     */
    protected void createSchema() throws SQLException {
	DB.update(TEST_POOL,
		  "CREATE TABLE firma ("
		  +" pk_firma integer not null,"
		  +" name varchar(50),"
		  +" umsatz integer,"
		  +" CONSTRAINT firma_pkey PRIMARY KEY (pk_firma)"
		  +")");

	DB.update(TEST_POOL,
		  "CREATE TABLE person ("
		  +" pk_person integer not null,"
		  +" fk_firma integer,"
		  +" vorname varchar(50),"
		  +" nachname varchar(50),"
		  +" geburtstag date,"
		  +" CONSTRAINT person_pkey PRIMARY KEY (pk_person),"
		  +" CONSTRAINT firma_fkey FOREIGN KEY (fk_firma) REFERENCES firma (pk_firma))"
		  +")");

	if (isSupportingSerials())
	    DB.update(TEST_POOL,
		    "CREATE TABLE produkt ("
		    +" pk_produkt serial not null,"
		    +" fk_firma integer,"
		    +" name varchar(50),"
		    +" CONSTRAINT produkt_pkey PRIMARY KEY (pk_produkt),"
		    +" CONSTRAINT produkt_fkey FOREIGN KEY (fk_firma) REFERENCES firma (pk_firma))"
		    +")");
    }

    /**
     * This method creates entries in the tables "firma", "person" and
     * (if the test database supports serials) "produkt".
     *
     * @see #isSupportingSerials()
     * @throws SQLException
     */
    protected void doInserts() {
	try {
	    DBContext dbc = DB.getDefaultContext(TEST_POOL);
	    SQL.Insert(dbc).table("firma")
		.insert("pk_firma", new Integer(1))
		.insert("name", "Dagoberts Geldspeicher")
		.insert("umsatz", new Integer(100000))
		.execute();

	    SQL.Insert(dbc).table("firma")
		.insert("pk_firma", new Integer(2))
		.insert("name", "Donalds Frittenbude")
		.insert("umsatz", new Integer(30))
		.execute();

	    SQL.Insert(dbc).table("firma")
		.insert("pk_firma", new Integer(3))
		.insert("name", "Düsentriebs Werkstatt")
		.insert("umsatz", new Integer(3000))
		.execute();

	    SQL.Insert(dbc).table("person")
		.insert("pk_person", new Integer(1))
		.insert("fk_firma", new Integer(1))
		.insert("vorname", "Dagobert")
		.insert("nachname", "Duck")
		.insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("31.03.80"))
		.execute();

	    SQL.Insert(dbc).table("person")
		.insert("pk_person", new Integer(2))
		.insert("fk_firma", new Integer(1))
		.insert("vorname", "Daisy")
		.insert("nachname", "Duck")
		.insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("11.03.80"))
		.execute();

	    SQL.Insert(dbc).table("person")
		.insert("pk_person", new Integer(3))
		.insert("fk_firma", new Integer(2))
		.insert("vorname", "Donald")
		.insert("nachname", "Duck")
		.insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("28.01.80"))
		.execute();

	    SQL.Insert(dbc).table("person")
		.insert("pk_person", new Integer(4))
		.insert("fk_firma", null)
		.insert("vorname", "Gustav")
		.insert("nachname", "Gans")
		.insert("geburtstag", DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("26.11.80"))
		.execute();

	    if (isSupportingSerials()) {
		SQL.Insert(dbc).table("produkt")
		    .insert("fk_firma", new Integer(2))
		    .insert("name", "Currywurst-Pommes rot-weiß")
		    .execute();
	    }
	} catch (Exception e) {
	    throw new RuntimeException("Error on reading date strings", e);
	}
    }
}
