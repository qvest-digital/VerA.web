/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mley on 02.09.14.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventResourceTest extends AbstractResourceTest<EventResource> {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    public EventResourceTest() {
        super(EventResource.class);
    }

    @BeforeClass
    public static void init() {

    }

    @Test
    public void testListEvents() {
        // GIVEN
        Query query = mock(Query.class);
        prepareSession();

        List results = createDummyEvents();
        when(mockitoSession.getNamedQuery("Event.list")).thenReturn(query);
        when(query.list()).thenReturn(results);

        List<Event> events = resource.listEvents();
        assertEquals(6, events.size());
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test
    public void testGetEvent() {
        // GIVEN
        Query query = mock(Query.class);
        prepareSession();

        List results = createDummyEvents();
        when(mockitoSession.getNamedQuery("Event.getEvent")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(results.get(2));

        Event e = resource.getEvent(2);
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

    private List<Event> createDummyEvents() {

        List<Event> events = new ArrayList<Event>();

        Event e = new Event();
        e.setPk(1);
        e.setDatebegin(getFutureDate());
        e.setMaxguest(0);
        e.setMaxreserve(0);
        e.setShortname("shortname");
        e.setEventtype("Offene Veranstaltung");
        events.add(e);

        e = new Event();
        e.setPk(2);
        e.setDatebegin(getFutureDate());
        e.setShortname("event2");
        e.setMaxguest(0);
        e.setMaxreserve(0);
        events.add(e);

        e = new Event();
        e.setPk(3);
        e.setDatebegin(getPastDate());
        e.setMaxguest(0);
        e.setMaxreserve(0);
        e.setShortname("pastEvent");
        e.setEventtype("Offene Veranstaltung");
        events.add(e);

        e = new Event();
        e.setPk(4);
        e.setDatebegin(getPastDate());
        e.setMaxguest(0);
        e.setMaxreserve(0);
        e.setDateend(getFutureDate());
        e.setShortname("activeEvent");
        e.setEventtype("Offene Veranstaltung");
        events.add(e);

        e = new Event();
        e.setPk(5);
        e.setDatebegin(getFutureDate());
        e.setMaxguest(0);
        e.setMaxreserve(0);
        e.setDateend(getFutureDate());
        e.setShortname("futureEvent");
        e.setEventtype("Offene Veranstaltung");
        events.add(e);

        e = new Event();
        e.setPk(6);
        e.setDatebegin(getPastDate());
        e.setMaxguest(0);
        e.setMaxreserve(0);
        e.setDateend(getPastDate());
        e.setShortname("futureEvent");
        e.setEventtype("Offene Veranstaltung");
        events.add(e);

        return events;

    }

    private void prepareSession() {
        when(resource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
