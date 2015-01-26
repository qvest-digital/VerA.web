package org.evolvis.veraweb.onlinereg.rest;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by aalexa on 22.01.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryResourceTest {
    @Mock
    private static SessionFactory mockitoSessionFactory;
    @Mock
    private static Session mockitoSession;

    CategoryResource categoryResource;

    public CategoryResourceTest() {
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

    private void prepareSession() {
        when(categoryResource.context.getAttribute("SessionFactory")).thenReturn(mockitoSessionFactory);
        when(mockitoSessionFactory.openSession()).thenReturn(mockitoSession);
    }

}