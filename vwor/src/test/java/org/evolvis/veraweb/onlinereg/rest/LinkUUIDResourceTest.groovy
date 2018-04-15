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
