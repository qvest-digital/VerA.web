package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.WarTestSuite;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mley on 02.09.14.
 */
public class EventResourceTest extends EventResource {

    private static SessionFactory sessionFactory;

    @Override
    public Session openSession() {
        return WarTestSuite.H2.sessionFactory.openSession();
    }

    @BeforeClass
    public static void init() {

        Session s = WarTestSuite.H2.sessionFactory.openSession();


        Event e = new Event();
        e.setPk(1);
        e.setDatebegin(new Date());
        e.setShortname("shortname");
        s.persist(e);


        e = new Event();
        e.setPk(2);
        e.setDatebegin(new Date());
        e.setShortname("event2");
        s.persist(e);

        s.flush();
        s.close();

    }

    @Test
    public void testListEvents() {
        List<Event> events= listEvents();
        assertEquals(2, events.size());
    }

    @Test
    public void testGetEvent() {
        Event e = getEvent(1);
        assertEquals("shortname", e.getShortname());
    }


}
