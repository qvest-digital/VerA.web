package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class LinkUUIDResourceSessionsTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    private LinkUUIDResource resource;

    void setup() {
        resource = new LinkUUIDResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
    }

    void testGetUserIdByUUIDTheFirst() {
        given:
            def query = Mock(Query)
            def resultList = Mock(List)
            session.getNamedQuery("LinkUUID.getUserIdByUUID") >> query
            query.list() >> resultList
            resultList.isEmpty() >> false
            query.uniqueResult() >> 1

        when:
            def result = resource.getUserIdByUUID("uuid")

        then:
            session != null
            1 * session.close()
            assert result == 1
    }

    void testGetUserIdByUUIDTheSecond() {
        given:
            def query = Mock(Query)
            def resultList = Mock(List)
            session.getNamedQuery("LinkUUID.getUserIdByUUID") >> query
            query.list() >> resultList
            resultList.isEmpty() >> true


        when:
            def result = resource.getUserIdByUUID("uuid")

        then:
            session != null
            1 * session.close()
            assert result == null
    }

    void testDeleteUUID() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.deleteUUIDByPersonid") >> query

        when:
            resource.deleteUUID(1)

        then:
            session != null
            1 * query.executeUpdate()
            1 * session.close()

    }

    void testLinkUuuIds() {
        given:
            def query = Mock(Query)
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid") >> query

        when:
            resource.getLinkUuidsByPersonId(1)

        then:
            session != null
            1 * session.close()

    }
}
