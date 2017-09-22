package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
