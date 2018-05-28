package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    @Mock
    private static Transaction mockitoTransaction;

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
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);

        // WHEN
        personResource.createPerson("maxmustermann", "Max", "Mustermann", "Max@Max");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).beginTransaction();
        verify(mockitoTransaction, times(1)).commit();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreatePersonSessionClosedWithoutResults() {
        // GIVEN
        Query query = mock(Query.class);
        List resultList = mock(List.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);

        // WHEN
        Person person = personResource.createPerson("maxmustermann", "Max", "Mustermann", "Max@Max");

        // THEN
        assertTrue(person == null);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateDelegateSessionClosedMale() {
        // GIVEN
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        // WHEN
        personResource.createDelegate(1, "maxmustermann", "Max", "Mustermann", "Herr", "category", "function");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).beginTransaction();
        verify(mockitoTransaction, times(1)).commit();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateDelegateSessionClosedFemale() {
        // GIVEN
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        // WHEN
        personResource.createDelegate(1, "lisarosler", "Lisa", "Rosler", "Frau", "category", "function");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateDelegateSessionClosedWithoutResults() {
        // GIVEN
        List resultList = mock(List.class);
        Query query = mock(Query.class);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);

        // WHEN
        Person person = personResource.createDelegate(1, "lisarosler", "Lisa", "Rosler", "Frau", "category", "function");

        // THEN
        assertTrue(person == null);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateMediaRepresentatives() {
        // GIVEN
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        // WHEN
        personResource.createPersonPress(1, "maxmustermann", "Antje", "Weber", "w", "maxmustermann@maxmustermann.de", "address",
          "63123", "city",
          "country");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).beginTransaction();
        verify(mockitoTransaction, times(1)).commit();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateMediaRepresentativesWithoutResults() {
        // GIVEN
        List resultList = mock(List.class);
        Query query = mock(Query.class);
        mockEvent(mockitoSession);
        mockPerson(mockitoSession);

        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);

        // WHEN
        Person personPress = personResource
          .createPersonPress(1, "maxmustermann", "Antje", "Weber", "w", "maxmustermann@maxmustermann.de", "address",
            "63123", "city",
            "country");

        // THEN
        assertTrue(personPress == null);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateMediaRepresentativesTheSecond() {
        // GIVEN
        Query query = mock(Query.class);
        List resultList = mock(List.class);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);
        mockEvent(mockitoSession);

        // WHEN
        Person result = personResource
          .createPersonPress(1, "maxmustermann", "Antje", "Weber", "w", "maxmustermann@maxmustermann.de", "address",
            "63123", "city",
            "country");

        // THEN
        assertNull(result);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreateMediaRepresentativesTheThird() {
        // GIVEN
        Query query = mock(Query.class);
        List resultList = mock(List.class);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(true);
        mockEvent(mockitoSession);

        // WHEN
        Person result = personResource
          .createPersonPress(1, "maxmustermann", "Antje", "Weber", "w", "maxmustermann@maxmustermann.de", "address",
            "63123", "city",
            "country");

        // THEN
        assertNull(result);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetDelegateByUUID() {
        // GIVEN
        List<Person> results = getDummyDelegates();
        Query query = mock(Query.class);
        String uuid = "5195a511-84f4-4981-8959-29248f4e49c1";
        prepareSession();
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
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.getCompanyByUUID")).thenReturn(query);

        // WHEN
        personResource.getCompanyByUUID(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testUpdateOrgunit() {
        // GIVEN
        Query query = mock(Query.class);
        Person person = mock(Person.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findByPersonId")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(person);

        // WHEN
        personResource.updatePersonOrgunit(1, 1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).beginTransaction();
        verify(mockitoTransaction, times(1)).commit();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetUserIdFromUsername() {
        // GIVEN
        Query query = mock(Query.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        List resultList = mock(List.class);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        personResource.getUserIdFromUsername("username");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetUserIdFromUsernameSecond() {
        // GIVEN
        Query query = mock(Query.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findPersonIdByUsername")).thenReturn(query);
        List resultList = mock(List.class);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(true);

        // WHEN
        Integer result = personResource.getUserIdFromUsername("username");

        // THEN
        assertNull(result);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(personResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
        when(mockitoSession.getTransaction()).thenReturn(mockitoTransaction);
    }

    @Test
    public void testGetPersonByUsername() {
        // GIVEN
        Person person = mock(Person.class);
        Query query = mock(Query.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(person);

        // WHEN
        personResource.getPersonByUsername("username");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetFirstAndLastName() {
        // GIVEN
        Query query = mock(Query.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.getPersonNamesByUsername")).thenReturn(query);
        when(query.uniqueResult()).thenReturn("Firstname Lastname");

        // WHEN
        personResource.getFirstAndLastName("username");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetUsernameByUserId() {
        // GIVEN
        Person person = mock(Person.class);
        Query query = mock(Query.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findByPersonId")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(person);

        // WHEN
        personResource.getUsernameByUserId(1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testUpdateDelegate() {
        // GIVEN
        Person person = mock(Person.class);
        Query query = mock(Query.class);
        prepareSession();
        when(mockitoSession.getNamedQuery("Person.findByPersonId")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(person);

        // WHEN
        Person personResult = personResource.updateDelegate("max", "mustermann", "m", null, 1);

        // THEN
        assertNotNull(personResult);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).beginTransaction();
        verify(mockitoTransaction, times(1)).commit();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testUpdatePersonCoreData() {
        // GIVEN
        prepareSession();
        Person person = mock(Person.class);
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Person.findByUsername")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(person);

        // WHEN
        personResource
          .updatePersonCoreData("username", "salutation", 1, "title", "firstname", "lastname", 1L, "de", "de", "gender");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).beginTransaction();
        verify(mockitoTransaction, times(1)).commit();
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
