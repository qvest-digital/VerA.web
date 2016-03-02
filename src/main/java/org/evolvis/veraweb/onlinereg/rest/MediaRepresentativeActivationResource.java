package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/press/activation")
@Produces(MediaType.APPLICATION_JSON)
public class MediaRepresentativeActivationResource extends AbstractResource {

    @POST
    @Path("/create")
    public MediaRepresentativeActivation addOsiamUserActivationEntry(
        @FormParam("eventId") Integer eventId,
        @FormParam("email") String email,
        @FormParam("activation_token") String activationToken) {

        final Session session = openSession();
        try {
            final MediaRepresentativeActivation mediaRepresentativeActivation = new MediaRepresentativeActivation(activationToken, email, eventId);
            session.persist(mediaRepresentativeActivation);
            session.flush();
            return mediaRepresentativeActivation;
        } finally {
            session.close();
        }
    }
}
