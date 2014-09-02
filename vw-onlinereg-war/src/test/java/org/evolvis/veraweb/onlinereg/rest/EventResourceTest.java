package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mley on 02.09.14.
 */
public class EventResourceTest extends AbstractResourceTest<EventResource> {


    public EventResourceTest() {
        super(EventResource.class);
    }

    @BeforeClass
    public static void init() {

        Session s = sessionFactory.openSession();


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
        List<Event> events = resource.listEvents();
        assertEquals(2, events.size());
    }

    @Test
    public void testGetEvent() {
        Event e = resource.getEvent(1);
        assertEquals("shortname", e.getShortname());
    }


}
