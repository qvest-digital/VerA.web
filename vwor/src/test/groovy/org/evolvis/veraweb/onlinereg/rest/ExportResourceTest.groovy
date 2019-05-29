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

import org.evolvis.veraweb.export.ValidExportFilter
import org.evolvis.veraweb.onlinereg.entities.Event
import org.evolvis.veraweb.onlinereg.entities.OptionalField
import org.evolvis.veraweb.onlinereg.utils.VworConstants
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification

import javax.naming.Context
import javax.naming.InitialContext
import javax.servlet.ServletContext
import javax.sql.DataSource
import javax.ws.rs.container.ResourceContext
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.Response

/**
 * Created by mweier on 26.04.16.
 */
class ExportResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    Transaction mockTxn = Mock(Transaction)

    Event event = Mock(Event)
    Query query = Mock(Query)
    MultivaluedHashMap<String, String> queryParameters = new MultivaluedHashMap<>()
    MultivaluedHashMap<String, String> formParameters = new MultivaluedHashMap<>()
    OptionalFieldResource optionalFieldResource = Mock(OptionalFieldResource)

    private ExportResource exportResource
    private InitialContext initContext = Mock(InitialContext)
    private Context namingContext = Mock(Context)
    private DataSource dataSource = Mock(DataSource)
    private ResourceContext resourceContext = Mock(ResourceContext)

    def setup() {
        exportResource = new ExportResource(initContext: initContext, context: context, resourceContext: resourceContext)
        initContext.lookup("java:comp/env") >> namingContext
        namingContext.lookup("jdbc/vwonlinereg") >> dataSource
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getTransaction() >> mockTxn

        session.getNamedQuery("Event.getEvent") >> query
        query.uniqueResult() >> event
        event.getShortname() >> "Event 1"
        event.getDatebegin() >> new Date()
        resourceContext.getResource(OptionalFieldResource.class) >> optionalFieldResource
    }

    void testGetGuestList() {
        given:
        optionalFieldResource.getOptionalFields(_) >> [
                new OptionalField(pk: 1, fk_type: 1, fk_event: 1, label: "Hotel")
        ]

        when:
        Response response = exportResource.getGuestList(1, formParameters, [])

        then:
        assert response.status == VworConstants.HTTP_OK
        session != null
        1 * session.close()
    }

    def testFilterParamterUsageForExport() {
        given:
        queryParameters.put(ValidExportFilter.CATEGORY_ID_FILTER.key, Collections.singletonList('4711'))
        queryParameters.put(ValidExportFilter.RESERVE_FILTER.key, Collections.singletonList('0815'))
        optionalFieldResource.getOptionalFields(_) >> []

        when:
        Response response = exportResource.getGuestList(1, formParameters, [])

        then:
        assert response.status == VworConstants.HTTP_OK
        session != null
        1 * session.close()

    }
}
