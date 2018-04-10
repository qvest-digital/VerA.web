package de.tarent.octopus.beans;

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
    /** Logger for this test */
    public final static Logger logger = Logger.getLogger(DatabaseUpdateTest.class.getName());

    /** Integer 5 */
    public final static Integer INTEGER_5 = new Integer(5);

    //
    // constructors and Testcase overrides
    //
    /** the empty constructor */
    public DatabaseUpdateTest() {
	super();
    }

    /** the constructor with an initial name */
    public DatabaseUpdateTest(String init) {
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
	if (database == null) return;
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
	if (database == null) return;
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
	if (SchemaCreator.getInstance() == null)
		return;

	if (SchemaCreator.getInstance().isSupportingSerials()) {
	    Database database = DatabaseSelectTest.getDatabase();
	    if (database == null) return;
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
