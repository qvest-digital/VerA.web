package org.evolvis.veraweb.onlinereg.rest;

/*-
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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import de.tarent.extract.Extractor;
import de.tarent.extract.ExtractorQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.evolvis.veraweb.common.Placeholder;
import org.evolvis.veraweb.export.ExtractorQueryBuilder;
import org.evolvis.veraweb.onlinereg.entities.PdfTemplate;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.SalutationAlternative;
import org.evolvis.veraweb.onlinereg.utils.PdfTemplateUtilities;
import org.evolvis.veraweb.onlinereg.utils.PersonComparator;
import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/pdftemplate")
@Produces(MediaType.APPLICATION_JSON)
public class PdfTemplateResource extends FormDataResource {
    private final String currentExportFileName = "pdfexport-" + new Date().getTime() + ".pdf";
    private final String OUTPUT_FILENAME =
      FileUtils.getTempDirectoryPath() + File.separator + UUID.randomUUID().toString() + "_" + currentExportFileName;
    private static final Logger LOGGER = Logger.getLogger(PdfTemplateResource.class.getCanonicalName());
    private final Integer DAYS_BACK = 1;
    private static final long MILLISECONDS_PER_DAY = 24L * 60 * 60 * 1000;
    private final long PURGE_TIME = System.currentTimeMillis() - (DAYS_BACK * MILLISECONDS_PER_DAY);
    private List<SalutationAlternative> alternativeSalutations;

    @POST
    @Path("/edit")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response editPdfTemplateWithFile(FormDataMultiPart data) {
        Integer id = null;
        if (!data.getField("pdftemplate-id").getValue().isEmpty()) {
            id = Integer.parseInt(data.getField("pdftemplate-id").getValue());
        }

        String name = data.getField("pdftemplate-name").getValue();
        Integer mandantId = Integer.parseInt(data.getField("pdftemplate-orgunit").getValue());

        final File file;
        try {
            file = saveTempFile(data.getField("files"));
            LOGGER.log(Logger.Level.DEBUG, file.exists());
        } catch (IOException e) {
            LOGGER.error("could not write data to tmp file.", e);
            return Response.status(Status.BAD_REQUEST).build();
        }

        final byte[] content;
        try {
            content = IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            LOGGER.error("could not read tmp file.", e);
            return Response.status(Status.BAD_REQUEST).build();
        }

        //create || update: name & contetn
        return editPdfTemplate(id, name, mandantId, content);
    }

    @POST
    @Path("/edit")
    public Response editPdfTemplateWithoutFile(@FormParam("pdftemplate-id") Integer id,
      @FormParam("pdftemplate-name") String name,
      @FormParam("pdftemplate-orgunit") Integer mandantId) {
        //catch when id is null (create new template) and content is null(which is implicit because of @consumes)
        if (id == null) {
            //create: without content
            return Response.status(VworConstants.HTTP_POLICY_NOT_FULFILLED).build();
        }
        //update: name
        return editPdfTemplate(id, name, mandantId, null);
    }

    @POST
    @Path("/delete")
    public Response deletePdfTemplate(@FormParam("templateId[]") List<Integer> idList) {
        if (idList == null || idList.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Session session = openSession();
        session.beginTransaction();
        try {
            final Query query = session.getNamedQuery(PdfTemplate.DELETE_PDF_TEMPLATE);
            for (Integer id : idList) {
                query.setParameter(PdfTemplate.PARAM_PDF_ID, id);
                query.executeUpdate();
            }
            session.flush();
            session.getTransaction().commit();
            return Response.ok(idList).build();
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/list")
    public Response listPdfTemplates(@QueryParam("mandantid") Integer mandantId) {
        Session session = openSession();
        try {
            final Query query = session.getNamedQuery(PdfTemplate.GET_PDF_TEMPLATE_LIST_BY_ORGUNIT);
            query.setParameter(PdfTemplate.PARAM_PDF_ORGUNIT, mandantId);
            final List<PdfTemplate> pdfTemplates = query.list();
            if (pdfTemplates.isEmpty()) {
                return Response.status(Status.NO_CONTENT).build();
            }
            return Response.ok(pdfTemplates).build();
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/export")
    @Produces({ VworConstants.APPLICATION_PDF_CONTENT_TYPE })
    public Response generatePdf(@QueryParam("templateId") Integer pdfTemplateId,
                                @QueryParam("eventId") Integer eventId,
                                @javax.ws.rs.core.Context UriInfo ui)
      throws IOException, DocumentException {
        if (pdfTemplateId == null || eventId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        final List<Person> people = getPersons(eventId, ui);
        if (people.isEmpty()) {
            return Response.status(Status.NO_CONTENT).build();
        }

        final UUID uuid = UUID.randomUUID();
        alternativeSalutations = getAlternativeSalutations(pdfTemplateId);
        final List<String> filesList = getFileList(people, pdfTemplateId, uuid);
        mergeFiles(filesList);

        final File outputFile = new File(OUTPUT_FILENAME);
        return Response.ok(outputFile)
          .header("Content-Disposition", "attachment;filename=" + currentExportFileName + ";charset=Unicode").build();
    }

    private Response editPdfTemplate(Integer id, String name, Integer mandantId, byte[] content) {
        if (name == null || name.trim().equals("")) {
            return Response.status(VworConstants.HTTP_PRECONDITION_FAILED).build();
        } else {
            try {
                final PdfTemplate pdfTemplate = createOrUpdatePdfTemplate(id, name, mandantId, content);
                return Response.ok(pdfTemplate).build();
            } catch (Exception e) {
                LOGGER.log(Logger.Level.ERROR, "Creating pdf template failed.", e);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    private List<String> getFileList(List<Person> people, Integer pdfTemplateId, UUID uuid)
      throws IOException, DocumentException {
        deleteOldPdfFiles();
        final String tempFileWithPdfTemplateContent = writePdfContentFromDbToTempFile(pdfTemplateId, uuid);
        final List<String> filesList = new ArrayList<>();
        for (Person person : people) {
            final String personalOutputFile = writePersonalOutputFile(tempFileWithPdfTemplateContent, person, uuid);
            filesList.add(personalOutputFile);
        }
        deleteTemplateOutputFiles(tempFileWithPdfTemplateContent);
        return filesList;
    }

    private void deleteTemplateOutputFiles(String tempFileWithPdfTemplateContent) throws IOException {
        FileUtils.forceDelete(new File(tempFileWithPdfTemplateContent));
    }

    private void deleteOldPdfFiles() {
        final File directory = new File(FileUtils.getTempDirectoryPath());
        if (directory.exists()) {
            deleteFiles(directory);
        }
    }

    private void deleteFiles(File directory) {
        final File[] listFiles = directory.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File listFile : listFiles) {
                if (listFile.lastModified() < PURGE_TIME) {
                    executeCurrentFileDeletion(listFile);
                }
            }
        }
    }

    private void executeCurrentFileDeletion(File listFile) {
        if (!listFile.delete()) {
            LOGGER.log(Logger.Level.ERROR, "Unable to delete file: " + listFile);
        }
    }

    private void mergeFiles(List<String> filesList) throws DocumentException, IOException {
        final Document outputFile = new Document();

        final PdfCopy pdfCopy = new PdfCopy(outputFile, new FileOutputStream(OUTPUT_FILENAME));
        outputFile.open();
        for (String filename : filesList) {
            PdfReader pdfReader = new PdfReader(filename);
            final int numberOfPages = pdfReader.getNumberOfPages();
            for (int page = 0; page < numberOfPages; ) {
                pdfCopy.addPage(pdfCopy.getImportedPage(pdfReader, ++page));
            }
        }

        deletePersonalOutputFiles(filesList);
        outputFile.close();
    }

    private void deletePersonalOutputFiles(List<String> filesList) throws IOException {
        for (String filename : filesList) {
            FileUtils.forceDelete(new File(filename));
        }
    }

    private String writePdfContentFromDbToTempFile(Integer pdfTemplateId, UUID uuid) throws IOException {
        final PdfTemplate pdfTemplate = getPdfTemplate(pdfTemplateId);
        final byte[] content = pdfTemplate.getContent();
        final File tempFileForPdfTemplate =
          File.createTempFile(uuid.toString() + "-pdfexport-template" + new Date().getTime(), ".pdf");
        final OutputStream outputStream = new FileOutputStream(tempFileForPdfTemplate);
        try {
            outputStream.write(content);
        } finally {
            outputStream.close();
        }

        return tempFileForPdfTemplate.toString();
    }

    private String writePersonalOutputFile(String pdfTemplateFilename, Person person, UUID uuid)
      throws IOException, DocumentException {
        final PdfReader pdfReader = new PdfReader(pdfTemplateFilename);
        final String path =
          FileUtils.getTempDirectoryPath() + File.separator + uuid.toString() + "-personal-pdf-file-" + person.getPk() +
            "-" +
            new Date().getTime() + ".pdf";
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(path));

        final Map<String, String> substitutions = getSubstitutions(person);
        //iterate over all field in "pdfTemplateFilename"
        final AcroFields acroFields = pdfStamper.getAcroFields();
        final List<String> tempFieldList = new ArrayList<>();
        for (Map.Entry<String, ?> fieldInTemplate : ((HashMap<String, ?>) acroFields.getFields()).entrySet()) {
            acroFields.setField(fieldInTemplate.getKey(), substitutions.get(fieldInTemplate.getKey()));
            tempFieldList.add(fieldInTemplate.getKey());
        }
        renameFields(person.getPk(), acroFields, tempFieldList);
        pdfStamper.close();

        return path;
    }

    /**
     * Rename fields to avoid global changes of the fields.
     *
     * @param personId   The personal ID
     * @param acroFields All fields in the template
     * @param fieldsList Temp list with fieldnames to avoid ConcurrentModificationException
     */
    private void renameFields(Integer personId, AcroFields acroFields, List<String> fieldsList) {
        for (String fieldName : fieldsList) {
            acroFields.renameField(fieldName, fieldName + personId);
        }
    }

    private Map<String, String> getSubstitutions(Person person) {
        final Map<String, String> substitutions;
        updateSalutation(person);

        substitutions = PlaceholderSubstitution.createMap(person);
        //TODO check if using titel instead of 'title' works or it is an unknown bug
        substitutions.put("titel", person.getTitle_a_e1());

        final String salutationCompleteOne = getSalutationCompleteOne(person);
        substitutions.put("salutationComplete1", salutationCompleteOne);
        final String salutationCompleteTwo = getSalutationCompleteTwo(person);
        substitutions.put("salutationComplete2", salutationCompleteTwo);
        final String envelopeOne = getEnvelopeOne(person);
        substitutions.put("envelope1", envelopeOne);

        return substitutions;
    }

    private void updateSalutation(Person person) {
        for (SalutationAlternative alternativeSalutation : alternativeSalutations) {
            if (alternativeSalutation.getSalutation_id().equals(person.getFk_salutation_a_e1())) {
                person.setSalutation_a_e1(alternativeSalutation.getContent());
            }
        }
    }

    private List<SalutationAlternative> getAlternativeSalutations(Integer pdfTemplateId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(SalutationAlternative.GET_SALUTATION_ALTERNATIVE_BY_PDF_ID);
            query.setParameter(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdfTemplateId);
            if (query.list().isEmpty()) {
                return new ArrayList<>();
            }
            return (List<SalutationAlternative>) query.list();
        } finally {
            session.close();
        }
    }

    private String getEnvelopeOne(Person person) {
        final PdfTemplateUtilities pdfTemplateUtilities = new PdfTemplateUtilities(person);
        return pdfTemplateUtilities.getEnvelopeOne();
    }

    private String getSalutationCompleteOne(Person person) {
        final PdfTemplateUtilities pdfTemplateUtilities = new PdfTemplateUtilities(person);
        return pdfTemplateUtilities.getSalutationCompleteOne();
    }

    private String getSalutationCompleteTwo(Person person) {
        final PdfTemplateUtilities pdfTemplateUtilities = new PdfTemplateUtilities(person);
        return pdfTemplateUtilities.getSalutationCompleteTwo();
    }

    private PdfTemplate getPdfTemplate(Integer pdfTemplateId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(PdfTemplate.GET_PDF_TEMPLATE);
            query.setParameter(PdfTemplate.PARAM_PDF_ID, pdfTemplateId);
            return (PdfTemplate) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    private List<Person> getPersons(Integer eventId, UriInfo ui) {
        final Session session = openSession();
        try {
            Query query  = session.getNamedQuery("Person.getPeopleByEventId");
            query.setParameter("eventid", eventId);

            final Properties properties = new Properties();
            final MultivaluedMap<String, String> params = ui.getQueryParameters();

            if (!params.isEmpty()) {
                params.keySet().forEach(key -> properties.setProperty(key, params.getFirst(key)));


                Map<String, String> filterSettings = new HashMap<>();
                params.keySet().stream().filter(key -> key.startsWith("filter")).forEach(key -> filterSettings.put(key, params.getFirst(key)));


                final ExtractorQuery extractorQuery = new ExtractorQuery();
                extractorQuery.setSql(query.getQueryString());

                final ExtractorQuery extractoredQuery = new ExtractorQueryBuilder(extractorQuery).setFilters(filterSettings).build();
                query = session.createQuery(extractoredQuery.getSql());
            }
            List<Person> personList = (List<Person>) query.list();

            PersonComparator comparator = new PersonComparator();
            Collections.sort(personList, comparator);
            return personList;
        } finally {
            session.close();
        }
    }

    private PdfTemplate createOrUpdatePdfTemplate(Integer id, String name, Integer mandantId, byte[] content) {
        PdfTemplate pdfTemplate;
        if (id != null) {
            pdfTemplate = handlePdfTemplateUpdate(id, name, content);
        } else {
            pdfTemplate = handlePdfTemplateCreate(name, mandantId, content);
        }
        return pdfTemplate;
    }

    private PdfTemplate handlePdfTemplateCreate(String name, Integer mandantId, byte[] content) {
        final Session session = openSession();
        session.beginTransaction();
        try {
            PdfTemplate pdfTemplate = initPdfTemplate(name, mandantId, content);
            session.save(pdfTemplate);
            session.flush();
            session.getTransaction().commit();
            return pdfTemplate;
        } finally {
            session.close();
        }
    }

    private PdfTemplate handlePdfTemplateUpdate(Integer id, String name, byte[] content) {
        final Session session = openSession();
        session.beginTransaction();
        try {
            PdfTemplate pdfTemplate = getExistingTemplate(id, session);
            if (content == null) {
                updatePdfTemplate(session, id, name);
            } else {
                updatePdfTemplateWithContent(session, id, name, content);
            }

            session.flush();
            session.getTransaction().commit();
            return pdfTemplate;
        } finally {
            session.close();
        }
    }

    private PdfTemplate getExistingTemplate(Integer id, Session session) {
        final Query query = session.getNamedQuery(PdfTemplate.GET_PDF_TEMPLATE);
        query.setParameter(PdfTemplate.PARAM_PDF_ID, id);
        return (PdfTemplate) query.uniqueResult();
    }

    private PdfTemplate initPdfTemplate(String name, Integer mandantId, byte[] content) {
        PdfTemplate pdfTemplate = new PdfTemplate();
        pdfTemplate.setName(name);
        pdfTemplate.setContent(content);
        pdfTemplate.setFk_orgunit(mandantId);

        return pdfTemplate;
    }

    private void updatePdfTemplate(Session session, Integer id, String name) {
        final Query query = session.getNamedQuery(PdfTemplate.UPDATE_PDF_TEMPLATE);
        setAndExecuteQuery(query, id, name);
    }

    private void updatePdfTemplateWithContent(Session session, Integer id, String name, byte[] content) {
        final Query query = session.getNamedQuery(PdfTemplate.UPDATE_PDF_TEMPLATE_CONTENT);
        query.setParameter(PdfTemplate.PARAM_PDF_CONTENT, content);
        setAndExecuteQuery(query, id, name);
    }

    private void setAndExecuteQuery(Query query, Integer id, String name) {
        query.setParameter(PdfTemplate.PARAM_PDF_ID, id);
        query.setParameter(PdfTemplate.PARAM_PDF_NAME, name);
        query.executeUpdate();
    }
}
