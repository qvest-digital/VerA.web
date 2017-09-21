package org.evolvis.veraweb.onlinereg.rest;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
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
    private EmailResource emailResource;

    @POST
    @Path("/create")
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
        return new OsiamUserActivation();
    }

    @GET
    @Path("/get/activation/byusername/{username}")
    public OsiamUserActivation getOsiamUserActivationByUsername(@PathParam("username") String username) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OsiamUserActivation.getOsiamUserActivationEntryByUsername");
            query.setString("username", username);
            final OsiamUserActivation osiamUserActivation = (OsiamUserActivation) query.uniqueResult();
            if (osiamUserActivation != null) {
                return osiamUserActivation;
            }
        } finally {
            session.close();
        }
        return new OsiamUserActivation();
    }

    private Date getExpirationDate(){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, LINK_VALIDITY_PERIOD_IN_DAYS);

        return calendar.getTime();
    }

    @POST
    @Path("/refresh/activation/data")
    public void refreshActivationdataByUsername(
            @FormParam("email") String email,
            @FormParam("username") String username,
            @FormParam("activation_token") String activationToken,
            @FormParam("endpoint") String endpoint,
            @FormParam("language") String currentLanguageKey) throws MessagingException {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OsiamUserActivation.refreshOsiamUserActivationByUsername");
            query.setString("username", username);
            query.setString("activation_token", activationToken);
            query.setDate("expiration_date",getExpirationDate());
            query.executeUpdate();

            // Resend mail
            emailResource = getEmailResource();
            emailResource.sendEmailVerification(email, endpoint, activationToken, currentLanguageKey, false);
        } finally {
            session.close();
        }
    }

    public EmailResource getEmailResource() {
        if (emailResource == null) {
            return new EmailResource();
        }
        return emailResource;
    }

    public void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource;
    }
}
