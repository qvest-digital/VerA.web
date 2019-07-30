package de.tarent.octopus.beans;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
import de.tarent.beans.Product;
import de.tarent.dblayer.SchemaCreator;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * This class tests basic bean framework {@link Database} insert, update
 * and delete features.
 *
 * @author Michael Klink
 */
@Log4j2
public class DatabaseUpdateTest extends TestCase {
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
