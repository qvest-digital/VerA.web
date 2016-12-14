package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.OptionalField
import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.AfterClass
import spock.lang.Specification

import javax.servlet.ServletContext

import static org.mockito.Mockito.mock

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class OptionalFieldResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)

    OptionalFieldResource optionalFieldResource;

    public void setup() {
        optionalFieldResource = new OptionalFieldResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    public void testGetOptionalFields() {
        given:
            def eventId = 1;
            Query query = Mock(Query)
            session.getNamedQuery("OptionalField.findByEventId") >> query
            query.list() >> [new OptionalField(label: "Hotel", pk: 1, fk_event: 1, fk_type: 1)]

        when:
            def result = optionalFieldResource.getOptionalFields(eventId)

        then:
            session != null
            1 * session.close()
            assert result.size() == 1
    }
}
