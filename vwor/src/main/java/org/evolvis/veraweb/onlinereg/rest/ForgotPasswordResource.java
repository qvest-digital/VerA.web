package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

import org.evolvis.veraweb.onlinereg.entities.LinkType;
import org.evolvis.veraweb.onlinereg.entities.LinkUUID;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.hibernate.query.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import org.evolvis.veraweb.common.RestPaths;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path(RestPaths.REST_FORGOTPASSWORD)
@Produces(MediaType.APPLICATION_JSON)
public class ForgotPasswordResource extends AbstractResource {

    private MailDispatcher mailDispatcher;
    private EmailConfiguration emailConfiguration;

    @POST
    @Path(RestPaths.REST_FORGOTPASSWORD_RESET_LINK)
    public void requestResetPasswordLink(@FormParam("username") String username,
      @FormParam("currentLanguageKey") String currentLanguageKey,
      @FormParam("oaEndpoint") String oaEndpoint) throws MessagingException {
        final Session session = openSession();
        session.beginTransaction();
        try {
            final Query query = session.getNamedQuery("Person.findByUsername");
            query.setParameter("username", username);
            final Person person = (Person) query.uniqueResult();
            final String uuid = UUID.randomUUID().toString();
            if (person != null && person.getMail_a_e1() != null) {
                final int personId = person.getPk();
                createOrUpdateLinkUuidEntry(personId, session, uuid);
                sendResetPasswordLinkEmail(person.getMail_a_e1(), currentLanguageKey, oaEndpoint, uuid);
            }
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    private void createOrUpdateLinkUuidEntry(Integer personId, Session session, String uuid) {
        final LinkUUID linkUUID = getAndSetLinkUuid(session, personId, uuid);
        session.persist(linkUUID);
        session.flush();
    }

    private LinkUUID getAndSetLinkUuid(Session session, Integer personId, String uuid) {
        final LinkUUID linkUUID = createOrGetLinkUuid(session, personId);
        setLinkUuidData(linkUUID, personId, uuid);
        return linkUUID;
    }

    private LinkUUID createOrGetLinkUuid(Session session, Integer personId) {
        final Query query = session.getNamedQuery("LinkUUID.getLinkUuidByPersonid");
        query.setParameter("personid", personId);

        if (query.uniqueResult() == null) {
            return new LinkUUID();
        } else {
            return (LinkUUID) query.uniqueResult();
        }
    }

    private void setLinkUuidData(LinkUUID linkUUID, Integer personId, String uuid) {
        linkUUID.setPersonid(personId);
        linkUUID.setLinktype(LinkType.PASSWORDRESET.getText());
        linkUUID.setUuid(uuid);
    }

    private void sendResetPasswordLinkEmail(String toEmail, String currentLanguageKey, String oaEndpoint, String uuid)
      throws MessagingException {
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration(currentLanguageKey);
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }
        String link = buildLink(oaEndpoint, uuid);
        mailDispatcher.sendVerificationEmail(emailConfiguration.getFrom(), toEmail, emailConfiguration.getSubjectResetPassword(),
          emailConfiguration.getContentResetPassword(), link, emailConfiguration.getContentType());
    }

    private String buildLink(String oaEndpoint, String uuid) {
        final String suffix = "reset/password/";
        if (oaEndpoint.endsWith("/")) {
            return oaEndpoint + suffix + uuid;
        }
        return oaEndpoint + "/" + suffix + uuid;
    }
}
