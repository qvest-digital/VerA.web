package org.evolvis.veraweb.onlinereg;

/*-
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
import org.evolvis.veraweb.onlinereg.rest.AbstractResource;
import org.hibernate.SessionFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mley on 02.09.14.
 */
public class AbstractResourceTest<T extends AbstractResource> {


    public static SessionFactory sessionFactory;

    public static ServletContext contextMock;

    private static ServletContextEvent contextEventMock;

    static {
        contextMock = mock(ServletContext.class);

        // always return the current value of sessionFactory
        when(contextMock.getAttribute(eq("SessionFactory"))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return sessionFactory;
            }
        });

        // store sessionFactory
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                sessionFactory = (SessionFactory) invocationOnMock.getArguments()[1];
                return null;
            }
        }).when(contextMock).setAttribute(eq("SessionFactory"), any(SessionFactory.class));


        contextEventMock = mock(ServletContextEvent.class);
        when(contextEventMock.getServletContext()).thenReturn(contextMock);

        startH2();
    }

    protected T resource;

    /**
     * Creates a new ResourceTest
     * @param clazz class object of class under test
     */
    public AbstractResourceTest(Class<T> clazz) {
        try {
            resource = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        resource.setContext(contextMock);
    }

    /**
     * start the in memory H2 database
     */
    public static void startH2() {
        new HibernateSessionFactoryListener().contextInitialized(contextEventMock);

    }

    /**
     * stop the in memory H2 database
     */
    public static void stopH2() {
        new HibernateSessionFactoryListener().contextDestroyed(contextEventMock);
    }

}
