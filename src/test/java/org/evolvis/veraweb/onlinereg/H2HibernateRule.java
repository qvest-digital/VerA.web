package org.evolvis.veraweb.onlinereg;

import org.evolvis.veraweb.onlinereg.entities.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Location;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.servlet.ServletContext;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mley on 02.09.14.
 */
public class H2HibernateRule implements TestRule {

    public SessionFactory sessionFactory;

    public ServletContext contextMock;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                startIfRequired();
                try {
                    base.evaluate();
                } finally {
                }
            }
        };
    }

    private void startIfRequired() {
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

}
