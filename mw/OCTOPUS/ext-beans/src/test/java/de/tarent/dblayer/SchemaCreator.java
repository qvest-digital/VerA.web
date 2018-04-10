package de.tarent.dblayer;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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
