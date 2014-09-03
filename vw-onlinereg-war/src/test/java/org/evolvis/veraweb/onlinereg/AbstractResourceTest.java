package org.evolvis.veraweb.onlinereg;

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
        when(contextMock.getAttribute(eq("SessionFactory"))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return sessionFactory;
            }
        });

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

    public AbstractResourceTest(Class<T> clazz) {
        try {
            resource = clazz.getConstructor().newInstance();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
        resource.setContext(contextMock);
    }

    public static void startH2() {
        new HibernateSessionFactoryListener().contextInitialized(contextEventMock);

    }

    public static void stopH2() {
        new HibernateSessionFactoryListener().contextDestroyed(contextEventMock);
    }

}
