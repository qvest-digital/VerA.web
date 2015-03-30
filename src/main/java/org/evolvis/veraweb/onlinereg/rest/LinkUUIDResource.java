package org.evolvis.veraweb.onlinereg.rest;

import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/links")
@Produces(MediaType.APPLICATION_JSON)
public class LinkUUIDResource extends AbstractResource {

    @Path("/{uuid}")
    @GET
    public Integer getUserIdByUUID(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("LinkUUID.getUserIdByUUID");
            query.setString("uuid", uuid);
            if (query.list().isEmpty()) {
                // user does not exists
                return null;
            } else {
                final Integer personId = (int) query.uniqueResult();
                return personId;
            }

        } finally {
            session.close();
        }
    }
}
