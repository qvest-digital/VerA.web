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
@Path("/osiamUserActivation")
public class OsiamUserActivationResource extends AbstractResource {

    @POST
    @Path("/user/new")
    public void persistNewUser(@FormParam("username") String username, @FormParam("activation_token") String activation_token) {
        int linkValidityPeriod = 3;//days
        Date expiration_date=getExpirationDate(linkValidityPeriod);
        OsiamUserActivation newUser=new OsiamUserActivation(username,expiration_date,activation_token);
        final Session session = openSession();
        session.persist(newUser);
        session.flush();
    }

    public Date getExpirationDate(int validityPeriod){
        Date today= new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, validityPeriod);
        return c.getTime();
    }
}
