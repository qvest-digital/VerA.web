package org.evolvis.veraweb.onlinereg;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import org.evolvis.veraweb.onlinereg.entities.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class HibernateSessionFactoryListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(HibernateSessionFactoryListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SessionFactory sessionFactory = (SessionFactory) servletContextEvent.getServletContext().getAttribute("SessionFactory");
        if (sessionFactory != null && !sessionFactory.isClosed()) {
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
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Category.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Config.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Delegation.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Event.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Function.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Guest.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.LinkUUID.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Location.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.MailTemplate.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.OptionalField.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.OptionalFieldType.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.PdfTemplate.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Person.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.PersonCategory.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.PersonMailinglist.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.Salutation.class);
            configuration.addAnnotatedClass(org.evolvis.veraweb.onlinereg.entities.SalutationAlternative.class);
            LOGGER.info("Hibernate Configuration created successfully");

            ServiceRegistry serviceRegistry =
              new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            LOGGER.info("ServiceRegistry created successfully");
            SessionFactory sessionFactory = configuration
              .buildSessionFactory(serviceRegistry);
            LOGGER.info("SessionFactory created successfully");

            servletContextEvent.getServletContext().setAttribute("SessionFactory", sessionFactory);
            LOGGER.info("Hibernate SessionFactory Configured successfully");
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }
}
