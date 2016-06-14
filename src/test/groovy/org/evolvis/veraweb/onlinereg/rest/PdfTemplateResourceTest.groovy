package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.PdfTemplate
import org.hibernate.Query;
import org.hibernate.Session
import org.hibernate.SessionFactory

import spock.lang.Specification

import javax.servlet.ServletContext
import javax.ws.rs.core.Response

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
            def pdfTemplateFromDb = Mock(PdfTemplate)
            query.uniqueResult() >> pdfTemplateFromDb
            def expectedId = 1
            pdfTemplateFromDb.pk >> expectedId

        when:
            def result = resource.editPdfTemplate(1, "name", 1);

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            1 * session.flush()
            assert result.status  == Response.Status.OK.statusCode
    }

    void testCreatePdfTemplate() {
        when:
            def result = resource.editPdfTemplate(null, "name", 1);

        then:
            session != null
            1 * session.close()
            1 * session.save(_)
            1 * session.flush()
            assert result.status  == Response.Status.OK.statusCode
    }

    void testDeletePdfTemplate() {
        when:
            def result = resource.deletePdfTemplate(1)

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            1 * session.flush()
            assert result.status  == Response.Status.OK.statusCode
    }

    void testListPdfTemplates() {
        given:
            def mandantId = 1
            List<PdfTemplate> templates = [
                    new PdfTemplate(pk:1, name: "name", fk_orgunit: 1), new PdfTemplate(pk:2, name: "name2", fk_orgunit: 1)
            ]
            query.list() >> templates

        when:
           def result = resource.listPdfTemplates(mandantId);

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            0 * session.flush()
            assert result.context.entity.size() == 2
            assert result.status  == Response.Status.OK.statusCode
    }

    void testListPdfTemplatesNoContent() {
        given:
            def mandantId = 1
            query.list() >> new ArrayList()

        when:
            def result = resource.listPdfTemplates(mandantId);

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            0 * session.flush()
            assert result.context.entity == null
            assert result.status  == Response.Status.NO_CONTENT.statusCode
    }

    void testEditPdfTemplateEmptyStringName() {
        when:
            def result = resource.editPdfTemplate(1, "", 1)

        then:
            assert result.status  == Response.Status.BAD_REQUEST.statusCode
    }

    void testEditPdfTemplateNameIsNull() {
        when:
            def result = resource.editPdfTemplate(1, null, 1)

        then:
            assert result.status  == Response.Status.BAD_REQUEST.statusCode
    }
}
