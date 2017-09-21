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
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 26.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventRessourceSessionsTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    EventResource eventResource;

    public EventRessourceSessionsTest() {
        eventResource = new EventResource();
        eventResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test
    public void testListEvents() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        List events = getDummyEvents();
        when(mockitoSession.getNamedQuery("Event.list")).thenReturn(query);
        when(query.list()).thenReturn(events);

        // WHEN
        eventResource.listEvents();

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testListUserEvents() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        Query query2 = mock(Query.class);
        List events = getDummyEvents();
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(query.list()).thenReturn(events);
        when(query.uniqueResult()).thenReturn(1);
        when(mockitoSession.getNamedQuery("Event.list.userevents")).thenReturn(query2);
        when(query2.list()).thenReturn(events);

        // WHEN
        eventResource.listUsersEvents("username");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testIsGuestListFullReturnTrue() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.checkMaxGuestLimit")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger("1"));

        // WHEN
        final Boolean guestListFull = eventResource.isGuestListFull(1);

        // THEN
        assertTrue(guestListFull);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testIsGuestListFullReturnFalse() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.checkMaxGuestLimit")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger("0"));

        // WHEN
        final Boolean guestListFull = eventResource.isGuestListFull(1);

        // THEN
        assertFalse(guestListFull);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }
    
    @Test
    public void testListUserEventsWithoutResults() {
        // GIVEN
        prepareSession();
        List resultList = mock(List.class);
        Query query = mock(Query.class);
        
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(true);

        // WHEN
        List<Event> listEvents = eventResource.listUsersEvents("username");

        // THEN
        assertTrue(listEvents == null);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCheckUserRegistrationToEvents() {
        // GIVEN
        prepareSession();
        List resultList = mock(List.class);
        Query query = mock(Query.class);
        Query query1 = mock(Query.class);

        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(mockitoSession.getNamedQuery("Event.count.userevents")).thenReturn(query1);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);
        when(query.uniqueResult()).thenReturn(42);
        when(query1.uniqueResult()).thenReturn(new BigInteger("1"));

        // WHEN
        Boolean userHastEventRegistrations = eventResource.checkUserRegistrationToEvents("username");

        // THEN
        assertTrue(userHastEventRegistrations);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCheckUserRegistrationToEventsReturnFalse() {
        // GIVEN
        prepareSession();
        List resultList = mock(List.class);
        Query query = mock(Query.class);

        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(true);

        // WHEN
        Boolean userHastEventRegistrations = eventResource.checkUserRegistrationToEvents("username");

        // THEN
        assertFalse(userHastEventRegistrations);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }


    @Test
    public void testGetEvent() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.getEvent")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(mock(Event.class));

        // WHEN
        eventResource.getEvent(1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testExistEventIdByDelegation() {
        // GIVEN
        prepareSession();
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.guestByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(1)));

        // WHEN
        eventResource.existEventIdByDelegation(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testExistEventIdByDelegation2() {
        // GIVEN
        prepareSession();
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.guestByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(0)));

        // WHEN
        eventResource.existEventIdByDelegation(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetEventIdByUUID() {
        // GIVEN
        prepareSession();
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.getEventByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        eventResource.getEventIdByUUID(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testIsOpenEvent() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.isOpen")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(1)));

        // WHEN
        eventResource.isOpenEvent(1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testIsOpenEventTheSecond() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.isOpen")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(0)));

        // WHEN
        eventResource.isOpenEvent(1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetPersonIdByUsername() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        List resultList = mock(List.class);
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);
        when(query.uniqueResult()).thenReturn(0);

        // WHEN
        eventResource.getPersonIdByUsername("username");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetPersonIdByUsernameTheSecond() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        List resultList = mock(List.class);
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(true);

        // WHEN
        Integer result = eventResource.getPersonIdByUsername("username");

        // THEN
        assertNull(result);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetPersonIdByUsernameTheThird() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        List resultList = mock(List.class);
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        eventResource.getPersonIdByUsername("username");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private List getDummyEvents() {
        List<Event> events = new ArrayList<Event>();
        events.add(mock(Event.class));
        events.add(mock(Event.class));
        events.add(mock(Event.class));
        return events;
    }

    private void prepareSession() {
        when(eventResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
