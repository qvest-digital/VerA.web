package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
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
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 22.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GuestResourceSessionsTest {
    @Mock
    private static SessionFactory sessionFactory;
    @Mock
    private static Session session;
    @Mock
    private static Transaction mockitoTransaction;

    GuestResource guestResource;

    public GuestResourceSessionsTest() {
        guestResource = new GuestResource();
        guestResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        sessionFactory.close();

        session.disconnect();
        session.close();
    }

    @Test
    public void testGetGuest() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.findByEventAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(mock(Guest.class));

        // WHEN
        guestResource.getGuest(1, 1);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testUpdateGuest() {
        // GIVEN
        prepareSession();
        final BigInteger maxGuests = getMaxGuests();

        Query query = mock(Query.class);
        final Guest guest = mock(Guest.class);
        when(session.getNamedQuery("Guest.findByEventAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.updateGuest(1, 1, 1, "notehost");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
        verify(session, times(1)).update(anyObject());
        verify(session, times(1)).flush();
    }

    private BigInteger getMaxGuests() {
        final Query query = mock(Query.class);
        final BigInteger maxGuests = new BigInteger(String.valueOf(100));
        when(session.getNamedQuery("Event.checkMaxGuestLimit")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(maxGuests);
        return maxGuests;
    }

    @Test
    public void testUserIsRegisteredForEvent() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.checkUserRegistration")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(BigInteger.valueOf(1));

        // WHEN
        guestResource.isUserRegisteredintoEvent("username", 1);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testUserIsNotRegisteredForEvent() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.checkUserRegistration")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(BigInteger.valueOf(0));

        // WHEN
        guestResource.isUserRegisteredintoEvent("username", 1);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testGetGuestId() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.findIdByEventAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        guestResource.getGuestId(1, 1);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testSaveGuest() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.findByEventAndUser")).thenReturn(query);
        Guest guest = mock(Guest.class);
        when(query.uniqueResult()).thenReturn(guest);
        doNothing().when(session).update(guest);

        // WHEN
        guestResource.saveGuest(1, 1, 1, "host", 0);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testFindEventIdByDelegation() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.findEventIdByDelegationUUID")).thenReturn(query);
        Guest guest = mock(Guest.class);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.findGuestByEventWithDelegationUUID(uuid);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testFindGuestByDelegationAndPerson() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.findByDelegationAndUser")).thenReturn(query);
        Guest guest = mock(Guest.class);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.findGuestByDelegationAndPerson(uuid, 1);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testExistEventIdByDelegationIsTrue() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.guestByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(1)));

        // WHEN
        guestResource.existEventIdByDelegation(uuid);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testExistEventIdByDelegationIsFalse() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.guestByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(2)));

        // WHEN
        guestResource.existEventIdByDelegation(uuid);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testAddGuestToEvent() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();

        // WHEN
        guestResource.addGuestToEvent(uuid, 1, 1, 1, 1, "m", 1, "", "testusername");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testAddGuestToEventTheSecond() {
        // GIVEN
        prepareSession();

        // QUERIES
        Query findIdByEventAndUserQuery = mock(Query.class);
        when(session.getNamedQuery("Guest.findIdByEventAndUser")).thenReturn(findIdByEventAndUserQuery);
        when(findIdByEventAndUserQuery.uniqueResult()).thenReturn(null);

        // WHEN
        guestResource.addGuestToEvent(1, 1, 1, 1, "Herr", 1, "username", "nodehost", 0);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testFindGuestByDelegationUUID() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        Guest guest = mock(Guest.class);
        when(session.getNamedQuery("Guest.findByDelegationUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        Guest guestResult = guestResource.findGuestByDelegationUUID(uuid);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testUpdateGuestWithoutLogin() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        Guest guest = mock(Guest.class);
        when(session.getNamedQuery("Guest.findByNoLoginUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.updateGuestWithoutLogin(uuid, 1, "Notiz");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testIsUserRegisteredintoEventByUUID() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.checkUserRegistrationWithoutLogin")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger("1"));

        // WHEN
        Boolean userRegisteredintoEventByUUID = guestResource.isUserRegisteredintoEventByUUID(uuid, 1);

        // THEN
        assertTrue(userRegisteredintoEventByUUID);
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testIsUserRegisteredintoEventByUUIDFailed() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.checkUserRegistrationWithoutLogin")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger("0"));

        // WHEN
        Boolean userRegisteredintoEventByUUID = guestResource.isUserRegisteredintoEventByUUID(uuid, 1);

        // THEN
        assertFalse(userRegisteredintoEventByUUID);
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testIsUserRegisteredintoEventByDelegation() {
        // GIVEN
        String delegation = "delegation";
        String username = "username";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.isGuestForEvent")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger("1"));

        // WHEN
        Boolean isUserRegisteredintoEventByDelegation = guestResource.isUserRegisteredintoEventByDelegation(username,
                delegation);

        // THEN
        assertTrue(isUserRegisteredintoEventByDelegation);
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testIsUserRegisteredintoEventByDelegationFailed() {
        // GIVEN
        String delegation = "delegation";
        String username = "username";
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.isGuestForEvent")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger("0"));

        // WHEN
        Boolean isUserRegisteredintoEventByDelegation = guestResource.isUserRegisteredintoEventByDelegation(username,
                delegation);

        // THEN
        assertFalse(isUserRegisteredintoEventByDelegation);
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testGetGuestImageUUID() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.findImageByDelegationAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn("1");

        // WHEN
        guestResource.getGuestImageUUID("delegationUUID", 2);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testUpdateGuestEntity() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        Guest guest = mock(Guest.class);
        when(session.getNamedQuery("Guest.getGuestById")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.updateGuestEntity(2, "delegationUUID");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
        verify(session, times(1)).flush();
        verify(session, times(1)).update(anyObject());
    }

    @Test
    public void testIsUserRegisteredintoEventToAccept() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.checkUserRegistrationToAccept")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(1)));

        // WHEN
        guestResource.isUserRegisteredintoEventToAccept("username", 1);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    @Test
    public void testIsReserveTheFirst() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.isReserve")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        final Boolean isReserve = guestResource.isReserve(1, "username");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
        assertEquals(true, isReserve);
    }

    @Test
    public void testIsReserveTheSecond() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(session.getNamedQuery("Guest.isReserve")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(0);

        // WHEN
        final Boolean isReserve = guestResource.isReserve(1, "username");

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
        assertEquals(false, isReserve);
    }

    private void prepareSession() {
        when(guestResource.context.getAttribute("SessionFactory")).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn(mockitoTransaction);
    }
}
