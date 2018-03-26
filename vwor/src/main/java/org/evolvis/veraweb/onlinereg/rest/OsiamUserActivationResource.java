package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
import org.hibernate.query.Query;
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
        session.beginTransaction();
        try {
            final Date expirationDate = getExpirationDate();
            final OsiamUserActivation osiamUserActivationEntry = new OsiamUserActivation(username, expirationDate, activationToken);
            session.persist(osiamUserActivationEntry);
            session.flush();
            session.getTransaction().commit();
            return osiamUserActivationEntry;
        } finally {
            session.close();
        }
    }

    @POST
    @Path("/activate")
    public void removeOsiamUserActivationEntry(@FormParam("osiam_user_activation") String activationToken) {
        final Session session = openSession();
        session.beginTransaction();
        try {
            final OsiamUserActivation osiamUserActivation = getOsiamUserActivation(activationToken);
            session.delete(osiamUserActivation);
            session.flush();
            session.getTransaction().commit();
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
        session.beginTransaction();
        try {
            final Query query = session.getNamedQuery("OsiamUserActivation.refreshOsiamUserActivationByUsername");
            query.setString("username", username);
            query.setString("activation_token", activationToken);
            query.setDate("expiration_date",getExpirationDate());
            query.executeUpdate();
            session.getTransaction().commit();

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
