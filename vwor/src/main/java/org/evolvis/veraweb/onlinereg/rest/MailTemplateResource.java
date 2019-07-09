package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.onlinereg.entities.MailTemplate;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path(RestPaths.REST_MAILTEMPLATE)
@Produces(MediaType.APPLICATION_JSON)
public class MailTemplateResource extends AbstractResource {
    @GET
    public Response getMailTemplate(@QueryParam("templateId") Integer templateId, @QueryParam("mandantId") Integer mandantId) {
        if (templateId == null || mandantId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery(MailTemplate.GET_MAILTEMPLATE_BY_ID);
            query.setParameter("templateId", templateId);
            query.setParameter("mandantId", mandantId);
            final MailTemplate template = (MailTemplate) query.uniqueResult();
            if (template != null) {
                return Response.ok(template).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } finally {
            session.close();
        }
    }
}
