package de.tarent.octopus.beans;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import java.io.IOException;
import java.util.logging.Logger;

import de.tarent.beans.Person;
import de.tarent.beans.Product;
import de.tarent.dblayer.SchemaCreator;

import junit.framework.TestCase;

/**
 * This class tests basic bean framework {@link Database} insert, update
 * and delete features.
 *
 * @author Michael Klink
 */
public class DatabaseUpdateTest extends TestCase {
    //
    // variables and constants
    //
    /**
     * Logger for this test
     */
    public final static Logger logger = Logger.getLogger(DatabaseUpdateTest.class.getName());

    /**
     * Integer 5
     */
    public final static Integer INTEGER_5 = new Integer(5);

    //
    // constructors and Testcase overrides
    //

    /**
     * the empty constructor
     */
    public DatabaseUpdateTest() {
        super();
    }

    /**
     * the constructor with an initial name
     */
    public DatabaseUpdateTest(String init) {
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
     * This test method tries to save a new {@link Person} bean to the database,
     * update it and delete it afterwards.<br>
     * This checks the basic bean insert, update and delete abilities on tables
     * with sequence entries.
     *
     * @see Database#saveBean(Bean)
     * @see Database#removeBean(Bean)
     */
    public void testPerson5() throws BeanException, IOException {
        Database database = DatabaseSelectTest.getDatabase();
        if (database == null) {
            return;
        }
        Person person = createNewPerson();
        Integer testPersonId = INTEGER_5;

        Person personInDB = (Person) database.getBean("Person", testPersonId);
        assertNull("unexpected person in database", personInDB);

        database.saveBean(person);
        assertEquals("unexpected id for new person", testPersonId, person.id);

        personInDB = (Person) database.getBean("Person", testPersonId);
        assertEquals("wrong new person in database", person, personInDB);

        person.firmId = DatabaseSelectTest.INTEGER_3;
        database.saveBean(person);
        assertEquals("unexpected id for updated person", testPersonId, person.id);

        personInDB = (Person) database.getBean("Person", testPersonId);
        assertEquals("wrong updated person in database", person, personInDB);

        database.removeBean(person);
        personInDB = (Person) database.getBean("Person", testPersonId);
        assertNull("unexpected deleted person in database", personInDB);
    }

    /**
     * This test method tries to save a new {@link Person} bean to the database
     * not requiring the retrieval of the new id.<br>
     * This checks the alternate basic bean insert abilities on tables
     * with sequence entries.
     *
     * @see Database#saveBean(Bean, ExecutionContext, boolean)
     */
    public void testPerson5NoIdUpdate() throws BeanException, IOException {
        Database database = DatabaseSelectTest.getDatabase();
        if (database == null) {
            return;
        }
        Person person = createNewPerson();
        Integer testPersonId = INTEGER_5;

        Person personInDB = (Person) database.getBean("Person", testPersonId);
        assertNull("unexpected person in database", personInDB);

        database.saveBean(person, database, false);
        person.id = testPersonId;

        personInDB = (Person) database.getBean("Person", testPersonId);
        assertEquals("wrong new person in database", person, personInDB);

        database.removeBean(person);
    }

    /**
     * This test method tries to save new {@link Product} beans to the database,
     * the first one not requiring the retrieval of the new id, the second one
     * requiring it.<br>
     * This checks the basic bean insert abilities on tables
     * without sequence entries using serial fields.
     *
     * @see Database#saveBean(Bean, ExecutionContext, boolean)
     * @see Database#saveBean(Bean)
     */
    public void testProduct2and3() throws BeanException, IOException {
        if (SchemaCreator.getInstance() == null) {
            return;
        }

        if (SchemaCreator.getInstance().isSupportingSerials()) {
            Database database = DatabaseSelectTest.getDatabase();
            if (database == null) {
                return;
            }
            Product product = createNewProduct();
            Integer testProductId2 = DatabaseSelectTest.INTEGER_2;
            Integer testProductId3 = DatabaseSelectTest.INTEGER_3;

            Product productInDB = (Product) database.getBean("Product", testProductId2);
            assertNull("unexpected product in database", productInDB);

            database.saveBean(product, database, false);
            product.id = testProductId2;

            productInDB = (Product) database.getBean("Product", testProductId2);
            assertEquals("wrong new product in database", product, productInDB);

            database.removeBean(product);
            productInDB = (Product) database.getBean("Product", testProductId2);
            assertNull("unexpected deleted product in database", productInDB);

            product.id = null;
            database.saveBean(product);
            assertEquals("unexpected id for new product", testProductId3, product.id);

            productInDB = (Product) database.getBean("Product", testProductId3);
            assertEquals("wrong new product in database", product, productInDB);

            database.removeBean(product);
        } else {
            logger.info("No insert tests without sequence usage executed as test database does not support serials");
        }
    }

    //
    // Helper methods
    //

    /**
     * This method creates a {@link Person} bean that is not yet saved to the
     * database.
     */
    Person createNewPerson() {
        Person person = new Person();
        person.forename = "Daniel";
        person.surname = "Düsentrieb";
        person.dateOfBirth = DatabaseSelectTest.getDate(77, 3, 6);
        return person;
    }

    /**
     * This method creates a {@link Product} bean that is not yet saved to the
     * database.
     */
    Product createNewProduct() {
        Product product = new Product();
        product.firmId = DatabaseSelectTest.INTEGER_3;
        product.name = "Ornithopter";
        return product;
    }
}
