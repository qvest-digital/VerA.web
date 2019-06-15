package de.tarent.octopus.beans;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
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

import de.tarent.beans.Person;
import de.tarent.dblayer.SchemaCreator;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.sql.statement.Select;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class tests basic bean framework {@link Database} selection features.
 *
 * @author Michael Klink
 */
@Log4j2
public class DatabaseSelectTest extends TestCase {
    protected static Database testDatabase;

    /**
     * db layer pool name of the test database connection
     */
    public final static String POOL_NAME = SchemaCreator.TEST_POOL;
    /**
     * package of the bean classes
     */
    public final static String BEAN_PACKAGE = "de.tarent.beans";
    /**
     * path of the bean property files
     */
    public final static File BEAN_PROPERTIES_PATH = new File("src/test/beans");

    /**
     * Integer 1
     */
    public final static Integer INTEGER_1 = new Integer(1);
    /**
     * Integer 2
     */
    public final static Integer INTEGER_2 = new Integer(2);
    /**
     * Integer 3
     */
    public final static Integer INTEGER_3 = new Integer(3);
    /**
     * Integer 4
     */
    public final static Integer INTEGER_4 = new Integer(4);

    /**
     * collection of all person ids
     */
    public final static Set ALL_PERSON_IDS =
      new TreeSet(Arrays.asList(new Integer[] { INTEGER_1, INTEGER_2, INTEGER_3, INTEGER_4 }));

    //
    // constructors and Testcase overrides
    //

    /**
     * the empty constructor
     */
    public DatabaseSelectTest() {
        super();
    }

    /**
     * the constructor with an initial name
     */
    public DatabaseSelectTest(String init) {
        super(init);
    }

    /**
     * the initialising method used to setup the database schema
     */
    public void setUp() throws Exception {
        SchemaCreator schemaCreator = SchemaCreator.getInstance();
        if (schemaCreator != null) {
            schemaCreator.setUp(false);
        }
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
        if (database == null) {
            return;
        }
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
        if (database == null) {
            return;
        }
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
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
        if (database == null) {
            return;
        }
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
        if (database == null) {
            return;
        }
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
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
        if (database == null) {
            return;
        }
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
        if (database == null) {
            return;
        }
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
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
        if (SchemaCreator.getInstance() == null) {
            return null;
        }

        if (testDatabase == null) {
            testDatabase = new Database(POOL_NAME, BEAN_PROPERTIES_PATH, BEAN_PACKAGE) {
                Connection defaultConnection = null;

                public Connection getDefaultConnection() throws SQLException {
                    if (defaultConnection == null) {
                        defaultConnection = DB.getConnection(getPoolName());
                    }
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
        } else {
            switch (person.id.intValue()) {
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
        }

        logger.debug("Person " + person.id + " correct");
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
     * @param persons         list of {@link Person} instances to check
     * @param expectedIds     set of ids expected in this list
     * @param checkForDoubles if <code>true</code> method will fail when
     *                        discovering a double entry
     */
    void checkPersonList(List persons, Set expectedIds, boolean checkForDoubles) {
        assertNotNull("list of persons is NULL", persons);
        Iterator itPersons = persons.iterator();
        Set foundIds = new TreeSet();
        while (itPersons.hasNext()) {
            Object entry = itPersons.next();
            assertNotNull("list of persons contains a NULL entry", entry);
            assertTrue("list of persons contains a non person", entry instanceof Person);
            Person person = (Person) entry;
            checkData(person);
            if (checkForDoubles) {
                assertFalse("list of persons contains double with id " + person.id, foundIds.contains(person.id));
            }
            foundIds.add(person.id);
        }
        assertEquals("list does not contain the expected persons", expectedIds, foundIds);
        logger.debug("list of persons correct");
    }

    /**
     * This helper method checks a list of person data Maps. It checks
     * whether the list contains only valid {@link Map} instances with
     * person data and whether it contains data of exactly the persons
     * with the expected ids.
     *
     * @param persons         list of person data {@link Map} instances to check
     * @param expectedIds     set of ids expected in this list
     * @param checkForDoubles if <code>true</code> method will fail when
     *                        discovering a double entry
     */
    void checkPersonDataList(List persons, Set expectedIds, boolean checkForDoubles) {
        assertNotNull("list of person data Maps is NULL", persons);
        Iterator itPersons = persons.iterator();
        Set foundIds = new TreeSet();
        while (itPersons.hasNext()) {
            Object entry = itPersons.next();
            assertNotNull("list of person data Maps contains a NULL entry", entry);
            assertTrue("list of person data Maps contains a non Map", entry instanceof Map);
            Map person = (Map) entry;
            //checkData(person);
            Object personId = person.get("id");
            if (checkForDoubles) {
                assertFalse("list of persons contains double with id " + personId, foundIds.contains(personId));
            }
            foundIds.add(personId);
        }
        assertEquals("list does not contain the expected person data Maps", expectedIds, foundIds);
        logger.debug("list of person data Maps correct");
    }
}
