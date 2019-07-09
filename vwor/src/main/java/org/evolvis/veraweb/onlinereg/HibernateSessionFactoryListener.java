package org.evolvis.veraweb.onlinereg;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Log4j2
public class HibernateSessionFactoryListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SessionFactory sessionFactory = (SessionFactory) servletContextEvent.getServletContext().getAttribute("SessionFactory");
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.debug("Closing sessionFactory");
            sessionFactory.close();
        }
        logger.info("Released Hibernate sessionFactory resource");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Config.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Event.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Location.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.MailTemplate.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.OptionalField.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.PdfTemplate.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Person.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.PersonMailinglist.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Salutation.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.SalutationAlternative.class);
            logger.debug("Hibernate Configuration created successfully");

            ServiceRegistry serviceRegistry =
              new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            assert (serviceRegistry != null);
            logger.debug("ServiceRegistry created successfully");
            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            assert (sessionFactory != null);
            logger.debug("SessionFactory created successfully");

            servletContextEvent.getServletContext().setAttribute("SessionFactory", sessionFactory);
            logger.info("Hibernate SessionFactory configured successfully");
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }
}
