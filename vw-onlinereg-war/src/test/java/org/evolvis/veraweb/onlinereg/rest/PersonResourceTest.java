package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.AbstractResourceTest;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by mley on 02.09.14.
 */
public class PersonResourceTest extends AbstractResourceTest<PersonResource>{

    SessionFactory secondSessionFactory;

    public PersonResourceTest() {
        super(PersonResource.class);
        secondSessionFactory = mock(SessionFactory.class);
    }

    @BeforeClass
    public static void init() {
        Session s = sessionFactory.openSession();

        Person p = new Person();
        p.setPk(1);
        String username = "exists";
        p.setUsername(username);
        p.setFirstName("Hans");
        p.setLastName("Wurst");
        p.setNote_a_e1("username:" + username);

        s.save(p);
        s.flush();
        s.close();
    }

    @Test
    public void testCreateUser() {
        Person p = resource.createPerson("luke", "Luke", "Skywalker");

        assertEquals(2, p.getPk());
        assertEquals("Luke", p.getFirstname_a_e1());
        assertEquals("Skywalker", p.getLastname_a_e1());
        assertEquals("username:luke", p.getNote_a_e1());
        assertEquals(0, p.getFk_orgunit());
    }

    @Test
    public void testTryCreateExistingUser() {
        Person p = resource.createPerson("exists", "Darth", "Vader");

        assertNull(p);
    }

    @Test
    public void testCreatePersonSessionClosed() {
        // GIVEN
        Session session = mock(Session.class);
        Query query = mock(Query.class);
        when(contextMock.getAttribute("SessionFactory")).thenReturn(secondSessionFactory);
        when(secondSessionFactory.openSession()).thenReturn(session);
        when(session.getNamedQuery("Person.findByUsername")).thenReturn(query);

        // WHEN
        resource.createPerson("gong", "Bonn", "Köln");

        // THEN
        verify(secondSessionFactory, times(1)).openSession();
        verify(session, times(1)).close();

    }

    @Test
    public void testCreateDelegateSessionClosed() {

        // GIVEN
        Event event = mock(Event.class);
        Object person = mock(Person.class);
        Session session = mock(Session.class);
        Query query = mock(Query.class);
        Query query2 = mock(Query.class);
        when(contextMock.getAttribute("SessionFactory")).thenReturn(secondSessionFactory);
        when(secondSessionFactory.openSession()).thenReturn(session);
        when(session.getNamedQuery("Event.getEvent")).thenReturn(query);
        when(session.getNamedQuery("Person.findByUsername")).thenReturn(query2);
        when(query.uniqueResult()).thenReturn(event);
        when(query2.uniqueResult()).thenReturn(person);
        when(event.getFk_orgunit()).thenReturn(1);

        // WHEN
        resource.createDelegate(1, "gong", "Bonn", "Köln", "m", "company");

        // THEN
        verify(secondSessionFactory, times(1)).openSession();
        verify(session, times(1)).close();

    }

}
