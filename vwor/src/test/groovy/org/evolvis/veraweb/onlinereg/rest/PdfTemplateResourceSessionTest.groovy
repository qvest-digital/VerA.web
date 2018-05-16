package org.evolvis.veraweb.onlinereg.rest

import org.evolvis.veraweb.onlinereg.entities.PdfTemplate
import org.glassfish.jersey.media.multipart.BodyPartEntity
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.ws.rs.core.Response

/**
 * Created by Atanas Alexandrov on 13.05.18.
 */
class PdfTemplateResourceSessionTest extends Specification {

    PdfTemplateResource subject
    SessionFactory sessionFactory
    Session session
    ServletContext context
    Transaction transaction

    def setup() {
        context = Mock(ServletContext)
        transaction = Mock(Transaction)
        sessionFactory = Mock(SessionFactory)
        session = Mock(Session)
        subject = new PdfTemplateResource(context: context)
        context.getAttribute("SessionFactory") >> sessionFactory
    }

    def 'edit pdf template with file'() {
        given: 'mocked form data'
            FormDataMultiPart data = Mock(FormDataMultiPart)
        and: 'stubbed pdf template id'
            FileDataBodyPart pdfTemplateId = Mock(FileDataBodyPart)
            pdfTemplateId.getValue() >> 1
        and: 'form attribute pdftemplate-id exists'
            data.getField("pdftemplate-id") >> pdfTemplateId
        and: 'stubbed pdf template name'
            FormDataBodyPart pdfTemplateName = Mock(FileDataBodyPart)
            data.getField("pdftemplate-name") >> pdfTemplateName
            pdfTemplateName.getValue() >> 'template name'
        and: 'stubbed pdf org unit'
            FormDataBodyPart orgunit = Mock(FileDataBodyPart)
            data.getField("pdftemplate-orgunit") >> orgunit
            orgunit.getValue() >> '1'
        and: 'stubbed pdf file'
            FormDataBodyPart pdfFile = Mock(FileDataBodyPart)
            data.getField("files") >> pdfFile
            FormDataContentDisposition content = Mock(FormDataContentDisposition)
            pdfFile.getFormDataContentDisposition() >> content
            content.getFileName() >> 'filename'
            BodyPartEntity entity = Mock(BodyPartEntity)
            pdfFile.getEntity() >> entity
        and: 'mocked input stream from the pdf file'
            def file = new File('src/test/resources/itext-template.pdf')
            entity.getInputStream() >> new FileInputStream(file)
        and: 'mocked query'
            Query query = Mock(Query)
            session.getNamedQuery(PdfTemplate.GET_PDF_TEMPLATE) >> query
        and: 'mocked template'
            query.uniqueResult() >> Mock(PdfTemplate)
        and: 'update query'
            Query updateQuery = Mock(Query)
            session.getNamedQuery(PdfTemplate.UPDATE_PDF_TEMPLATE_CONTENT) >> updateQuery

        when: 'invoke method to edit pdf template with file'
            Response result = subject.editPdfTemplateWithFile(data)

        then: 'respons status is 200'
            result.status == 200
        and: 'session is opened'
            1 * sessionFactory.openSession() >> session
        and: 'transaction exists'
            1 * session.getTransaction() >> transaction
        and: 'transaction is started'
            1 * session.beginTransaction()
        and: 'transaction.commit is executed'
            1 * transaction.commit()
        and: 'session is closed'
            1 * session.close()
        and: 'update query is executed once'
            1 * updateQuery.executeUpdate();
    }
}
