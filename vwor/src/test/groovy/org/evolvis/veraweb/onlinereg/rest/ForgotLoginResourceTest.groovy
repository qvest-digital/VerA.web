package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.Person
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher
import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.mail.Transport
import javax.servlet.ServletContext

/**
 * @author Max Weierstall, tarent solutions GmbH
 */
class ForgotLoginResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    def dispatcher
    def forgotLoginResource

    def transport = Mock(Transport)

    void setup() {
        def emailConfiguration = new EmailConfiguration("host", 465, "ssl", "username", "password", "from@tarent.de", "subjectForVerificationEmail", "contentForVerificationEmail", "plaintext", "resetPasswordSubect", "resetPasswordContext", "subjectResendLogin", "contentResendLogin")
        dispatcher = new MailDispatcher(emailConfiguration)
        dispatcher.setTransport(transport)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        forgotLoginResource = new ForgotLoginResource(mailDispatcher: dispatcher, context: context, emailConfiguration: emailConfiguration)
    }

    public void testResendLogin() {
        given:
            Query query = Mock(Query)
            Person person = Mock(Person)
            List resultList = new ArrayList()
            resultList.add(person)
            session.getNamedQuery("Person.findByMail") >> query
            person.getUsername() >> "username"
            query.list() >> resultList


        when:
            forgotLoginResource.resendLogin("recipient@email.com", "de_DE")

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
            session != null
            1 * session.close()
    }

    public void testResendLoginNoResult() {
        given:
            Query query = Mock(Query)
            session.getNamedQuery("Person.findByMail") >> query
            query.list() >> new ArrayList()

        when:
            forgotLoginResource.resendLogin("recipient@email.com", "de_DE")

        then:
            0 * transport.connect('host', 'username', 'password')
            0 * transport.close()
            session != null
            1 * session.close()
    }

    public void testResendLoginResultIsNull() {
        given:
            Query query = Mock(Query)
            session.getNamedQuery("Person.findByMail") >> query
            query.list() >> null

        when:
            forgotLoginResource.resendLogin("recipient@email.com", "de_DE")

        then:
            0 * transport.connect('host', 'username', 'password')
            0 * transport.close()
            session != null
            1 * session.close()
    }
}
