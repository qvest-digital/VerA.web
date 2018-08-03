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
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
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

import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

/**
 * Created by Atanas Alexandrov on 03.05.18.
 */
class MediaRepresentativeActivationResourceTest extends Specification implements WithMediaRepresentationActivation {

    MediaRepresentativeActivationResource subject
    SessionFactory sessionFactory
    Session session
    ServletContext context
    Transaction transaction

    def activationToken = 'activationToken'
    def email = 'email'
    def eventId = 1
    def gender = 'gender'
    def address = 'address'
    def city = 'city'
    def country = 'country'
    def firstname = 'firstname'
    def lastname = 'lastname'
    def zip = 53123

    def setup() {
        context = Mock(ServletContext)
        transaction = Mock(Transaction)
        sessionFactory = Mock(SessionFactory)
        session = Mock(Session)
        subject = new MediaRepresentativeActivationResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
    }

    def 'add media representative activation entry'() {
        when: 'add activation'
           subject.addMediaRepresentativeActivationEntry(activationToken, email, eventId, gender, address, city, country, firstname, lastname, zip)

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'transaction exists'
            1 * session.getTransaction() >> transaction
        and: 'transaction is started'
            1 * session.beginTransaction()
        and: 'transaction.commit is executed'
            1 * transaction.commit()
        and: 'session is closed'
            1 * session.close()
    }

    @Unroll
    def 'returns true if activation exists, otherwise false'() {
        given: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId") >> query
        and: 'result from the query'
            query.uniqueResult() >> numberOfActivations

        when: 'invoke method to check, if activation exists'
            boolean activationExists = subject.existEventIdByDelegation('encodedAddress', '1')

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'session is closed'
            1 * session.close()
        and: 'activation exists == #expectedResult'
            activationExists == expectedResult

        where:
            numberOfActivations | expectedResult
            new BigInteger(0)   | false
            new BigInteger(1)   | true
            new BigInteger(2)   | true
            null                | false

    }

    @Unroll
    def 'get activation by token'() {
        given: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.getActivationByActivationToken") >> query
        and: 'result from the query'
            query.uniqueResult() >> stubbedActivation

        when: 'invoke method to get activation by token'
            MediaRepresentativeActivation activation = subject.getMediaRepresentativeActivationByToken('token')

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'session is closed'
            1 * session.close()
        and: 'activation status is correct'
            activation.activated == activationStatus

        where:
            stubbedActivation          | activationStatus
            createActiveActivation()   | 1
            createDisabledActivation() | 0
    }

    def 'activate press user'() {
        given: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery("MediaRepresentativeActivation.activate") >> query

        when: 'invoke method to activate user'
            subject.activatePressUser('email@email.com', 1)

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'transaction exists'
            1 * session.getTransaction() >> transaction
        and: 'transaction is started'
            1 * session.beginTransaction()
        and: 'transaction.commit is executed'
            1 * transaction.commit()
        and: 'session is closed'
            1 * session.close()
    }

}
