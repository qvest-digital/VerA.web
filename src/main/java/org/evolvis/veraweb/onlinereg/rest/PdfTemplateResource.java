package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.PdfTemplate;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/pdftemplate")
@Produces(MediaType.APPLICATION_JSON)
public class PdfTemplateResource extends AbstractResource {
    @POST
    @Path("/edit")
    public Response editPdfTemplate(@FormParam("pdftemplate-id") Integer id, @FormParam("pdftemplate-name") String name) {
        Session session = openSession();
        try {
            if (id != null) {
                updatePdfTemplate(session, id, name);
                session.flush();
            } else {
                final PdfTemplate pdfTemplate = initPdfTemplate(name);
                session.save(pdfTemplate);
                session.flush();
            }
            return Response.status(Status.OK).build();
        } finally {
            session.close();
        }

    }

    private PdfTemplate initPdfTemplate(String name) {
        PdfTemplate pdfTemplate = new PdfTemplate();
        pdfTemplate.setName(name);
        final byte[] content = "Any String you want".getBytes();
        pdfTemplate.setContent(content);

        return pdfTemplate;
    }

    private void updatePdfTemplate(Session session, Integer id, String name) {
        final Query query = session.getNamedQuery("PdfTemplate.updatePdfTemplateById");
        query.setInteger("id", id);
        query.setString("name", name);
        query.executeUpdate();
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
}
