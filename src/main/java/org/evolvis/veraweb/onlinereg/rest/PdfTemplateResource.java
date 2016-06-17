package org.evolvis.veraweb.onlinereg.rest;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.evolvis.veraweb.onlinereg.entities.PdfTemplate;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/pdftemplate")
@Produces(MediaType.APPLICATION_JSON)
public class PdfTemplateResource extends FormDataResource {
    private final String currentExportFileName = "pdfexport-" + new Date().getTime() + ".pdf";
    private final String OUTPUT_FILENAME = FileUtils.getTempDirectoryPath() + File.separator + currentExportFileName;
    private static final Logger LOGGER = Logger.getLogger(PdfTemplateResource.class.getCanonicalName());
    private final Integer DAYS_BACK = 1;
    private final long PURGE_TIME = System.currentTimeMillis() - (DAYS_BACK * 24 * 60 * 60 * 1000);

    @POST
    @Path("/edit")
    public Response editPdfTemplate(@FormParam("pdftemplate-id") Integer id, @FormParam("pdftemplate-name") String name, @FormParam("pdftemplate-orgunit") Integer mandantId, @FormParam("pdftemplate-content") FormDataMultiPart data) {
        final Map<String, File> files = getFiles(data.getFields("files"));
        LOGGER.log(Logger.Level.DEBUG, files.size());

        byte[] content = "".getBytes();

        if (name == null || name.trim().equals("")) {
            return Response.status(Status.BAD_REQUEST).build();
        } else {
            try {
                final PdfTemplate pdfTemplate = createOrUpdatePdfTemplate(id, name, mandantId, content);
                return Response.ok(pdfTemplate).build();
            } catch (IOException e) {
                LOGGER.log(Logger.Level.ERROR, "Creating pdf template failed.", e);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @POST
    @Path("/delete")
    public Response deletePdfTemplate(@FormParam("templateId[]") List<Integer> idList) {
        if (idList == null || idList.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Session session = openSession();
        try {
            final Query query = session.getNamedQuery("PdfTemplate.deletePdfTemplateById");
            for (Integer id : idList) {
                query.setInteger("id", id);
                query.executeUpdate();
            }
            session.flush();
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
            final Query query = session.getNamedQuery("PdfTemplate.getPdfTemplateListByOrgunit");
            query.setInteger("fk_orgunit", mandantId);
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
    public Response generatePdf(@QueryParam("templateId") Integer pdfTemplateId, @QueryParam("eventId") Integer eventId) throws IOException, DocumentException {
        if (pdfTemplateId == null || eventId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        final List<Person> people = getPersons(eventId);
        if (people.isEmpty()) {
            return Response.status(Status.NO_CONTENT).build();
        }

        final List<String> filesList = getFileList(people, pdfTemplateId);
        mergeFiles(filesList);

        final File outputFile = new File(OUTPUT_FILENAME);
        return Response.ok(outputFile).header("Content-Disposition", "attachment;filename=" + currentExportFileName + ";charset=Unicode").build();
    }

    private List<String> getFileList(List<Person> people, Integer pdfTemplateId) throws IOException, DocumentException {
        deleteOldPdfFiles();
        final String tempFileWithPdfTemplateContent = wrtiePdfContentFromDbToTempFile(pdfTemplateId);
        final List<String> filesList = new ArrayList<>();
        for (Person person : people) {
            final String personalOutputFile = writePersonalOutputFile(tempFileWithPdfTemplateContent, person);
            filesList.add(personalOutputFile);
        }
        FileUtils.forceDelete(new File(tempFileWithPdfTemplateContent));
        return filesList;
    }

    private void deleteOldPdfFiles() {
        final File directory = new File(FileUtils.getTempDirectoryPath());
        if(directory.exists()){
            deleteFiles(directory);
        }
    }

    private void deleteFiles(File directory) {
        final File[] listFiles = directory.listFiles();
        if(listFiles.length > 0) {
            for(File listFile : listFiles) {
                if(listFile.lastModified() < PURGE_TIME) {
                    executeCurrentFileDeletion(listFile);
                }
            }
        }
    }

    private void executeCurrentFileDeletion(File listFile) {
        if(!listFile.delete()) {
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

    private void deletePersonalOutputFiles(List<String> filesList) {
        for (String filename : filesList) {
            try {
                FileUtils.forceDelete(new File(filename));
            } catch (IOException e) {
                LOGGER.log(Logger.Level.ERROR, "The file " + filename + " could not be deleted");
            }
        }
    }

    private String wrtiePdfContentFromDbToTempFile(Integer pdfTemplateId) throws IOException {
        final PdfTemplate pdfTemplate = getPdfTemplate(pdfTemplateId);
        final byte[] content = pdfTemplate.getContent();
        final File tempFileForPdfTemplate = File.createTempFile("pdfexport-template-" + new Date().getTime(), ".pdf");
        final OutputStream outputStream = new FileOutputStream(tempFileForPdfTemplate);
        outputStream.write(content);
        outputStream.close();

        return tempFileForPdfTemplate.toString();
    }

    private String writePersonalOutputFile(String pdfTemplateFilename, Person person) throws IOException, DocumentException {

        final PdfReader pdfReader = new PdfReader(pdfTemplateFilename);
        final String path = FileUtils.getTempDirectoryPath() + File.separator + "personal-pdf-file-" + person.getPk() + "-" + new Date().getTime() + ".pdf";
        final PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(path));
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
            pdfStamper.getAcroFields().setField("salutation", person.getSalutation_a_e1());
            pdfStamper.getAcroFields().setField("firstname", person.getFirstname_a_e1());

        }
        pdfStamper.close();
        return path;
    }

    private PdfTemplate getPdfTemplate(Integer pdfTemplateId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("PdfTemplate.getPdfTemplateById");
            query.setInteger("id", pdfTemplateId);
            return (PdfTemplate) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    private List<Person> getPersons(@QueryParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.getPeopleByEventId");
            query.setInteger("eventid", eventId);
            return (List<Person>) query.list();
        } finally {
            session.close();
        }
    }


    private PdfTemplate createOrUpdatePdfTemplate(Integer id, String name, Integer mandantId, byte[] content) throws IOException {
        PdfTemplate pdfTemplate;
        if (id != null) {
            pdfTemplate = handlePdfTemplateUpdate(id, name);
        } else {
            pdfTemplate = handlePdfTemplateCreate(name, mandantId, content);
        }
        return pdfTemplate;
    }

    private PdfTemplate handlePdfTemplateCreate(String name, Integer mandantId, byte[] content) throws IOException {
        final Session session = openSession();
        try {
            PdfTemplate pdfTemplate = initPdfTemplate(name, mandantId, content);
            session.save(pdfTemplate);
            session.flush();
            return pdfTemplate;
        } finally {
            session.close();
        }
    }

    private PdfTemplate handlePdfTemplateUpdate(Integer id, String name) {
        final Session session = openSession();
        try {
            PdfTemplate pdfTemplate = getExistingTemplate(id, session);
            updatePdfTemplate(session, id, name);
            session.flush();
            return pdfTemplate;
        } finally {
            session.close();
        }
    }

    private PdfTemplate getExistingTemplate(Integer id, Session session) {
        final Query query = session.getNamedQuery("PdfTemplate.getPdfTemplateById");
        query.setInteger("id", id);
        return (PdfTemplate) query.uniqueResult();
    }

    private PdfTemplate initPdfTemplate(String name, Integer mandantId, byte[] content) throws IOException {
        PdfTemplate pdfTemplate = new PdfTemplate();
        pdfTemplate.setName(name);
        pdfTemplate.setContent(content);
        pdfTemplate.setFk_orgunit(mandantId);

        return pdfTemplate;
    }

    private byte[] convertPdfToByteArray() throws IOException {
        final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("formular-tarent.pdf");
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    private void updatePdfTemplate(Session session, Integer id, String name) {
        final Query query = session.getNamedQuery("PdfTemplate.updatePdfTemplateById");
        query.setInteger("id", id);
        query.setString("name", name);
        query.executeUpdate();
    }
}
