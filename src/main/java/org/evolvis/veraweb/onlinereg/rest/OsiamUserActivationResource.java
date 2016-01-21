package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tzimme on 19.01.16.
 */
@Path("/osiam/user")
@Produces(MediaType.APPLICATION_JSON)
public class OsiamUserActivationResource extends AbstractResource {

    // FIXME Set the value via properties file
    private static final Integer LINK_VALIDITY_PERIOD_IN_DAYS = 3;

    @POST
    @Path("/create")
    public OsiamUserActivation addOsiamUserActivationEntry(@FormParam("username") String username, @FormParam("activation_token") String activationToken) {
        final Session session = openSession();
        try {
            final Date expirationDate = getExpirationDate();
            final OsiamUserActivation osiamUserActivationEntry = new OsiamUserActivation(username, expirationDate, activationToken);
            session.persist(osiamUserActivationEntry);
            session.flush();
            session.close();

            return osiamUserActivationEntry;
        } finally {
            session.close();
        }
    }

    @POST
    @Path("/activate")
    public void removeOsiamUserActivationEntry(@FormParam("osiam_user_activation") String activationToken) {
        final Session session = openSession();
        try {
            final OsiamUserActivation osiamUserActivation = getOsiamUserActivation(activationToken);
            session.delete(osiamUserActivation);
            session.flush();
        } finally {
            session.close();
        }
    }

    @GET
    @Path("/get/activation/{activation_token}")
    public OsiamUserActivation getOsiamUserActivation(@PathParam("activation_token") String activationToken) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OsiamUserActivation.getOsiamUserActivationEntryByToken");
            query.setString("activation_token", activationToken);
            final OsiamUserActivation osiamUserActivation = (OsiamUserActivation) query.uniqueResult();
            if (osiamUserActivation != null) {
                return osiamUserActivation;
            }
        } finally {
            session.close();
        }
        return null;
    }

    private Date getExpirationDate(){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, LINK_VALIDITY_PERIOD_IN_DAYS);

        return calendar.getTime();
    }

    @POST
    @Path("/osiam/user/resetactivation")
    public void removeActivationdataByUsername(@FormParam("username") String username, @FormParam("activation_token") String activationToken) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Delegation.deleteOptionalFieldsByGuestId");
            query.setString("username", username);
            query.setInteger("activation_token", activationToken);
            query.executeUpdate();
        } finally {
            session.close();
        }
    }
}
