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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.evolvis.veraweb.onlinereg.entities.LinkUUID;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit Testing for {@link org.evolvis.veraweb.onlinereg.rest.LinkUUIDResource}
 * @author jnunez
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkUUIDResourceTest {

    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;
    
    LinkUUIDResource linkUUIDResource;
    
    public LinkUUIDResourceTest() {
    	linkUUIDResource = new LinkUUIDResource();
    	linkUUIDResource.context = mock(ServletContext.class);
    }

    @AfterClass
    public static void tearDown() {
        mockitoSessionFactory.close();
        mockitoSession.disconnect();
        mockitoSession.close();
    }
    
    @Test
    public void testGetUserIdByUUID() {
    	// GIVEN
    	prepareSession();
    	int number = 1234;
    	String uuid = "5195a511-84f4-4981-8959-29248f4e49c1";
    	List<LinkUUID> listLinkUUID = getDummyLinkUUID();
    	Query query = mock(Query.class);
    	
    	when(mockitoSession.getNamedQuery("LinkUUID.getUserIdByUUID")).thenReturn(query);
    	when(query.list()).thenReturn(listLinkUUID);
    	when(query.uniqueResult()).thenReturn(number);
    	
    	// WHEN
    	Integer personId = linkUUIDResource.getUserIdByUUID(uuid);
    	
    	 // THEN
    	assertEquals(new Long(number), new Long(personId));
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }
    
    @Test
    public void testGetUserIdByUUIDWithoutResults() {
    	// GIVEN
    	prepareSession();
    	int number = 1234;
    	String uuid = "5195a511-84f4-4981-8959-29248f4e49c1";
    	List resultList = mock(List.class);
    	Query query = mock(Query.class);
    	
    	when(mockitoSession.getNamedQuery("LinkUUID.getUserIdByUUID")).thenReturn(query);
    	when(query.list()).thenReturn(resultList);
    	when(query.list().isEmpty()).thenReturn(true);
    	
    	// WHEN
    	Integer personId = linkUUIDResource.getUserIdByUUID(uuid);
    	
    	 // THEN
    	assertNull(personId);
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private List<LinkUUID> getDummyLinkUUID() {
    	List<LinkUUID> listToReturn = new ArrayList<LinkUUID>();

    	listToReturn.add(mock(LinkUUID.class));
    	listToReturn.add(mock(LinkUUID.class));
    	listToReturn.add(mock(LinkUUID.class));
    	listToReturn.add(mock(LinkUUID.class));
    	listToReturn.add(mock(LinkUUID.class));
    	
    	return listToReturn;
    }
    private void prepareSession() {
        when(linkUUIDResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }
}
