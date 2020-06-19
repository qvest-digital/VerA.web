/**
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.evolvis.veraweb.onlinereg.rest

import org.apache.commons.io.output.ByteArrayOutputStream
import org.evolvis.veraweb.onlinereg.entities.PdfTemplate
import org.evolvis.veraweb.onlinereg.entities.Person
import org.evolvis.veraweb.onlinereg.entities.SalutationAlternative
import org.evolvis.veraweb.onlinereg.utils.VworConstants
import org.glassfish.jersey.media.multipart.BodyPartEntity
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.query.Query
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

    Query querysal = Mock(Query)

    def setup() {
        context.getAttribute("SessionFactory") >> sessionFactory
        sessionFactory.openSession() >> session
        session.getNamedQuery(SalutationAlternative.GET_SALUTATION_ALTERNATIVE_BY_PDF_ID) >> querysal
        session.getNamedQuery(_) >> query
        session.getTransaction() >> mockTxn

        querysal.list() >> new ArrayList()

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
        assert result.status == Response.Status.OK.statusCode
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
        assert result.status == VworConstants.HTTP_PRECONDITION_FAILED
    }

    void testCreatePdfTemplateWithFileNameIsNull() {
        given:
        bodypart_id.getValue() >> ""
        bodypart_name.getValue() >> null
        bodypart_orgunit.getValue() >> "1"

        when:
        def result = resource.editPdfTemplateWithFile(multipart);

        then:
        assert result.status == VworConstants.HTTP_PRECONDITION_FAILED
    }

    //CREATE W/O============================================================================================================
    void testCreatePdfTemplateWithoutFile() {
        when:
        def result = resource.editPdfTemplateWithoutFile(null, "name", 1);

        then:
        assert result.status == VworConstants.HTTP_POLICY_NOT_FULFILLED
    }

    void testCreatePdfTemplateWithoutFileEmptyStringName() {
        when:
        def result = resource.editPdfTemplateWithoutFile(null, "", 1);

        then:
        assert result.status == VworConstants.HTTP_POLICY_NOT_FULFILLED
    }

    void testCreatePdfTemplateWithoutFileNameIsNull() {
        when:
        def result = resource.editPdfTemplateWithoutFile(null, null, 1);

        then:
        assert result.status == VworConstants.HTTP_POLICY_NOT_FULFILLED
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

        4 * query.setParameter(_, _)

        assert result.status == Response.Status.OK.statusCode
    }

    void testEditPdfTemplateWithFileEmptyStringName() {
        given:
        bodypart_id.getValue() >> "1"
        bodypart_name.getValue() >> ""
        bodypart_orgunit.getValue() >> "1"

        when:
        def result = resource.editPdfTemplateWithFile(multipart);

        then:
        assert result.status == VworConstants.HTTP_PRECONDITION_FAILED
    }

    void testEditPdfTemplateWithFileNameIsNull() {
        given:
        bodypart_id.getValue() >> "1"
        bodypart_name.getValue() >> null
        bodypart_orgunit.getValue() >> "1"

        when:
        def result = resource.editPdfTemplateWithFile(multipart);

        then:
        assert result.status == VworConstants.HTTP_PRECONDITION_FAILED
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

        3 * query.setParameter(_, _)

        assert result.status == Response.Status.OK.statusCode
        assert result.context.entity == pdfTemplateFromDb
    }

    void testEditPdfTemplateWithoutFileEmptyStringName() {
        when:
        def result = resource.editPdfTemplateWithoutFile(1, "", 1);

        then:
        assert result.status == VworConstants.HTTP_PRECONDITION_FAILED
    }

    void testEditPdfTemplateWithoutFileNameIsNull() {
        when:
        def result = resource.editPdfTemplateWithoutFile(1, null, 1);

        then:
        assert result.status == VworConstants.HTTP_PRECONDITION_FAILED
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
        3 * query.setParameter(_, _);
        3 * query.executeUpdate();
        assert result.context.entity.size() == 3
        assert result.status == Response.Status.OK.statusCode
    }

    void testDeletePdfTemplateEmptyIdList() {
        given:
        List<Integer> idList = []

        when:
        def result = resource.deletePdfTemplate(idList)

        then:
        assert result.status == Response.Status.BAD_REQUEST.statusCode
    }

    void testDeletePdfTemplateIdListIsNull() {
        when:
        def result = resource.deletePdfTemplate(null)

        then:
        assert result.status == Response.Status.BAD_REQUEST.statusCode
    }

    //LIST==================================================================================================================
    void testListPdfTemplates() {
        given:
        def mandantId = 1
        List<PdfTemplate> templates = [
          new PdfTemplate(pk: 1, name: "name", fk_orgunit: 1), new PdfTemplate(pk: 2, name: "name2", fk_orgunit: 1)
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
        assert result.status == Response.Status.OK.statusCode
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
        assert result.status == Response.Status.NO_CONTENT.statusCode
    }

    //EXPORT================================================================================================================
    void testGeneratePdf() {
        given:
        def pdfTemplateId = 1
        def eventId = 1
        def ui = Mock(UriInfo)
        ui.getQueryParameters() >> new MultivaluedHashMap<String, String>()
        query.list() >> [new Person(pk: 1), new Person(pk: 2), new Person(pk: 3)]
        query.uniqueResult() >> new PdfTemplate(pk: 1, name: "Tischkarte", content: convertPdfToByteArray())

        when:
        def response = resource.generatePdf(pdfTemplateId, eventId, ui)

        then:
        1 * session.createQuery(*_) >> query
        session != null
        3 * session.close()
        2 * query.setParameter(_, _)
        1 * querysal.setParameter(_, _)
        assert response.status == Response.Status.OK.statusCode
        assert new String(response.context.entity.bytes, 0, 4).equals("%PDF");
        assert response.context.entity.size() > 1000
    }

    void testQueryBuilder() {
        given:
        def map = new HashMap<String, String>()
        map.put('filterCategoryId', '1')
        map.put('filterWord', keywords)
        map.put('filterInvStatus', invStatus)
        map.put('filterReserve', '1')

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
        keywords      | invStatus | expectedFilter                                                                                                                                                                                                                                                                                    | expectedKeywordFilter
        'dude'        | '1'       | '((invitationtype = 1 AND (invitationstatus IS NULL OR invitationstatus=0 OR invitationstatus_p IS NULL OR invitationstatus_p=0)) OR (invitationtype = 2 AND (invitationstatus IS NULL OR invitationstatus=0)) OR (invitationtype = 3 AND (invitationstatus_p IS NULL OR invitationstatus_p=0)))' | "g.keywords LIKE '%dude%'"
        'my dudeness' | '2'       | '((invitationtype = 1 AND (invitationstatus = 1 OR invitationstatus_p = 1)) OR (invitationtype = 2 AND invitationstatus = 1) OR (invitationtype = 3 AND invitationstatus_p = 1))'                                                                                                                 | "g.keywords LIKE '%my%' AND g.keywords LIKE '%dudeness%'"
        'bi ba bu'    | '3'       | '((invitationtype = 1 AND (invitationstatus = 2 OR invitationstatus_p = 2)) OR (invitationtype = 2 AND invitationstatus = 2) OR (invitationtype = 3 AND invitationstatus_p = 2))'                                                                                                                 | "g.keywords LIKE '%bi%' AND g.keywords LIKE '%ba%' AND g.keywords LIKE '%bu%'"
        'nope'        | '4'       | '((invitationtype = 1 AND (invitationstatus = 3 OR invitationstatus_p = 3)) OR (invitationtype = 2 AND invitationstatus = 3) OR (invitationtype = 3 AND invitationstatus_p = 3))'                                                                                                                 | "g.keywords LIKE '%nope%'"
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
    private static byte[] convertPdfToByteArray() throws IOException {
        def file = new File('src/test/resources/itext-template.pdf')
        final InputStream resourceAsStream = new FileInputStream(file)
        byte[] buffer = new byte[8192]
        ByteArrayOutputStream baos = new ByteArrayOutputStream()

        int bytesRead
        while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead)
        }
        return baos.toByteArray()
    }
}
