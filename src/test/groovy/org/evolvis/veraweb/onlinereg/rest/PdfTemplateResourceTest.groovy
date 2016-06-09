package org.evolvis.veraweb.onlinereg.rest

import org.hibernate.Session
import org.hibernate.SessionFactory
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class PdfTemplateResourceTest extends Specification {

    def resource
    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)

    def setup() {
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        resource = new PdfTemplateResource(context: context);
    }

    void testEditPdfTemplate() {
        when:
            resource.editPdfTemplate(1, "name");

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            0 * session.flush()
    }

    void testCreatePdfTemplate() {
        when:
            resource.editPdfTemplate(null, "name");

        then:
            session != null
            1 * session.close()
            1 * session.save(_)
            1 * session.flush()
    }
}
