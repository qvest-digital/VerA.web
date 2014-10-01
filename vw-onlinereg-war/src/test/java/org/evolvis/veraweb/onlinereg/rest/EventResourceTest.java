package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Session;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;
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

        Session session = sessionFactory.openSession();

        Date date = getFutureDate();

        Event e = new Event();
        e.setPk(1);
        e.setDatebegin(date);
        e.setShortname("shortname");
        session.persist(e);


        e = new Event();
        e.setPk(2);
        e.setDatebegin(date);
        e.setShortname("event2");
        session.persist(e);

        session.flush();
        session.close();
    }

    @Test @Ignore
    public void testListEvents() {
        List<Event> events = resource.listEvents();
        assertEquals(2, events.size());
    }

    @Test @Ignore
    public void testFilterEventsInThePast() {
        // GIVEN
        addPastEvents();

        // WHEN
        List<Event> events = resource.listEvents();

        // THEN
        assertEquals(2, events.size());
    }

    @Test
    public void testShowEventsWithPastBeginAndFutureEndDate() {
        // GIVEN
        addEventWithPastBeginAndFutureEnd();

        // WHEN
        List<Event> events = resource.listEvents();

        // THEN
        assertEquals(3, events.size());
    }

    @Test @Ignore
    public void testShowEventsWithFutureBeginAndEndDate() {
        // GIVEN
        addEventsWithFutureBeginAndEndDate();

        // WHEN
        List<Event> events = resource.listEvents();

        // THEN
        assertEquals(3, events.size());
    }

    @Test
    public void testGetEvent() {
        Event e = resource.getEvent(1);
        assertEquals("shortname", e.getShortname());
    }

    private static Date getFutureDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 15); // Adds 15 days
        return calendar.getTime();
    }

    private static Date getPastDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -15); // Removes 15 days
        return calendar.getTime();
    }

    private void addPastEvents() {
        Session session = sessionFactory.openSession();

        Event e = new Event();
        e.setPk(3);
        e.setDatebegin(getPastDate());
        e.setShortname("pastEvent");

        persistEvent(session, e);
    }

    private void addEventWithPastBeginAndFutureEnd() {
        Session session = sessionFactory.openSession();

        Event e = new Event();
        e.setPk(4);
        e.setDatebegin(getPastDate());
        e.setDateend(getFutureDate());
        e.setShortname("activeEvent");

        persistEvent(session, e);
    }

    private void addEventsWithFutureBeginAndEndDate() {
        Session session = sessionFactory.openSession();

        Event e = new Event();
        e.setPk(5);
        e.setDatebegin(getFutureDate());
        e.setDateend(getFutureDate());
        e.setShortname("activeEvent");

        persistEvent(session, e);
    }

    private void persistEvent(Session session, Event e) {
        session.persist(e);
        session.flush();
        session.close();
    }
}
