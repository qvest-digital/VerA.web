package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class FreeVisitorsResourceGroovyTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    private FreeVisitorsResource resource;

    void setup() {
        resource = new FreeVisitorsResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testCheckGuestExistsByNoLoginRequiredUUIDTheFirst() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("Guest.getGuestByNoLoginRequiredUUID") >> query
            query.uniqueResult() >> 1

        when:
            resource.checkGuestExistsByNoLoginRequiredUUID("someUUID")

        then:
            session != null
            1 * session.close()
    }

    void testCheckGuestExistsByNoLoginRequiredUUIDTheSecond() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("Guest.getGuestByNoLoginRequiredUUID") >> query
            query.uniqueResult() >> null

        when:
            def result = resource.checkGuestExistsByNoLoginRequiredUUID("someUUID")

        then:
            session != null
            result == -1
            1 * session.close()
    }
}
