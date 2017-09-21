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
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    EventResource eventResource;


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

    @Test
    public void testGetEventByUUID() {
        // GIVEN
        eventResource = new EventResource();
        eventResource.context = mock(ServletContext.class);
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Event eventMocked = mock(Event.class);
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.getEventByHash")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(eventMocked);

        // WHEN
        Event event = eventResource.getEventByUUId(uuid);

        // THEN
        assertEquals(event,eventMocked);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetEventByUUIDWithoutResults() {
        // GIVEN
        eventResource = new EventResource();
        eventResource.context = mock(ServletContext.class);
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.getEventByHash")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // WHEN
        Event event = eventResource.getEventByUUId(uuid);

        // THEN
        assertNull(event);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(eventResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
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