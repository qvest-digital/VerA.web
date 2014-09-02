package org.evolvis.veraweb.onlinereg;

import org.evolvis.veraweb.onlinereg.entities.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Location;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.rest.AbstractResource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import javax.servlet.ServletContext;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mley on 02.09.14.
 */
public class AbstractResourceTest<T extends AbstractResource> {

    public static SessionFactory sessionFactory;

    public static ServletContext contextMock;

    static {
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
        // setup the session factory
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        configuration.addAnnotatedClass(Config.class)
                .addAnnotatedClass(Event.class)
                .addAnnotatedClass(Guest.class)
                .addAnnotatedClass(Location.class)
                .addAnnotatedClass(Person.class);
        configuration.setProperty("hibernate.dialect",
                "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class",
                "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");


        sessionFactory =  configuration.buildSessionFactory();

        contextMock = mock(ServletContext.class);
        when(contextMock.getAttribute(eq("SessionFactory"))).thenReturn(sessionFactory);
    }

    public static void stopH2() {
        sessionFactory.close();
    }

}
