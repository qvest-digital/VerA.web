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
