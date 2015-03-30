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

import org.evolvis.veraweb.onlinereg.entities.PersonDoctype;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import antlr.collections.List;

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
public class PersonDoctypeResourceSessionsTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    PersonDoctypeResource personDoctypeResource;

    public PersonDoctypeResourceSessionsTest() {
        personDoctypeResource = new PersonDoctypeResource();
        personDoctypeResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();

        mockitoSession.disconnect();
        mockitoSession.close();
    }

    @Test
    public void testCreatePersonDoctype() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("PersonDoctype.findByDoctypeIdAndPersonId")).thenReturn(query);

        // WHEN
        personDoctypeResource.createPersonDoctype(1, "Max", "Mustermann");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testCreatePersonDoctypeWithoutResults() {
        // GIVEN
        prepareSession();
        
        Query query = mock(Query.class);
        java.util.List resultList = mock(java.util.List.class);
        
        when(mockitoSession.getNamedQuery("PersonDoctype.findByDoctypeIdAndPersonId")).thenReturn(query);
        when(query.list()).thenReturn(resultList);
        
        // WHEN
        PersonDoctype personDoctype = personDoctypeResource.createPersonDoctype(1, "Max", "Mustermann");

        // THEN
        assertTrue(personDoctype == null);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(personDoctypeResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
