package org.evolvis.veraweb.onlinereg.rest

import org.apache.commons.io.output.ByteArrayOutputStream
import org.evolvis.veraweb.onlinereg.entities.PdfTemplate
import org.evolvis.veraweb.onlinereg.entities.Person
import org.evolvis.veraweb.onlinereg.utils.VworConstants
import org.glassfish.jersey.media.multipart.BodyPartEntity
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
import spock.lang.Ignore
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo
import java.nio.charset.StandardCharsets

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class PdfTemplateResourceTest extends Specification {

    def resource
    def multipart = Mock(FormDataMultiPart)

    def bodypart_file = Mock(FormDataBodyPart)
    def content_disposition = Mock(FormDataContentDisposition)
    def body_entity = Mock(BodyPartEntity)

    def bodypart_id = Mock(FormDataBodyPart)
    def bodypart_name = Mock(FormDataBodyPart)
    def bodypart_orgunit = Mock(FormDataBodyPart)
    ServletContext context = Mock(ServletContext)
    SessionFactory sessionFactory = Mock(SessionFactory)
    Session session = Mock(Session)
    Transaction mockTxn = Mock(Transaction)

    Query query = Mock(Query)

    def setup() {
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getNamedQuery(_) >> query
        session.getTransaction() >> mockTxn

        multipart.getField("files") >> bodypart_file
        bodypart_file.getFormDataContentDisposition() >> content_disposition
        content_disposition.getFileName() >> "filename"
        bodypart_file.getEntity() >> body_entity
        body_entity.getInputStream() >> new ByteArrayInputStream("file".getBytes(StandardCharsets.UTF_8))

        multipart.getField("pdftemplate-id") >> bodypart_id
        multipart.getField("pdftemplate-name") >> bodypart_name
        multipart.getField("pdftemplate-orgunit") >> bodypart_orgunit

        resource = new PdfTemplateResource(context: context);
    }

//CREATE WITH===========================================================================================================
    void testCreatePdfTemplateWithFile() {
        given:
            bodypart_id.getValue() >> ""
            bodypart_name.getValue() >> "name"
            bodypart_orgunit.getValue() >> "1"

        when:
            def result = resource.editPdfTemplateWithFile(multipart);

        then:
            session != null
            1 * session.close()
            1 * session.save(_)
            1 * session.flush()
            assert result.status  == Response.Status.OK.statusCode
            assert result.context.entity.name == "name"
            assert result.context.entity.content == "file".getBytes(StandardCharsets.UTF_8)
            assert result.context.entity.fk_orgunit == 1
    }

    void testCreatePdfTemplateWithFileEmptyStringName() {
        given:
            bodypart_id.getValue() >> ""
            bodypart_name.getValue() >> ""
            bodypart_orgunit.getValue() >> "1"

        when:
            def result = resource.editPdfTemplateWithFile(multipart);

        then:
            assert result.status  == VworConstants.HTTP_PRECONDITION_FAILED
    }

    void testCreatePdfTemplateWithFileNameIsNull() {
        given:
            bodypart_id.getValue() >> ""
            bodypart_name.getValue() >> null
            bodypart_orgunit.getValue() >> "1"

        when:
            def result = resource.editPdfTemplateWithFile(multipart);

        then:
            assert result.status  == VworConstants.HTTP_PRECONDITION_FAILED
    }

//CREATE W/O============================================================================================================
    void testCreatePdfTemplateWithoutFile() {
        when:
            def result = resource.editPdfTemplateWithoutFile(null, "name", 1);

        then:
            assert result.status  == VworConstants.HTTP_POLICY_NOT_FULFILLED
    }

    void testCreatePdfTemplateWithoutFileEmptyStringName() {
        when:
            def result = resource.editPdfTemplateWithoutFile(null, "", 1);

        then:
            assert result.status  == VworConstants.HTTP_POLICY_NOT_FULFILLED
    }

    void testCreatePdfTemplateWithoutFileNameIsNull() {
        when:
            def result = resource.editPdfTemplateWithoutFile(null, null, 1);

        then:
            assert result.status  == VworConstants.HTTP_POLICY_NOT_FULFILLED
    }

//EDIT WITH=============================================================================================================
    void testEditPdfTemplateWithFile() {
        given:
            bodypart_id.getValue() >> "1"
            bodypart_name.getValue() >> "name"
            bodypart_orgunit.getValue() >> "1"

        when:
            def result = resource.editPdfTemplateWithFile(multipart);

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            1 * session.flush()

            4 * query.setParameter(_,_)

            assert result.status  == Response.Status.OK.statusCode
    }

    void testEditPdfTemplateWithFileEmptyStringName() {
        given:
            bodypart_id.getValue() >> "1"
            bodypart_name.getValue() >> ""
            bodypart_orgunit.getValue() >> "1"

        when:
            def result = resource.editPdfTemplateWithFile(multipart);

        then:
            assert result.status  == VworConstants.HTTP_PRECONDITION_FAILED
    }

    void testEditPdfTemplateWithFileNameIsNull() {
        given:
            bodypart_id.getValue() >> "1"
            bodypart_name.getValue() >> null
            bodypart_orgunit.getValue() >> "1"

        when:
            def result = resource.editPdfTemplateWithFile(multipart);

        then:
            assert result.status  == VworConstants.HTTP_PRECONDITION_FAILED
    }
//EDIT W/O==============================================================================================================
    void testEditPdfTemplateWithoutFile() {
        given:
            def pdfTemplateFromDb = Mock(PdfTemplate)
            query.uniqueResult() >> pdfTemplateFromDb

        when:
            def result = resource.editPdfTemplateWithoutFile(1, "name", 1);

        then:
            session != null
            1 * session.close()
            0 * session.save(_)
            1 * session.flush()

            3 * query.setParameter(_,_)

            assert result.status  == Response.Status.OK.statusCode
            assert result.context.entity == pdfTemplateFromDb
    }

    void testEditPdfTemplateWithoutFileEmptyStringName() {
        when:
            def result = resource.editPdfTemplateWithoutFile(1, "", 1);

        then:
            assert result.status  == VworConstants.HTTP_PRECONDITION_FAILED
    }

    void testEditPdfTemplateWithoutFileNameIsNull() {
        when:
            def result = resource.editPdfTemplateWithoutFile(1, null, 1);

        then:
            assert result.status  == VworConstants.HTTP_PRECONDITION_FAILED
    }

//DELETE================================================================================================================
    void testDeletePdfTemplate() {
        given:
            List<Integer> idList = [1, 2, 3]

        when:
            def result = resource.deletePdfTemplate(idList)

        then:
            session != null
            1 * session.close()
            1 * session.flush()
            3 * query.setParameter(_,_);
            3 * query.executeUpdate();
            assert result.context.entity.size() == 3
            assert result.status  == Response.Status.OK.statusCode
    }

    void testDeletePdfTemplateEmptyIdList() {
        given:
            List<Integer> idList = []

        when:
         def result = resource.deletePdfTemplate(idList)

        then:
            assert result.status  == Response.Status.BAD_REQUEST.statusCode
    }

    void testDeletePdfTemplateIdListIsNull() {
        when:
            def result = resource.deletePdfTemplate(null)

        then:
            assert result.status  == Response.Status.BAD_REQUEST.statusCode
    }

//LIST==================================================================================================================
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

//EXPORT================================================================================================================
@Ignore //FIXME
    void testGeneratePdf() {
        given:
            def pdfTemplateId = 1
            def eventId = 1
            query.list() >> [new Person(pk: 1), new Person(pk: 2), new Person(pk: 3)]
            query.uniqueResult() >> new PdfTemplate(pk:1, name: "Tischkarte", content: convertPdfToByteArray())

        when:
            def response = resource.generatePdf(pdfTemplateId, eventId)

        then:
            session != null
            2 * session.close()
            assert response.status == Response.Status.OK.statusCode
            assert new String(response.context.entity.bytes,0,4).equals("%PDF");
            assert response.context.entity.size() > 1000
    }

    void testQueryBuilder() {
        given:
            def map = new HashMap<String, String>()
            map.put('filterCategoryId','1')
            map.put('filterWord', keywords)
            map.put('filterInvStatus', invStatus)
            map.put('filterReserve','1')

        when:
            def query = resource.buildQuery(map)

        then:
            assert query == "SELECT p FROM Person p" +
                    " JOIN Guest g ON (p.pk = g.fk_person)" +
                    " WHERE g.fk_event=:eventid" +
                    " AND g.reserve = :filterReserve AND " +
                    expectedFilter +
                    " AND g.fk_category = :filterCategoryId" +
                    " AND " + expectedKeywordFilter +
                    " ORDER BY p.lastname_a_e1 ASC "

        where:
        keywords        | invStatus | expectedFilter                                                                                                                                                                                                                                                                                    | expectedKeywordFilter
        'dude'          | '1'       | '((invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR invitationstatus_p IS NULL OR invitationstatus_p=0)) OR (invitationtype = 2 AND (invitationstatus IS NULL OR invitationstatus=0)) OR (invitationtype = 3 AND (invitationstatus_p IS NULL OR invitationstatus_p=0)))' | "g.keywords LIKE '%dude%'"
        'my dudeness'   | '2'       | '((invitationtype = 1 AND (invitationstatus = 1 OR invitationstatus_p = 1)) OR (invitationtype = 2 AND invitationstatus = 1) OR (invitationtype = 3 AND invitationstatus_p = 1))'                                                                                                                 | "g.keywords LIKE '%my%' AND g.keywords LIKE '%dudeness%'"
        'bi ba bu'      | '3'       | '((invitationtype = 1 AND (invitationstatus = 2 OR invitationstatus_p = 2)) OR (invitationtype = 2 AND invitationstatus = 2) OR (invitationtype = 3 AND invitationstatus_p = 2))'                                                                                                                 | "g.keywords LIKE '%bi%' AND g.keywords LIKE '%ba%' AND g.keywords LIKE '%bu%'"
        'nope'          | '4'       | '((invitationtype = 1 AND (invitationstatus = 3 OR invitationstatus_p = 3)) OR (invitationtype = 2 AND invitationstatus = 3) OR (invitationtype = 3 AND invitationstatus_p = 3))'                                                                                                                 | "g.keywords LIKE '%nope%'"
    }

    void testGeneratePdfReturnNoContent() {
        given:
            def pdfTemplateId = 1
            def eventId = 1
            query.list() >> new ArrayList()
            def ui = Mock(UriInfo)
            ui.getQueryParameters() >> new MultivaluedHashMap<String, String>()

        when:
            def response = resource.generatePdf(pdfTemplateId, eventId, ui)

        then:
            1 * session.createQuery(*_) >> query
            session != null
            1 * session.close()
            assert response.status == Response.Status.NO_CONTENT.statusCode
            assert response.context.entity == null
    }

    void testGeneratePdfNoEventId() {
        given:
            def pdfTemplateId = 1
            def uriInfo = Mock(UriInfo)

        when:
            def response = resource.generatePdf(pdfTemplateId, null, uriInfo)

        then:
            assert response.status == Response.Status.BAD_REQUEST.statusCode
    }

    void testGeneratePdfNoTemplateId() {
        given:
            def eventId = 1
            def uriInfo = Mock(UriInfo)

        when:
            def response = resource.generatePdf(null, eventId, uriInfo)

        then:
            assert response.status == Response.Status.BAD_REQUEST.statusCode
    }

//HELPER================================================================================================================
    private byte[] convertPdfToByteArray() throws IOException {
        final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("itext-template.pdf");
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
