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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 22.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryResourceSessionsTest {
	
    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    CategoryResource categoryResource;

    public CategoryResourceSessionsTest() {
        categoryResource = new CategoryResource();
        categoryResource.context = mock(ServletContext.class);
    }

    @Test
    public void testGetCategoryId() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Category.findIdByCatname")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        categoryResource.getCategoryId("category-name", uuid);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetCategoryIdNotExists() {
        // GIVEN
        String uuid = "534707a6-f432-4f6b-9e6a-c1032f221a50";
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Category.findIdByCatname")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // WHEN
        Integer categoryId = categoryResource.getCategoryId("category-name", uuid);

        // THEN
        assertEquals(new Long(categoryId),new Long(0));
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetCategoriesByEventId() {
        // GIVEN
        prepareSession();
        List<String> resultList = mock(List.class);
        Query query = mock(Query.class);

        when(mockitoSession.getNamedQuery("Category.findCatnamesByEventId")).thenReturn(query);
        when(query.list()).thenReturn(resultList);

        // WHEN
        categoryResource.getCategoriesByEventId(1);

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    @Test
    public void testGetCategoryIdByCategoryName() {
        // GIVEN
        prepareSession();
        Query query = mock(Query.class);
        when(mockitoSession.getNamedQuery("Category.getCategoryIdByCategoryName")).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1);

        // WHEN
        categoryResource.getCategoryIdByCategoryName("NeueKategorie");

        // THEN
        verify(mockitoSessionFactory, times(1)).openSession();
        verify(mockitoSession, times(1)).close();
    }

    private void prepareSession() {
        when(categoryResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }

}