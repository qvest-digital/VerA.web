package org.evolvis.veraweb.onlinereg.rest

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
class ForgotLoginResourceTest extends Specification {

    ForgotLoginResource forgotLoginResource
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    ServletContext context = Mock(ServletContext)
    Transaction transaction = Mock(Transaction)
    MailDispatcher mailDispatcher = Mock(MailDispatcher)

    def setup() {
        forgotLoginResource = new ForgotLoginResource(context: context, mailDispatcher: mailDispatcher)
        context.getAttribute("SessionFactory") >> sessionFactory
    }

    def 'resend login'() {
        given: 'mocked query'
            def query = Mock(Query)
            session.getNamedQuery("Person.findByMail") >> query
        and: 'result list with users'
            query.list() >> [new Person(pk: 1, username: 'maxm')]

        when: 'invoke resend login'
            forgotLoginResource.resendLogin("any@mail.de", "de")

        then:
            1 * sessionFactory.openSession() >> session
            1 * session.beginTransaction()
            1 * session.getTransaction() >> transaction
            1 * transaction.commit()
            1 * session.close()
    }

}
