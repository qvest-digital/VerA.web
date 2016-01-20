package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tzimme on 19.01.16.
 */
@Path("/osiam/user")
public class OsiamUserActivationResource extends AbstractResource {

    private Integer linkValidityPeriodInDays;

    @POST
    @Path("/activation")
    public OsiamUserActivation addOsiamUserActivationEntry(@FormParam("username") String username, @FormParam("activation_token") String activationToken) {
        final Session session = openSession();
        try {
            final Date expirationDate = getExpirationDate();
            final OsiamUserActivation osiamUserActivationEntry = new OsiamUserActivation(username, expirationDate, activationToken);
            session.persist(osiamUserActivationEntry);
            session.flush();

            return osiamUserActivationEntry;
        } finally {
            session.close();
        }
    }

    private Date getExpirationDate(){
        linkValidityPeriodInDays = 3;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, linkValidityPeriodInDays);

        return calendar.getTime();
    }
}
