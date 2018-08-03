/**
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017, 2018 Атанас Александров (a.alexandrov@tarent.de)
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

import org.evolvis.veraweb.onlinereg.entities.LinkUUID
import org.evolvis.veraweb.onlinereg.entities.Person
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * Created by Atanas Alexandrov on 15.04.18.
 */
class LinkUUIDResourceTest extends Specification {

    LinkUUIDResource linkUUIDResource

    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    ServletContext context = Mock(ServletContext)
    Transaction transaction = Mock(Transaction)

    def setup() {
        linkUUIDResource = new LinkUUIDResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
    }

    def 'get user id by uuid'() {
        given: 'mocked query'
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.getUserIdByUUID")  >> query
        and: 'existing person'
            def person = new Person(pk: 1, username: 'maxm')
        and: 'result list with users'
            query.list() >> [person]
        and: 'user from the db'
            query.uniqueResult() >> 1

        when: 'get uuid method is invoked'
            linkUUIDResource.getUserIdByUUID("uuid")

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'session is closed'
            1 * session.close()
    }

    def 'get user id by uuid returns empty list'() {
        given: 'mocked query'
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.getUserIdByUUID")  >> query
        and: 'result list without users'
            query.list() >> []

        when: 'get uuid method is invoked'
            def userId = linkUUIDResource.getUserIdByUUID("uuid")

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'session is closed'
            1 * session.close()
        and: 'the user is is null'
            userId == null
    }

    def 'delete uuid'() {
        given: 'mocked query'
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.deleteUUIDByPersonid")  >> query

        when: 'delete method is invoked'
            linkUUIDResource.deleteUUID(1)

        then: 'delete statement is executed once'
            1 * query.executeUpdate()
        and: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'transaction exists'
            1 * session.getTransaction() >> transaction
        and: 'transaction is commited'
            1 * transaction.commit()
        and: 'the session is closed'
            1 * session.close()
    }

    def 'get link uuids by personid'() {
        given: 'mocked query'
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid")  >> query
        and: 'result list with link uuids'
            query.list() >> [Mock(LinkUUID), Mock(LinkUUID), Mock(LinkUUID)]

        when: 'get uuid method is invoked'
            def linkUUIDS = linkUUIDResource.getLinkUuidsByPersonId(1)

        then: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'the session is closed'
            1 * session.close()
        and: 'there are three link uuids in the result list'
            linkUUIDS.size() == 3
    }

}
