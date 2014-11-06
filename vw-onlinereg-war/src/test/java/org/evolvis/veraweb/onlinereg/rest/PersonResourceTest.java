package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by mley on 02.09.14.
 */
public class PersonResourceTest extends AbstractResourceTest<PersonResource>{


    public PersonResourceTest() {
        super(PersonResource.class);
    }

    @BeforeClass
    public static void init() {
        Session s = sessionFactory.openSession();

        Person p = new Person();
        p.setPk(1);
        p.setUsername("exists");
        p.setFirstName("Hans");
        p.setLastName("Wurst");

        s.save(p);
        s.flush();
        s.close();
    }

    @Test
    public void testCreateUser() {
        Person p = resource.createPerson("luke", "Luke", "Skywalker");

        assertEquals(2, p.getPk());
        assertEquals("Luke", p.getFirstname_a_e1());
        assertEquals("Skywalker", p.getLastname_a_e1());
        assertEquals("username:luke", p.getNote_a_e1());
        assertEquals(0, p.getFk_orgunit());
    }

    @Test
    public void testTryCreateExistingUser() {
        Person p = resource.createPerson("exists", "Darth", "Vader");

        assertNull(p);
    }
}
