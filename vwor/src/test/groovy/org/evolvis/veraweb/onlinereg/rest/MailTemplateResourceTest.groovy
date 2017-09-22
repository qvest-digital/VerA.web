package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.MailTemplate
import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class MailTemplateResourceTest extends Specification {

    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    private MailTemplateResource mailTemplateResource
    def query = Mock(Query)

    public void setup() {

        mailTemplateResource = new MailTemplateResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getNamedQuery(_) >> query
    }

    void testGetMailTemplate() {
        given:
            session.getNamedQuery(MailTemplate.GET_MAILTEMPLATE_BY_ID) >> query
            query.uniqueResult() >> Mock(MailTemplate)

        when:
            def response = mailTemplateResource.getMailTemplate(1, 1)

        then:
            session != null
            1 * session.close()
            assert response.status == 200
    }

    void testGetMailTemplateNotFound() {
        given:
            def query = Mock(Query)
            query.uniqueResult() >> null

        when:
            def response = mailTemplateResource.getMailTemplate(2333, 456)

        then:
            session != null
            1 * session.close()
            assert response.status == 404
    }

    void testGetMailTemplateIdIsNull() {
        given:
            def query = Mock(Query)
            query.uniqueResult() >> null

        when:
            def response = mailTemplateResource.getMailTemplate(null, 456)

        then:
            session != null
            0 * session.close()
            assert response.status == 400
    }

    void testGetMailMandantIdIsNull() {
        given:
            def query = Mock(Query)
            query.uniqueResult() >> null

        when:
            def response = mailTemplateResource.getMailTemplate(2333, null)

        then:
            session != null
            0 * session.close()
            assert response.status == 400
    }
}
