package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 22.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonResourceSessionsTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    PersonResource personResource;

    public PersonResourceSessionsTest() {
        personResource = new PersonResource();
        personResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test
    public void testCreatePersonSessionClosed() {
        // GIVEN
        Query query = mock(Query.class);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);

        // WHEN
        personResource.createPerson("maxmustermann", "Max", "Mustermann");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateDelegateSessionClosed() {
        // GIVEN
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        // WHEN
        personResource.createDelegate(1, "maxmustermann", "Max", "Mustermann", "m", "company");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void mockEvent(Session mockitoSession) {
        Event event = mock(Event.class);
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Event.getEvent")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(event);
        when(event.getFk_orgunit()).thenReturn(1);
    }

    private void mockPerson(Session mockitoSession) {
        Person person = mock(Person.class);
        Query query2 = mock(Query.class);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query2);
        when(query2.uniqueResult()).thenReturn(person);
    }
}
