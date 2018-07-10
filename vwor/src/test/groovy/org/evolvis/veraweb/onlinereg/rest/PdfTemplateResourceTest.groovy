/**
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package org.evolvis.veraweb.onlinereg.rest

import org.apache.commons.io.output.ByteArrayOutputStream
import org.evolvis.veraweb.onlinereg.entities.PdfTemplate
import org.evolvis.veraweb.onlinereg.entities.Person
import org.evolvis.veraweb.onlinereg.utils.VworConstants
import org.glassfish.jersey.media.multipart.BodyPartEntity
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.hibernate.query.Query
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import spock.lang.Ignore
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.ws.rs.core.Response
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
@Ignore //ADDED VERA-684
    void testGeneratePdfReturnNoContent() {
        given:
            def pdfTemplateId = 1
            def eventId = 1
            query.list() >> new ArrayList()

        when:
            def response = resource.generatePdf(pdfTemplateId, eventId, "", "", "", "")

        then:
            session != null
            1 * session.close()
            assert response.status == Response.Status.NO_CONTENT.statusCode
            assert response.context.entity == null
    }
    @Ignore //ADDED VERA-684
    void testGeneratePdfNoEventId() {
        given:
            def pdfTemplateId = 1

        when:
            def response = resource.generatePdf(pdfTemplateId, null, "", "", "", "")

        then:
            assert response.status == Response.Status.BAD_REQUEST.statusCode
    }
    @Ignore //ADDED VERA-684
    void testGeneratePdfNoTemplateId() {
        given:
            def eventId = 1

        when:
            def response = resource.generatePdf(null, eventId, "", "", "", "")

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
