package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.LinkUUID
import org.evolvis.veraweb.onlinereg.entities.Person
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * Created by Atanas Alexandrov on 15.04.18.
 */
class ForgotPasswordResourceTest extends Specification {

    ForgotPasswordResource forgotPasswordResource
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    ServletContext context = Mock(ServletContext)
    Transaction transaction = Mock(Transaction)
    MailDispatcher mailDispatcher = Mock(MailDispatcher)

    def setup() {
        forgotPasswordResource = new ForgotPasswordResource(context: context, mailDispatcher: mailDispatcher)
        context.getAttribute("SessionFactory") >> sessionFactory
    }

    def 'resend login'() {
        given: 'mocked query to get the person'
            def query = Mock(Query)
            session.getNamedQuery("Person.findByUsername") >> query
        and: 'the person'
            query.uniqueResult() >> new Person(pk: 1, username: 'maxm', mail_a_e1: 'maxm@tarent.de')
        and: 'mocked query to get the link uuid'
            def queryForLinkUUID = Mock(Query)
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid") >> queryForLinkUUID
        and: 'mocked link uuid'
            queryForLinkUUID.uniqueResult() >> new LinkUUID(pk: 1, uuid: 'a7e46cd9-6a98-4f0e-8b49-5b401f026cb2', personid: 1)

        when: 'invoke resend login'
            forgotPasswordResource.requestResetPasswordLink("username", "de", "oa-endpoint")

        then:
            1 * sessionFactory.openSession() >> session
            1 * session.beginTransaction()
            1 * session.getTransaction() >> transaction
            1 * transaction.commit()
            1 * session.close()
    }

}
