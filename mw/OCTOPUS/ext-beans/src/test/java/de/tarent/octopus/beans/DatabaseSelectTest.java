/*
 * tarent-octopus bean extension,
 * an opensource webservice and webapplication framework (bean extension)
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent-octopus bean extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: DatabaseSelectTest.java,v 1.3 2007/06/11 13:24:36 christoph Exp $
 * 
 * Created on 03.05.2006
 */
package de.tarent.octopus.beans;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import junit.framework.TestCase;
import de.tarent.beans.Person;
import de.tarent.dblayer.SchemaCreator;
import de.tarent.dblayer.sql.statement.Select;
import java.sql.Connection;
import java.sql.SQLException;
import de.tarent.dblayer.engine.DB;

/**
 * This class tests basic bean framework {@link Database} selection features. 
 * 
 * @author Michael Klink
 */
public class DatabaseSelectTest extends TestCase {

    protected static Database testDatabase;
    //
    // variables and constants
    //
    /** Logger for this test */
    public final static Logger logger = Logger.getLogger(DatabaseSelectTest.class.getName());
    
    /** db layer pool name of the test database connection */
    public final static String POOL_NAME = SchemaCreator.TEST_POOL;
    /** package of the bean classes */
    public final static String BEAN_PACKAGE = "de.tarent.beans";
    /** path of the bean property files */
    public final static File BEAN_PROPERTIES_PATH = new File("src/test/beans");
    
    /** Integer 1 */
    public final static Integer INTEGER_1 = new Integer(1);
    /** Integer 2 */
    public final static Integer INTEGER_2 = new Integer(2);
    /** Integer 3 */
    public final static Integer INTEGER_3 = new Integer(3);
    /** Integer 4 */
    public final static Integer INTEGER_4 = new Integer(4);
    
    /** collection of all person ids */
    public final static Set ALL_PERSON_IDS = new TreeSet(Arrays.asList(new Integer[]{INTEGER_1, INTEGER_2, INTEGER_3, INTEGER_4}));
    
    //
    // constructors and Testcase overrides
    //
    /** the empty constructor */
    public DatabaseSelectTest() {
        super();
    }

    /** the constructor with an initial name */
    public DatabaseSelectTest(String init) {
        super(init);
    }

    /** the initialising method used to setup the database schema */
    public void setUp() throws Exception {
    	SchemaCreator schemaCreator = SchemaCreator.getInstance();
    	if (schemaCreator != null)
        	schemaCreator.setUp(false);
    }
    
    //
    // test methods
    //
    /**
     * This test method tries to read a {@link Person} bean from the database.<br>
     * This checks the basic bean attribute mapping abilities.
     * 
     * @see Database#getBean(String, Integer)
     */
    public void testReadPerson1() throws BeanException, IOException {
        Database database = getDatabase();
        if (database == null) return;
        Person person = (Person) database.getBean("Person", INTEGER_1);
        checkData(person);
        assertEquals("wrong person id", INTEGER_1, person.id);
    }

    /**
     * This test method tries to read a {@link Person} bean from the database
     * many (100) times.<br>
     * This checks proper connection pool use.
     * 
     * @see Database#getBean(String, Integer)
     */
    public void testReadPerson1x100() throws BeanException, IOException {
        Database database = getDatabase();
        if (database == null) return;
        final long startTime = System.currentTimeMillis(); 
        for(int i = 0; i < 100; i++) {
            Person person = (Person) database.getBean("Person", INTEGER_1);
            checkData(person);
            assertEquals("wrong person id", INTEGER_1, person.id);
        }
        logger.info("testReadPerson1x100 took " + (System.currentTimeMillis() - startTime) + "ms to execute");
    }

    /**
     * This test method tries to read a {@link List} of {@link Person} beans from
     * the database.<br>
     * This checks the bean mass reading abilities.
     * 
     * @see Database#getBeanList(String, Select)
     */
    public void testReadAllPersons() throws BeanException, IOException {
        Database database = getDatabase();
        if (database == null) return;
        List persons = database.getBeanList("Person", database.getSelect(Person.SAMPLE));
        checkPersonList(persons, ALL_PERSON_IDS, true);
    }
    
    /**
     * This test method tries to read a {@link List} of {@link Person} beans from
     * the database many (100) times.<br>
     * This checks proper connection pool use.
     * 
     * @see Database#getBeanList(String, Select)
     */
    public void testReadAllPersonsx100() throws BeanException, IOException {
        Database database = getDatabase();
        if (database == null) return;
        final long startTime = System.currentTimeMillis(); 
        for(int i = 0; i < 100; i++) {
            List persons = database.getBeanList("Person", database.getSelect(Person.SAMPLE));
            checkPersonList(persons, ALL_PERSON_IDS, true);
        }
        logger.info("testReadAllPersonsx100 took " + (System.currentTimeMillis() - startTime) + "ms to execute");
    }

    /**
     * This test method tries to read a {@link List} of person data {@link Map}
     * instances from the database.<br>
     * This checks the bean mass reading abilities.
     * 
     * @see Database#getList(Select, ExecutionContext)
     */
    public void testReadAllPersonData() throws BeanException, IOException {
        Database database = getDatabase();
        if (database == null) return;
        List personData = database.getList(database.getSelect(Person.SAMPLE), database);
        checkPersonDataList(personData, ALL_PERSON_IDS, true);
    }
    
