package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
        guestResource.addGuestToEvent(uuid,1,1,1,1,"m",1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(guestResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
