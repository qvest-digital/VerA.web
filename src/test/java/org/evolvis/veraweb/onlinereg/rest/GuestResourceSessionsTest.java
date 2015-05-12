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

import static org.junit.Assert.assertEquals;
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
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    GuestResource guestResource;

    public GuestResourceSessionsTest() {
        guestResource = new GuestResource();
        guestResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test
    public void testGetGuest() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.findByEventAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(mock(Guest.class));

        // WHEN
        guestResource.getGuest(1,1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testUpdateGuest() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.findByEventAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(mock(Guest.class));

        // WHEN
        guestResource.updateGuest(1, 1, 1, "notehost");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testUserIsRegisteredForEvent() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.checkUserRegistration")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(BigInteger.valueOf(1));

        // WHEN
        guestResource.isUserRegisteredintoEvent("username", 1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testUserIsNotRegisteredForEvent() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.checkUserRegistration")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(BigInteger.valueOf(0));

        // WHEN
        guestResource.isUserRegisteredintoEvent("username", 1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetGuestId() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.findIdByEventAndUser")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        guestResource.getGuestId(1,1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testSaveGuest() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.findByEventAndUser")).thenReturn(query);
        Guest guest = mock(Guest.class);
        when(query.uniqueResult()).thenReturn(guest);
        doNothing().when(mockitoSession).update(guest);

        // WHEN
        guestResource.saveGuest(1,1,1,"host");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testFindEventIdByDelegation() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.findEventIdByDelegationUUID")).thenReturn(query);
        Guest guest = mock(Guest.class);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.findEventIdByDelegation(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testFindGuestByDelegationAndPerson() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.findByDelegationAndUser")).thenReturn(query);
        Guest guest = mock(Guest.class);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        guestResource.findGuestByDelegationAndPerson(uuid, 1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testExistEventIdByDelegationIsTrue() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.guestByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(1)));

        // WHEN
        guestResource.existEventIdByDelegation(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testExistEventIdByDelegationIsFalse() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Guest.guestByUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(new BigInteger(String.valueOf(2)));

        // WHEN
        guestResource.existEventIdByDelegation(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testAddGuestToEvent() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();

        // WHEN
        guestResource.addGuestToEvent(uuid,1,1,1,1,"m",1,"","testusername");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testAddGuestToEventTheSecond() {
        // GIVEN
        prepareSession();

        // WHEN
        guestResource.addGuestToEvent(1, 1, 1, 1, "Herr", 1, "username", "nodehost");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testFindGuestByDelegationUUID() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        Guest guest = mock(Guest.class);
        when(mockitoSession.getNamedQuery("Guest.findByDelegationUUID")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(guest);

        // WHEN
        Guest guestResult = guestResource.findGuestByDelegationUUID(uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(guestResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
