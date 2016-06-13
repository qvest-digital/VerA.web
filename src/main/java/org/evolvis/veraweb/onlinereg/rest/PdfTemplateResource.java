package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.PdfTemplate;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/pdftemplate")
@Produces(MediaType.APPLICATION_JSON)
public class PdfTemplateResource extends AbstractResource {
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
    public Response listPdfTemplates(@FormParam("mandant-id") Integer mandantId) {
        Session session = openSession();
        try {
            final Query query = session.getNamedQuery("PdfTemplate.getPdfTemplatesByOrgunit");
            query.setInteger("fk_orgunit", mandantId);
            final List<PdfTemplate> pdfTemplates = query.list();
            return Response.ok(pdfTemplates).build();
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
        final byte[] content = "Any String you want".getBytes();
        pdfTemplate.setContent(content);
        pdfTemplate.setFk_orgunit(mandantId);

        return pdfTemplate;
    }

    private void updatePdfTemplate(Session session, Integer id, String name) {
        final Query query = session.getNamedQuery("PdfTemplate.updatePdfTemplateById");
        query.setInteger("id", id);
        query.setString("name", name);
        query.executeUpdate();
    }
}
