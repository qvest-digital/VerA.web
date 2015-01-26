package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
