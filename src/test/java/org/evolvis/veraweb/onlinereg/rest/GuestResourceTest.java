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
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mley on 02.09.14.
 */
public class GuestResourceTest extends AbstractResourceTest<GuestResource>{

    public GuestResourceTest() {
        super(GuestResource.class);
    }

    @BeforeClass
    public static void init() {
        Session session = sessionFactory.openSession();

        Guest guest = new Guest();
//        guest.setPk(1);
        guest.setFk_event(1);
        guest.setFk_person(1);
        guest.setNotehost("note");
        guest.setInvitationstatus(0);

        session.persist(guest);

        session.flush();
        session.close();
    }

    @Test
    public void testGetGuest() {
        Guest g = resource.getGuest(1, 1);

        assertEquals("note", g.getNotehost());
        assertEquals(0, g.getInvitationstatus().intValue());
        assertEquals(0, g.getReserve().intValue());
    }

    @Test
    public void testSaveGuest() {
        Guest g = resource.saveGuest(1, 1, 2, "new note", 0);

        assertEquals("new note", g.getNotehost());
        assertEquals(2, g.getInvitationstatus().intValue());
        assertEquals(0, g.getReserve().intValue());
    }
}
