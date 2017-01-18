/**
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
package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Guest;
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

import static org.hamcrest.CoreMatchers.any;
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
        guestResource.getGuestId(1,1);

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
        guestResource.addGuestToEvent(uuid,1,1,1,1,"m",1,"","testusername");

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
        doNothing().when(guest).setImage_uuid(String.valueOf(any(String.class)));

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
    }
}
