package org.evolvis.veraweb.onlinereg.rest;

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
        guest.setPk(1);
        guest.setFk_event(1);
        guest.setFk_person(1);
        guest.setNotehost("note");
        guest.setInvitationstatus(0);
        session.persist(guest);

        guest = new Guest();
        guest.setPk(2);
        guest.setFk_event(2);
        guest.setFk_person(1);
        session.persist(guest);

        guest = new Guest();
        guest.setPk(3);
        guest.setFk_event(6);
        guest.setFk_person(1);
        session.persist(guest);

        session.flush();
        session.close();
    }

    @Test
    public void testGetGuest() {
        Guest g = resource.getGuest(1, 1);

        assertEquals("note", g.getNotehost());
        assertEquals(0, g.getInvitationstatus());
    }

    @Test
    public void testSaveGuest() {
        Guest g = resource.saveGuest(1, 1, 2, "new note");
        assertEquals("new note", g.getNotehost());
        assertEquals(2, g.getInvitationstatus());
    }
}
