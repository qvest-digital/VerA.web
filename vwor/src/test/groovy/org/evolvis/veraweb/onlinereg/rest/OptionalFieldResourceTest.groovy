package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.OptionalField
import org.hibernate.query.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class OptionalFieldResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    Transaction mockTxn = Mock(Transaction)

    OptionalFieldResource optionalFieldResource;

    public void setup() {
        optionalFieldResource = new OptionalFieldResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getTransaction() >> mockTxn
    }

    public void testGetOptionalFields() {
        given:
            def eventId = 1;
            Query query = Mock(Query)
            session.getNamedQuery(OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID) >> query
            query.list() >> [new OptionalField(label: "Hotel", pk: 1, fk_event: 1, fk_type: 1)]

        when:
            def result = optionalFieldResource.getOptionalFields(eventId)

        then:
            session != null
            1 * session.close()
            assert result.size() == 1
    }
}
