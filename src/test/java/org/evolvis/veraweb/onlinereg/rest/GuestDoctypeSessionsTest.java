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

import java.util.List;

import org.evolvis.veraweb.onlinereg.entities.GuestDoctype;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 26.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GuestDoctypeSessionsTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    GuestDoctypeResource guestDoctypeResource;

    public GuestDoctypeSessionsTest() {
        guestDoctypeResource = new GuestDoctypeResource();
        guestDoctypeResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test
    public void testCreateGuestDoctype() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("GuestDoctype.findByDoctypeIdAndGuestId")).thenReturn(query);

        // WHEN
        guestDoctypeResource.createGuestDoctype(1, "Max", "Mustermann");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }
    
    @Test
    public void testCreateGuestDoctypeWithoutResults() {
        // GIVEN
        prepareSession();
        List resultList = mock(List.class);
        
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("GuestDoctype.findByDoctypeIdAndGuestId")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        when(resultList.isEmpty()).thenReturn(false);

        // WHEN
        List<GuestDoctype> listResults = (List<GuestDoctype>) guestDoctypeResource.createGuestDoctype(1, "Max", "Mustermann");
        
        // THEN
        assertTrue(listResults == null);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(guestDoctypeResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
