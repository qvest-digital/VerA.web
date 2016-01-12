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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.evolvis.veraweb.onlinereg.entities.Event;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit Testing for {@link org.evolvis.veraweb.onlinereg.rest.FreeVisitorsResource}
 * @author jnunez
 */
@RunWith(MockitoJUnitRunner.class)
public class FreeVisitorsResourceTest {
	
	 @Mock
	 private static SessionFactory mockitoSessionFactory;
	 @Mock
	 private static Session mockitoSession;
	 
	 FreeVisitorsResource freeVisitorsResource;

	 public FreeVisitorsResourceTest() {
		 freeVisitorsResource = new FreeVisitorsResource();
		 freeVisitorsResource.context = mock(ServletContext.class);
	 }
	 
	 @Test
	 public void testGetEventByUUID() {
		 // GIVEN
		 String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
		 prepareSession();
		 Event eventMocked = mock(Event.class);
		 Query query = mock(Query.class);
	     when(mockitoSession.getNamedQuery("Event.getEventByHash")).thenReturn(query);
	     when(query.uniqueResult()).thenReturn(eventMocked);
		 
	     // WHEN
		 Event event = freeVisitorsResource.getEventByUUId(uuid);
		 
		 // THEN
		 assertEquals(event,eventMocked);
		 verify(mockitoSessionFactory, times(1)).openSession();
		 verify(mockitoSession, times(1)).close();
	 }
	 
	 @Test
	 public void testGetEventByUUIDWithoutResults() {
		 // GIVEN
		 String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
		 prepareSession();
		 Query query = mock(Query.class);
	     when(mockitoSession.getNamedQuery("Event.getEventByHash")).thenReturn(query);
	     when(query.uniqueResult()).thenReturn(null);
		 
	     // WHEN
		 Event event = freeVisitorsResource.getEventByUUId(uuid);
		 
		 // THEN
		 assertNull(event);
		 verify(mockitoSessionFactory, times(1)).openSession();
		 verify(mockitoSession, times(1)).close();
	 }

	 private void prepareSession() {
		 when(freeVisitorsResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
		 when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
	 }
}
