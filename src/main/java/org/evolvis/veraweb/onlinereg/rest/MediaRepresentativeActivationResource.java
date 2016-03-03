package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.MediaRepresentativeActivation;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;

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

    @GET
    @Path("/exists/{emailUp}/{emailDown}/{eventId}")
    public Boolean existEventIdByDelegation(@PathParam("emailUp") String emailUp, @PathParam("emailDown") String emailDown, @PathParam("eventId") String eventId) {
        final Session session = openSession();
        final String email = emailUp + emailDown;
        try {
            final Query query = session.getNamedQuery("MediaRepresentativeActivation.getEntryByEmailAndEventId");
            query.setInteger("fk_event", Integer.parseInt(eventId));
            query.setString("email", email);
            final BigInteger activations = (BigInteger) query.uniqueResult();
            if (activations != null && activations.intValue() >= 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            session.close();
        }
    }
}
