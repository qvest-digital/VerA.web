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

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 22.01.15.
 */
public class PersonResourceSessionsTest extends AbstractResourceTest<PersonResource> {

    private static SessionFactory mockitoSessionFactory;
    private static Session mockitoSession;

    public PersonResourceSessionsTest() {
        super(PersonResource.class);
    }

    @BeforeClass
    public static void setUp() {
        mockitoSessionFactory = mock(SessionFactory.class);
        mockitoSession = mock(Session.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test@Ignore
    public void testCreatePersonSessionClosed() {
        // GIVEN
        Query query = mock(Query.class);
        when(contextMock.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);

        // WHEN
        resource.createPerson("maxmustermann", "Max", "Mustermann");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test@Ignore
    public void testCreateDelegateSessionClosed() {
        // GIVEN
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(contextMock.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        // WHEN
        resource.createDelegate(1, "maxmustermann", "Max", "Mustermann", "m", "company");

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
