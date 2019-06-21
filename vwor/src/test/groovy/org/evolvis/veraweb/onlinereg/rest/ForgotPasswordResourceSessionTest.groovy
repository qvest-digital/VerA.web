/**
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
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
package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.LinkUUID
import org.evolvis.veraweb.onlinereg.entities.Person
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification

import javax.mail.Transport
import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class ForgotPasswordResourceSessionTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    Transaction mockTxn = Mock(Transaction)

    def dispatcher
    def forgotPasswordResource

    def transport = Mock(Transport)

    void setup() {
        def emailConfiguration = new EmailConfiguration("host", 465, "ssl", "username", "password", "from@tarent.de", "subjectForVerificationEmail", "contentForVerificationEmail", "resetPasswordSubect", "resetPasswordContext", "subjectResendLogin", "contentResendLogin")
        dispatcher = new MailDispatcher(emailConfiguration)
        dispatcher.setTransport(transport)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getTransaction() >> mockTxn
        forgotPasswordResource = new ForgotPasswordResource(mailDispatcher: dispatcher, context: context, emailConfiguration: emailConfiguration)
    }

    public void "request reset password link successfull first time"() {
        given:
            Query query1 = Mock(Query)
            Query query2 = Mock(Query)
            Person person = Mock(Person)

            session.getNamedQuery("Person.findByUsername") >> query1
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid") >> query2

            query1.uniqueResult() >> person
            person.getMail_a_e1() >> "recipient@email.com"
            query2.uniqueResult() >> null

        when:
            forgotPasswordResource.requestResetPasswordLink("tarentuser", "de_DE", "http://localhost:8181/#/")

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
            session != null
            1 * session.close()
            1 * session.flush()
    }

    public void "request reset password link successfull second time"() {
        given:
            Query query1 = Mock(Query)
            Query query2 = Mock(Query)
            Person person = Mock(Person)
            LinkUUID linkUUID = Mock(LinkUUID)

            session.getNamedQuery("Person.findByUsername") >> query1
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid") >> query2

            query1.uniqueResult() >> person
            person.getMail_a_e1() >> "recipient@email.com"
            query2.uniqueResult() >> linkUUID

        when:
            forgotPasswordResource.requestResetPasswordLink("tarentuser", "de_DE", "http://localhost:8181/#/")

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
            session != null
            1 * session.close()
            1 * session.flush()
    }

    public void "request reset password link failed"() {
        given:
            Query query = Mock(Query)
            session.getNamedQuery("Person.findByUsername") >> query
            query.uniqueResult() >> null

        when:
            forgotPasswordResource.requestResetPasswordLink("tarentuser", "de_DE", "http://localhost:8181/#/")

        then:
            0 * transport.connect('host', 'username', 'password')
            0 * transport.close()
            session != null
            1 * session.close()
    }
}
