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
package org.evolvis.veraweb.onlinereg;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jboss.logging.Logger;


public class HibernateSessionFactoryListener implements ServletContextListener {

    public final Logger logger = Logger.getLogger(HibernateSessionFactoryListener.class);

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SessionFactory sessionFactory = (SessionFactory) servletContextEvent.getServletContext().getAttribute("SessionFactory");
        if(sessionFactory != null && !sessionFactory.isClosed()){
            logger.info("Closing sessionFactory");
            sessionFactory.close();
        }
        logger.info("Released Hibernate sessionFactory resource");
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            logger.info("Hibernate Configuration created successfully");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            logger.info("ServiceRegistry created successfully");
            SessionFactory sessionFactory = configuration
                    .buildSessionFactory(serviceRegistry);
            logger.info("SessionFactory created successfully");

            servletContextEvent.getServletContext().setAttribute("SessionFactory", sessionFactory);
            logger.info("Hibernate SessionFactory Configured successfully");
        } catch(Exception e) {
            logger.error("Error", e);
        }
    }

}
