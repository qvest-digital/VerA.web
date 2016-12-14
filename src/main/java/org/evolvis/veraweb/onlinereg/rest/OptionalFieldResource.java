package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.OptionalField;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/optional/fields")
@Produces(MediaType.APPLICATION_JSON)
public class OptionalFieldResource extends AbstractResource {

    @GET
    @Path("/list/{eventId}")
    public List<OptionalField> getOptionalFields(@PathParam("eventId") Integer eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OptionalField.findByEventId");
            query.setInteger("eventId", eventId);
            return (List<OptionalField>) query.list();
        } finally {
            session.close();
        }
    }
}
