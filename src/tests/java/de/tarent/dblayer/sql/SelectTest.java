///*
// * tarent-database,
// * jdbc database library
// * Copyright (c) 2005-2006 tarent GmbH
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License,version 2
// * as published by the Free Software Foundation.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
// * 02110-1301, USA.
// *
// * tarent GmbH., hereby disclaims all copyright
// * interest in the program 'tarent-database'
// * Signature of Elmar Geese, 14 June 2007
// * Elmar Geese, CEO tarent GmbH.
// */
//
///*
// * $Id: SelectTest.java,v 1.14 2007/06/14 14:51:57 dgoema Exp $
// */
//package de.tarent.dblayer.sql;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.logging.Log;
//
//import de.tarent.commons.logging.LogFactory;
//import de.tarent.dblayer.SchemaCreator;
//import de.tarent.dblayer.engine.DB;
//import de.tarent.dblayer.helper.ResultList;
//import de.tarent.dblayer.sql.clause.Expr;
//import de.tarent.dblayer.sql.clause.Limit;
//import de.tarent.dblayer.sql.clause.Order;
//import de.tarent.dblayer.sql.statement.Select;
//import de.tarent.dblayer.engine.DBContext;
//
///**
// * This test class tests a number of db layer selection features. 
// */
//public class SelectTest
//    extends junit.framework.TestCase {
//    static Log logger = LogFactory.getLog(SelectTest.class);
//    
//    DBContext dbc;
//    //
//    // constructors and Testcase overrides
//    //
//    /** the empty constructor */
//    public SelectTest() {
//        super();
//    }
//
//    /** the constructor with an initial name */
//    public SelectTest(String init) {
//        super(init);
//    }
//
//    /** the initialising method used to setup the database schema */
//    public void setUp() throws Exception {
//    	SchemaCreator.getInstance().setUp(false);
//        dbc = DB.getDefaultContext(SchemaCreator.TEST_POOL);
//    }
//
//    public void tearDown() throws Exception {
//        dbc.getDefaultConnection().close();
//    }
//    
//    //
//    // test methods
//    //
//    /**
//     * This method tests a simple ordered select from the table "person"; special
//     * attention is given to the order and correctness of the results.
//     */
//    public void testSimpleSelect() 
//        throws SQLException {
//
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .select("person.pk_person")
//                .select("person.vorname")
//                .select("person.nachname")
//                .select("person.geburtstag")
//                .orderBy(Order.asc("person.pk_person"))
//                .getResultSet();
//
//            final int[] primaryKeys = new int[]{1, 2, 3, 4};
//            for (int count = 0; count < primaryKeys.length; count++) {
//                assertTrue("missing data record " + (count + 1), rs.next());
//                checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//                assertEquals("primary key wrong", primaryKeys[count], rs.getInt("pk_person"));
//            }
//
//            assertFalse("surplus data record " + (primaryKeys.length + 1), rs.next());
//        } finally {
//            DB.close(rs);
//        }
//    }
//    
//    /**
//     * This method tests a simple select from the inner join of the two tables "person"
//     * and "firma"; special attention is given to the correctness of the result parts
//     * from either table, the non-emptiness of either part, the correct join of them
//     * and the correct number of results.
//     */
//    public void testInnerJoin() throws SQLException {
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .join("firma", "person.fk_firma", "firma.pk_firma")
//                .selectAs("person.pk_person", "pk_person")
//                .selectAs("person.fk_firma", "fk_firma")
//                .selectAs("firma.pk_firma", "pk_firma")
//                .selectAs("person.vorname", "vorname")
//                .selectAs("person.nachname", "nachname")
//                .selectAs("person.geburtstag", "geburtstag")
//                .selectAs("firma.name", "name")
//                .selectAs("firma.umsatz", "umsatz")
//                .getResultSet();
//            int count = 0;
//            for (; rs.next(); count++) {
//                checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//                checkFirmData(rs.getInt("pk_firma"), rs.getString("name"), rs.getInt("umsatz"));
//                assertFalse("invalid inner join empty right data set", rs.getInt("pk_firma") == 0);
//                assertFalse("invalid inner join empty left data set", rs.getInt("pk_person") == 0);
//                assertEquals("join fields differ", rs.getInt("fk_firma"), rs.getInt("pk_firma"));
//            }
//            assertEquals("result count wrong", count, 3);
//        } finally {
//            DB.close(rs);
//        }
//    }
//
//    /**
//     * This method tests a simple select from the left outer join of the two tables
//     * "person" and "firma"; special attention is given to the correctness of the
//     * result parts from either table, the non-emptiness of the left part, the correct
//     * join of them and the correct number of results.
//     */
//    public void testLeftOuterJoin() throws SQLException {
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .joinLeftOuter("firma", "person.fk_firma", "firma.pk_firma")
//                .selectAs("person.pk_person", "pk_person")
//                .selectAs("person.fk_firma", "fk_firma")
//                .selectAs("firma.pk_firma", "pk_firma")
//                .selectAs("person.vorname", "vorname")
//                .selectAs("person.nachname", "nachname")
//                .selectAs("person.geburtstag", "geburtstag")
//                .selectAs("firma.name", "name")
//                .selectAs("firma.umsatz", "umsatz")
//                .getResultSet();
//            int count = 0;
//            for (; rs.next(); count++) {
//                checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//                checkFirmData(rs.getInt("pk_firma"), rs.getString("name"), rs.getInt("umsatz"));
//                assertFalse("invalid left outer join empty left data set", rs.getInt("pk_person") == 0);
//                assertEquals("join fields differ", rs.getInt("fk_firma"), rs.getInt("pk_firma"));
//            }
//            assertEquals("result count wrong", 4, count);
//        } finally {
//            DB.close(rs);
//        }
//    }
//
//    /**
//     * This method tests a simple select from the right outer join of the two tables
//     * "person" and "firma"; special attention is given to the correctness of the
//     * result parts from either table, the non-emptiness of the right part, the correct
//     * join of them and the correct number of results.
//     */
//    public void testRightOuterJoin() throws SQLException {
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .joinRightOuter("firma", "person.fk_firma", "firma.pk_firma")
//                .selectAs("person.pk_person", "pk_person")
//                .selectAs("person.fk_firma", "fk_firma")
//                .selectAs("firma.pk_firma", "pk_firma")
//                .selectAs("person.vorname", "vorname")
//                .selectAs("person.nachname", "nachname")
//                .selectAs("person.geburtstag", "geburtstag")
//                .selectAs("firma.name", "name")
//                .selectAs("firma.umsatz", "umsatz")
//                .getResultSet();
//            int count = 0;
//            for (; rs.next(); count++) {
//                checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//                checkFirmData(rs.getInt("pk_firma"), rs.getString("name"), rs.getInt("umsatz"));
//                assertFalse("invalid right outer join empty right data set", rs.getInt("pk_firma") == 0);
//                if (rs.getInt("fk_firma") != 0)
//                    assertEquals("join fields differ", rs.getInt("fk_firma"), rs.getInt("pk_firma"));
//            }
//            assertEquals("result count wrong", 4, count);
//        } finally {
//            DB.close(rs);
//        }
//    }
//
//    /**
//     * This method tests a simple select from the full outer join of the two tables
//     * "person" and "firma"; special attention is given to the correctness of the
//     * result parts from either table, the correct join of them and the correct
//     * number of results.
//     */
//    public void testOuterJoin() throws SQLException {
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .joinOuter("firma", "person.fk_firma", "firma.pk_firma")
//                .selectAs("person.pk_person", "pk_person")
//                .selectAs("person.fk_firma", "fk_firma")
//                .selectAs("firma.pk_firma", "pk_firma")
//                .selectAs("person.vorname", "vorname")
//                .selectAs("person.nachname", "nachname")
//                .selectAs("person.geburtstag", "geburtstag")
//                .selectAs("firma.umsatz", "umsatz")
//                .selectAs("firma.name", "name")
//                .getResultSet();
//            int count = 0;
//            for (; rs.next(); count++) {
//                checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//                checkFirmData(rs.getInt("pk_firma"), rs.getString("name"), rs.getInt("umsatz"));
//                if (rs.getInt("fk_firma") != 0)
//                    assertEquals("join fields differ", rs.getInt("fk_firma"), rs.getInt("pk_firma"));
//            }
//            assertEquals("result count wrong", 5, count);
//        } finally {
//            DB.close(rs);
//        }
//    }
//
//    /**
//     * This method tests a simple ordered select from the table "person" using
//     * limit and offset; special attention is given to selection and correctness
//     * of the results. Only {@link Select#selectAs(String, String)} is used to
//     * select columns.
//     */
//    public void testLimit1() 
//        throws SQLException {
//
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .selectAs("person.pk_person", "pk_person")
//                .selectAs("person.vorname", "vorname")
//                .selectAs("person.nachname", "nachname")
//                .selectAs("person.geburtstag", "geburtstag")
//                .orderBy(Order.asc("person.pk_person"))
//                .Limit(new Limit(1, 1))
//                .getResultSet();
//
//            assertTrue("missing data record", rs.next());
//            checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//            assertEquals("wrong data record", 2, rs.getInt("pk_person"));
//        
//            assertFalse("surplus data record", rs.next());
//        } finally {
//            DB.close(rs);
//        }
//    }
//
//    /**
//     * This method tests a simple ordered select from the table "person" using
//     * limit and offset; special attention is given to selection and correctness
//     * of the results. Only {@link Select#selectAs(String)} is used to select
//     * columns.
//     */
//    public void testLimit2() 
//        throws SQLException {
//
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .selectAs("person.pk_person")
//                .selectAs("person.vorname")
//                .selectAs("person.nachname")
//                .selectAs("person.geburtstag")
//                .orderBy(Order.asc("person.pk_person"))
//                .Limit(new Limit(1, 1))
//                .getResultSet();
//
//            assertTrue("missing data record", rs.next());
//            checkPersonData(rs.getInt("person.pk_person"), rs.getString("person.vorname"), rs.getString("person.nachname"), rs.getDate("person.geburtstag"));
//            assertEquals("wrong data record", 2, rs.getInt("person.pk_person"));
//        
//            assertFalse("surplus data record", rs.next());
//        } finally {
//            DB.close(rs);
//        }
//    }
//
//    /**
//     * This method tests a simple ordered select from the table "person" using
//     * limit and offset; special attention is given to selection and correctness
//     * of the results. Only {@link Select#select(String)} is used to select
//     * columns.
//     */
//    public void testLimit3() 
//        throws SQLException {
//
//        ResultSet rs = null;
//        try {
//            rs = SQL.Select(dbc)
//                .from("person")
//                .select("person.pk_person")
//                .select("person.vorname")
//                .select("person.nachname")
//                .select("person.geburtstag")
//                .orderBy(Order.asc("person.pk_person"))
//                .Limit(new Limit(1, 1))
//                .getResultSet();
//        
//            assertTrue("missing data record", rs.next());
//            checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//            assertEquals("wrong data record", rs.getInt("pk_person"), 2);
//        
//            assertFalse("surplus data record", rs.next());
//        } finally {
//            DB.close(rs);
//        }
//    }
//    
//    public void testUnion() throws SQLException{
//    	ResultSet rs = null;
//    	try{
//    		Select select1 = SQL.Select(dbc)
//                .select("person.pk_person")
//                .select("person.vorname")
//                .select("person.nachname")
//                .select("person.geburtstag")
//    			.from("person")
//    			.where(Expr.equal("person.vorname", "Donald"));
//    		Select select2 = SQL.Select(dbc)
//                .select("person.pk_person")
//                .select("person.vorname")
//                .select("person.nachname")
//                .select("person.geburtstag")
//				.from("person")
//				.where(Expr.equal("person.vorname", "Daisy"));
//    		select1.union(select2);
//    		rs = select1.getResultSet(dbc);
//    		assertTrue("missing data record", rs.next());
//    		checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));
//    		assertTrue("missing data record", rs.next());
//    		checkPersonData(rs.getInt("pk_person"), rs.getString("vorname"), rs.getString("nachname"), rs.getDate("geburtstag"));    		
//    		assertFalse("surplus data record", rs.next());
//    	}finally{
//    		DB.close(rs);
//    	}
//    }
//    
//    /**
//     * This method tests a simple ordered select from the table "person" using the
//     * {@link ResultList} helper class; special attention is given to the order and
//     * correctness of the results.<br>
//     * This test releases resources lazily as the finalisation {@link Runnable} of
//     * {@link ResultList} is used to close the {@link ResultSet}, its {@link java.sql.Statement}
//     * and its {@link Connection}.
//     */
//    public void testResultList() throws SQLException {
//        final ResultSet rs = SQL.Select(dbc)
//            .from("person")
//            .select("person.pk_person")
//            .select("person.vorname")
//            .select("person.nachname")
//            .select("person.geburtstag")
//            .orderBy(Order.asc("person.pk_person"))
//            .getResultSet();
//
//        List resultList = new ResultList(new Runnable() { public void run() { DB.close(rs); }},rs);
//        
//        final int[] primaryKeys = new int[]{1, 2, 3, 4};
//        assertEquals("wrong ResultList size", new Integer(primaryKeys.length), new Integer(resultList.size()));
//        for (int count = 0; count < primaryKeys.length; count++) {
//            Object entry = resultList.get(count);
//            assertTrue("ResultList contains a non Map", entry instanceof Map);
//            Map entryMap = (Map) entry;
//            Object entryPk = entryMap.get("pk_person");
//            assertTrue("primary key value is not an Integer", entryPk instanceof Integer);
//            Object entryForename = entryMap.get("vorname");
//            assertTrue("forename value is not a String", entryForename instanceof String);
//            Object entrySurname = entryMap.get("nachname");
//            assertTrue("surname value is not a String", entrySurname instanceof String);
//            Object entryDateOfBirth = entryMap.get("geburtstag");
//            assertTrue("date of birth value is not a String", entryDateOfBirth instanceof Date);
//            checkPersonData(((Integer)entryPk).intValue(), (String) entryForename, (String) entrySurname, (Date)entryDateOfBirth);
//            assertEquals("primary key wrong", primaryKeys[count], ((Integer)entryPk).intValue());
//        }
//    }
//    
//    /**
//     * This method tests a simple ordered select from the table "person" using the
//     * {@link ResultList} helper class many (100) times; special attention is given
//     * to pooling matters.<br>
//     * This methods simply calls {@link #testResultList()} a hundred times; as this
//     * test releases resources lazily we explicitely call garbage collection each time.
//     */
//    public void testResultListx100() throws SQLException {
//        for (int i = 0; i < 100; i++) {
//            testResultList();
//            System.gc();
//        }
//    }
//    
//    //
//    // Helper methods
//    //
//    /**
//     * This helper method checks person data for correctness. 
//     */
//    void checkPersonData(int key, String firstName, String lastname, Date birthday) {
//        switch(key) {
//        case 0:
//            assertNull("firstname not null", firstName);
//            assertNull("lastname not null", lastname);
//            assertNull("birthday not null", birthday);
//            break;
//        case 1:
//            assertEquals("firstname wrong", "Dagobert", firstName);
//            assertEquals("lastname wrong", "Duck", lastname);
//            assertEquals("birthday wrong", new Date(80, 02, 31), birthday);
//            break;
//        case 2:
//            assertEquals("firstname wrong", "Daisy", firstName);
//            assertEquals("lastname wrong", "Duck", lastname);
//            assertEquals("birthday wrong", new Date(80, 02, 11), birthday);
//            break;
//        case 3:
//            assertEquals("firstname wrong", "Donald", firstName);
//            assertEquals("lastname wrong", "Duck", lastname);
//            assertEquals("birthday wrong", new Date(80, 00, 28), birthday);
//            break;
//        case 4:
//            assertEquals("firstname wrong", "Gustav", firstName);
//            assertEquals("lastname wrong", "Gans", lastname);
//            assertEquals("birthday wrong", new Date(80, 10, 26), birthday);
//            break;
//        default:
//            fail("Unexpected person: " + key + ", " + firstName + ' ' + lastname);
//        }
//        logger.debug("Person " + key + "correct");
//    }
//    
//    /**
//     * This helper method checks firm data for correctness. 
//     */
//    void checkFirmData(int key, String name, int sales) {
//        switch(key) {
//        case 0:
//            assertNull("name not null", name);
//            assertEquals("sales not 0", 0, sales);
//            break;
//        case 1:
//            assertEquals("name wrong", "Dagoberts Geldspeicher", name);
//            assertEquals("sales wrong", 100000, sales);
//            break;
//        case 2:
//            assertEquals("name wrong", "Donalds Frittenbude", name);
//            assertEquals("sales wrong", 30, sales);
//            break;
//        case 3:
//            assertEquals("name wrong", "Duesentriebs Werkstatt", name);
//            assertEquals("sales wrong", 3000, sales);
//            break;
//        default:
//            fail("Unexpected firm: " + key + ", " + name);
//        }
//        logger.debug("Firm " + key + "correct");
//    }
//}