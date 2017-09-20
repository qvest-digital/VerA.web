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
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jboss.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class HibernateSessionFactoryListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(HibernateSessionFactoryListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SessionFactory sessionFactory = (SessionFactory) servletContextEvent.getServletContext().getAttribute("SessionFactory");
        if(sessionFactory != null && !sessionFactory.isClosed()){
            LOGGER.info("Closing sessionFactory");
            sessionFactory.close();
        }
        LOGGER.info("Released Hibernate sessionFactory resource");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            LOGGER.info("Hibernate Configuration created successfully");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            LOGGER.info("ServiceRegistry created successfully");
            SessionFactory sessionFactory = configuration
                    .buildSessionFactory(serviceRegistry);
            LOGGER.info("SessionFactory created successfully");

            servletContextEvent.getServletContext().setAttribute("SessionFactory", sessionFactory);
            LOGGER.info("Hibernate SessionFactory Configured successfully");
        } catch(Exception e) {
            LOGGER.error("Error", e);
        }
    }

}
