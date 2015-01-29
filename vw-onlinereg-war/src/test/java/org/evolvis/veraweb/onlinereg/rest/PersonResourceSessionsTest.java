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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void testCreateMediaRepresentatives() {
        // GIVEN
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        // WHEN
        personResource.createPersonPress(1, "maxmustermann", "Antje", "Weber", "w", "maxmustermann@maxmustermann.de", "address", "63123", "city", "country");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetDelegateByUUID() {
        // GIVEN
        List<Person> results = getDummyDelegates();
        Query query = mock(Query.class);
        String uuid = "5195a511-84f4-4981-8959-29248f4e49c1";
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getNamedQuery("Person.getDelegatesByUUID")).thenReturn(query);
        when(query.list()).thenReturn(results);

        // WHEN
        personResource.getDelegatesByUUID(uuid);

        // THEN
        assertEquals(4, results.size());
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }


    @Test
    public void testGetCompanyByUUID() {
        // GIVEN
        Query query = mock(Query.class);
        String uuid = "5195a511-84f4-4981-8959-29248f4e49c1";
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getNamedQuery("Person.getCompanyByUUID")).thenReturn(query);

        // WHEN
        personResource.getCompanyByUUID(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private List<Person> getDummyDelegates() {
        List<Person> results = new ArrayList<Person>();
        results.add(mock(Person.class));
        results.add(mock(Person.class));
        results.add(mock(Person.class));
        results.add(mock(Person.class));
        return results;
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