    /**
     * This test method tries to read a {@link List} of person data {@link Map}
     * instances from the database many (100) times.<br>
     * This checks proper connection pool use.
     * 
     * @see Database#getList(Select, ExecutionContext)
     */
    public void testReadAllPersonDatax100() throws BeanException, IOException {
        Database database = getDatabase();
        if (database == null) return;
        final long startTime = System.currentTimeMillis(); 
        for(int i = 0; i < 100; i++) {
            System.gc();
            List personData = database.getList(database.getSelect(Person.SAMPLE), database);
            checkPersonDataList(personData, ALL_PERSON_IDS, true);
        }
        logger.info("testReadAllPersonDatax100 took " + (System.currentTimeMillis() - startTime) + "ms to execute");
    }

    //
    // Helper methods
    //
    /**
     * This method returns a {@link Database} instance for the test database.
     */
    static Database getDatabase() {
    	if (SchemaCreator.getInstance() == null)
    		return null;
    	
        if (testDatabase == null) {
            testDatabase = new Database(POOL_NAME, BEAN_PROPERTIES_PATH, BEAN_PACKAGE) {
                    Connection defaultConnection = null;
                    public Connection getDefaultConnection() throws SQLException {
                        if (defaultConnection == null)
                            defaultConnection = DB.getConnection(getPoolName());
                        return defaultConnection;
                    }                
                };
        }
        return testDatabase;
    }
    
    /**
     * This helper method checks person data for correctness. 
     */
    void checkData(Person person) {
        assertNotNull("person is NULL", person);
        if (person.id == null) {
            assertNull("forename not null", person.forename);
            assertNull("surname not null", person.surname);
            assertNull("date of birth not null", person.dateOfBirth);
        } else switch(person.id.intValue()) {
        case 1:
            assertEquals("forename wrong", "Dagobert", person.forename);
            assertEquals("surname wrong", "Duck", person.surname);
            assertEquals("date of birth wrong", getDate(80, 02, 31), person.dateOfBirth);
            break;
        case 2:
            assertEquals("forename wrong", "Daisy", person.forename);
            assertEquals("surname wrong", "Duck", person.surname);
            assertEquals("date of birth wrong", getDate(80, 02, 11), person.dateOfBirth);
            break;
        case 3:
            assertEquals("forename wrong", "Donald", person.forename);
            assertEquals("surname wrong", "Duck", person.surname);
            assertEquals("date of birth wrong", getDate(80, 00, 28), person.dateOfBirth);
            break;
        case 4:
            assertEquals("forename wrong", "Gustav", person.forename);
            assertEquals("surname wrong", "Gans", person.surname);
            assertEquals("date of birth wrong", getDate(80, 10, 26), person.dateOfBirth);
            break;
        default:
            fail("Unexpected person: " + person);
        }
        
        logger.fine("Person " + person.id + " correct");
    }
    
    static Date getDate(int year, int month, int date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(year, month, date);
    	return calendar.getTime();
    }
    
    /**
     * This helper method checks a list of persons. It checks whether the
     * list contains only valid {@link Person} instances and whether it
     * contains exactly the persons with the expected ids.
     * 
     * @param persons list of {@link Person} instances to check
     * @param expectedIds set of ids expected in this list
     * @param checkForDoubles if <code>true</code> method will fail when
     *  discovering a double entry
     */
    void checkPersonList(List persons, Set expectedIds, boolean checkForDoubles) {
        assertNotNull("list of persons is NULL", persons);
        Iterator itPersons = persons.iterator();
        Set foundIds = new TreeSet();
        while(itPersons.hasNext()) {
            Object entry = itPersons.next();
            assertNotNull("list of persons contains a NULL entry", entry);
            assertTrue("list of persons contains a non person", entry instanceof Person);
            Person person = (Person) entry;
            checkData(person);
            if (checkForDoubles)
                assertFalse("list of persons contains double with id " + person.id, foundIds.contains(person.id));
            foundIds.add(person.id);
        }
        assertEquals("list does not contain the expected persons", expectedIds, foundIds);
        logger.fine("list of persons correct");
    }

    /**
     * This helper method checks a list of person data Maps. It checks
     * whether the list contains only valid {@link Map} instances with
     * person data and whether it contains data of exactly the persons
     * with the expected ids. 
     * 
     * @param persons list of person data {@link Map} instances to check
     * @param expectedIds set of ids expected in this list
     * @param checkForDoubles if <code>true</code> method will fail when
     *  discovering a double entry
     */
    void checkPersonDataList(List persons, Set expectedIds, boolean checkForDoubles) {
        assertNotNull("list of person data Maps is NULL", persons);
        Iterator itPersons = persons.iterator();
        Set foundIds = new TreeSet();
        while(itPersons.hasNext()) {
            Object entry = itPersons.next();
            assertNotNull("list of person data Maps contains a NULL entry", entry);
            assertTrue("list of person data Maps contains a non Map", entry instanceof Map);
            Map person = (Map) entry;
            //checkData(person);
            Object personId = person.get("id");
            if (checkForDoubles)
                assertFalse("list of persons contains double with id " + personId, foundIds.contains(personId));
            foundIds.add(personId);
        }
        assertEquals("list does not contain the expected person data Maps", expectedIds, foundIds);
        logger.fine("list of person data Maps correct");
    }
}
