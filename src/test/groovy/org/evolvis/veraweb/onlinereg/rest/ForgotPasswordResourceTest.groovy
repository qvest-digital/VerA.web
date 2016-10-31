package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.LinkUUID
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
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class ForgotPasswordResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    def dispatcher
    def forgotPasswordResource

    def transport = Mock(Transport)

    void setup() {
        def emailConfiguration = new EmailConfiguration("host", 465, "ssl", "username", "password", "from@tarent.de", "subjectForVerificationEmail", "contentForVerificationEmail", "plaintext", "resetPasswordSubect", "resetPasswordContext", "subjectResendLogin", "contentResendLogin")
        dispatcher = new MailDispatcher(emailConfiguration)
        dispatcher.setTransport(transport)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        forgotPasswordResource = new ForgotPasswordResource(mailDispatcher: dispatcher, context: context, emailConfiguration: emailConfiguration)
    }

    public void "request reset password link successfull first time"() {
        given:
            Query query1 = Mock(Query)
            Query query2 = Mock(Query)
            Person person = Mock(Person)

            session.getNamedQuery("Person.findByUsername") >> query1
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid") >> query2

            query1.uniqueResult() >> person
            person.getMail_a_e1() >> "recipient@email.com"
            query2.uniqueResult() >> null

        when:
            forgotPasswordResource.requestResetPasswordLink("tarentuser", "de_DE", "http://localhost:8181/#/")

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
            session != null
            1 * session.close()
            1 * session.flush()
    }

    public void "request reset password link successfull second time"() {
        given:
            Query query1 = Mock(Query)
            Query query2 = Mock(Query)
            Person person = Mock(Person)
            LinkUUID linkUUID = Mock(LinkUUID)

            session.getNamedQuery("Person.findByUsername") >> query1
            session.getNamedQuery("LinkUUID.getLinkUuidByPersonid") >> query2

            query1.uniqueResult() >> person
            person.getMail_a_e1() >> "recipient@email.com"
            query2.uniqueResult() >> linkUUID

        when:
            forgotPasswordResource.requestResetPasswordLink("tarentuser", "de_DE", "http://localhost:8181/#/")

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
            session != null
            1 * session.close()
            1 * session.flush()
    }

    public void "request reset password link failed"() {
        given:
            Query query = Mock(Query)
            session.getNamedQuery("Person.findByUsername") >> query
            query.uniqueResult() >> null

        when:
            forgotPasswordResource.requestResetPasswordLink("tarentuser", "de_DE", "http://localhost:8181/#/")

        then:
            0 * transport.connect('host', 'username', 'password')
            0 * transport.close()
            session != null
            1 * session.close()
    }

}
