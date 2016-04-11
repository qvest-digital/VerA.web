package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class LinkUUIDResourceTestTheSecond extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    private LinkUUIDResource resource;

    void setup() {
        resource = new LinkUUIDResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testUpdateUUID() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.updateUUIDByPersonid") >> query

        when:
            resource.updateUUID(1)

        then:
            session != null
            1 * query.executeUpdate()
            1 * session.close()

    }
}
