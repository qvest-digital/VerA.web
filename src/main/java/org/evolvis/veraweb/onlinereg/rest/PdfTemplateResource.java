package org.evolvis.veraweb.onlinereg.rest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.evolvis.veraweb.onlinereg.entities.PdfTemplate;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.hibernate.Query;
import org.hibernate.Session;

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
import java.util.Date;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/pdftemplate")
@Produces(MediaType.APPLICATION_JSON)
public class PdfTemplateResource extends AbstractResource {
    private static final String EXTENSION_PDF = "pdf";

    @POST
    @Path("/edit")
    public Response editPdfTemplate(@FormParam("pdftemplate-id") Integer id, @FormParam("pdftemplate-name") String name, @FormParam("pdftemplate-orgunit") Integer mandantId) {
        if (name == null || name.trim().equals("")) {
            return Response.status(Status.BAD_REQUEST).build();
        } else {
            final PdfTemplate pdfTemplate = createOrUpdatePdfTemplate(id, name, mandantId);
            return Response.ok(pdfTemplate).build();
        }
    }

    @POST
    @Path("/delete")
    //TODO: PARAMETER ANPASSEN
    public Response deletePdfTemplate(@FormParam("id") Integer id) {
        Session session = openSession();
        try {
            final Query query = session.getNamedQuery("PdfTemplate.deletePdfTemplateById");
            query.setInteger("id", id);
            query.executeUpdate();
            session.flush();
            return Response.status(Status.OK).build();
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
            if(pdfTemplates.isEmpty()) {
                return Response.status(Status.NO_CONTENT).build();
            }
            return Response.ok(pdfTemplates).build();
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/export")
    public Response generatePdf(@QueryParam("templateId") Integer pdfTemplateId, @QueryParam("eventId") Integer eventId) throws IOException {
        if (pdfTemplateId == null || eventId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        final List<Person> list = getPersons(eventId);
        if (list.isEmpty()) {
            return Response.status(Status.NO_CONTENT).build();
        }

        final File file = new File(writeOutputFile(pdfTemplateId));
        return Response.ok(file).header("Content-Disposition", "attachment;filename=" + file.getName() + ";charset=Unicode").build();
    }

    private String writeOutputFile(Integer pdfTemplateId) throws IOException {
        final PdfTemplate pdfTemplate = getPdfTemplate(pdfTemplateId);
        final byte[] content = pdfTemplate.getContent();
        final String filename = getOutputFilename(pdfTemplate.name);
        final String path = FileUtils.getTempDirectory() + File.separator + filename;
        final OutputStream outputStream = new FileOutputStream(path);
        outputStream.write(content);
        outputStream.close();

        return path;
    }

    private String getOutputFilename(String pdfTemplateName) {
        return new StringBuilder(pdfTemplateName).
                append("-").
                append(new Date().getTime()).
                append(".").
                append(EXTENSION_PDF).toString();
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


    private PdfTemplate createOrUpdatePdfTemplate(Integer id, String name, Integer mandantId) {
        PdfTemplate pdfTemplate;
        if (id != null) {
            pdfTemplate = handlePdfTemplateUpdate(id, name);
        } else {
            pdfTemplate = handlePdfTemplateCreate(name, mandantId);
        }
        return pdfTemplate;
    }

    private PdfTemplate handlePdfTemplateCreate(String name, Integer mandantId) {
        final Session session = openSession();
        try {
            PdfTemplate pdfTemplate = initPdfTemplate(name, mandantId);
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

    private PdfTemplate initPdfTemplate(String name, Integer mandantId) {
        PdfTemplate pdfTemplate = new PdfTemplate();
        pdfTemplate.setName(name);
        byte[] content = new byte[0];
        try {
            content = convertPdfToByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfTemplate.setContent(content);
        pdfTemplate.setFk_orgunit(mandantId);

        return pdfTemplate;
    }

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

    private void updatePdfTemplate(Session session, Integer id, String name) {
        final Query query = session.getNamedQuery("PdfTemplate.updatePdfTemplateById");
        query.setInteger("id", id);
        query.setString("name", name);
        query.executeUpdate();
    }
}
