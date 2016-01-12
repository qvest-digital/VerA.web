/**
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
package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Session;
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
        createDummyEvents();
    }

    @Test
    public void testListEvents() {
        List<Event> events = resource.listEvents();
        assertEquals(5, events.size());
    }

    @Test
    public void testGetEvent() {
        Event e = resource.getEvent(3);
        assertEquals("pastEvent", e.getShortname());
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

    private static void createDummyEvents() {
        Session session = sessionFactory.openSession();

        Event e = new Event();
        e.setPk(1);
        e.setDatebegin(getFutureDate());
        e.setShortname("shortname");
        e.setEventtype("Offene Veranstaltung");
        session.persist(e);

        e = new Event();
        e.setPk(2);
        e.setDatebegin(getFutureDate());
        e.setShortname("event2");
        session.persist(e);

        e = new Event();
        e.setPk(3);
        e.setDatebegin(getPastDate());
        e.setShortname("pastEvent");
        e.setEventtype("Offene Veranstaltung");
        session.persist(e);

        e = new Event();
        e.setPk(4);
        e.setDatebegin(getPastDate());
        e.setDateend(getFutureDate());
        e.setShortname("activeEvent");
        e.setEventtype("Offene Veranstaltung");
        session.persist(e);

        e = new Event();
        e.setPk(5);
        e.setDatebegin(getFutureDate());
        e.setDateend(getFutureDate());
        e.setShortname("futureEvent");
        e.setEventtype("Offene Veranstaltung");
        session.persist(e);

        e = new Event();
        e.setPk(6);
        e.setDatebegin(getPastDate());
        e.setDateend(getPastDate());
        e.setShortname("futureEvent");
        e.setEventtype("Offene Veranstaltung");
        session.persist(e);

        session.flush();
        session.close();
    }
}