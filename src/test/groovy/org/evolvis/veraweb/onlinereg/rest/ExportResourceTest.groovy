package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.Event
import org.evolvis.veraweb.onlinereg.entities.OptionalField
import org.evolvis.veraweb.onlinereg.utils.VworConstants
import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.naming.Context
import javax.naming.InitialContext
import javax.servlet.ServletContext
import javax.sql.DataSource
import javax.ws.rs.container.ResourceContext
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * Created by mweier on 26.04.16.
 */
class ExportResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)

    private ExportResource exportResource
    private InitialContext initContext = Mock(InitialContext)
    private Context namingContext = Mock(Context)
    private DataSource dataSource = Mock(DataSource)
    private ResourceContext resourceContext = Mock(ResourceContext)

    public void setup() {
        exportResource = new ExportResource(initContext: initContext, context: context, resourceContext: resourceContext)
        initContext.lookup("java:comp/env") >> namingContext
        namingContext.lookup("jdbc/vwonlinereg") >> dataSource
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testGetGuestList() {
        given:
            def uriInfo = Mock(UriInfo)
            def event = Mock(Event)
            def query = Mock(Query)
            session.getNamedQuery("Event.getEvent") >> query
            query.uniqueResult() >> event
            event.getShortname() >> "Event 1"
            event.getDatebegin() >> new Date()
            uriInfo.getQueryParameters() >> new MultivaluedHashMap<String, String>()
            OptionalFieldResource optionalFieldResource = Mock(OptionalFieldResource)
            resourceContext.getResource(OptionalFieldResource.class) >> optionalFieldResource
            optionalFieldResource.getOptionalFields(_) >> [
                new OptionalField(pk:1, fk_type: 1, fk_event: 1, label: "Hotel")
            ]

        when:
            Response response = exportResource.getGuestList(1, uriInfo)

        then:
            assert response.status == VworConstants.HTTP_OK
            session != null
            1 * session.close()
    }
}
