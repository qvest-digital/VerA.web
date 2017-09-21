package org.evolvis.veraweb.onlinereg.rest;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    @Ignore
    @Test
    public void testCreateUser() {
        Person p = resource.createPerson("luke", "Luke", "Skywalker", "Luke@Skywalker");
        assertEquals(2, p.getPk());
        assertEquals("Luke", p.getFirstname_a_e1());
        assertEquals("Skywalker", p.getLastname_a_e1());
        assertEquals("luke", p.getUsername());
    }

    @Ignore
    @Test
    public void testTryCreateExistingUser() {
        Person p = resource.createPerson("exists", "Darth", "Vader", "Darth@Vader");

        assertNull(p);
    }
    
}