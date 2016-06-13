package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.PdfTemplate
import org.hibernate.Query;
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
    Query query = Mock(Query)

    def setup() {
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getNamedQuery(_) >> query

        resource = new PdfTemplateResource(context: context);
    }

    void testEditPdfTemplate() {
        given:
            def pdfTemplate = Mock(PdfTemplate)
            query.uniqueResult() >> pdfTemplate
            def expectedId = 1
            pdfTemplate.getPk() >> expectedId

        when:
            def pdfTemplateId = resource.editPdfTemplate(1, "name", 1);

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            1 * session.flush()
            assert  pdfTemplateId == expectedId
    }

    void testCreatePdfTemplate() {
        when:
            resource.editPdfTemplate(null, "name", 1);

        then:
            session != null
            1 * session.close()
            1 * session.save(_)
            1 * session.flush()
    }

    void testDeletePdfTemplate() {
        when:
            resource.deletePdfTemplate(1)

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            1 * session.flush()
    }
}
