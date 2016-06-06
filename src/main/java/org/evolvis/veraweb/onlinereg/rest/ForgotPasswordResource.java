package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.LinkType;
import org.evolvis.veraweb.onlinereg.entities.LinkUUID;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/forgotPassword")
@Produces(MediaType.APPLICATION_JSON)
public class ForgotPasswordResource extends AbstractResource {

    private MailDispatcher mailDispatcher;
    private EmailConfiguration emailConfiguration;

    @POST
    @Path("/request/reset-password-link")
    public void requestResetPasswordLink(@FormParam("username") String username, @FormParam("currentLanguageKey") String currentLanguageKey) throws MessagingException {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findByUsername");
            query.setParameter("username", username);
            final Person person = (Person) query.uniqueResult();
            if (person != null && person.getMail_a_e1() != null) {
                final int personId = person.getPk();
                addOrUpdatePasswordEntry(personId, session);
                sendResetPasswordLinkEmail(person.getMail_a_e1(), currentLanguageKey);
            }
        } finally {
            session.close();
        }
    }

    private void addOrUpdatePasswordEntry(Integer personId, Session session) {
        final String uuid = UUID.randomUUID().toString();
        final Query query = session.getNamedQuery("LinkUUID.getLinkUuidByPersonid");
        query.setParameter("personid", personId);
        final List<LinkUUID> list = query.list();
        if (list.size() < 1) {
            addNewLinkUuidEntry(personId, session, uuid);
        } else if (list.size() == 1) {
            updateLinkUuidEntry(personId, session, uuid);
        }

    }

    private void updateLinkUuidEntry(Integer personId, Session session, String uuid) {
        final Query query = session.getNamedQuery("LinkUUID.updateUUIDByPersonid");
        query.setString("uuid", uuid);
        query.setInteger("personid", personId);
        query.executeUpdate();
    }

    private void addNewLinkUuidEntry(Integer personId, Session session, String uuid) {
        final LinkUUID linkUUID = new LinkUUID();
        linkUUID.setUuid(uuid);
        linkUUID.setPersonid(personId);
        linkUUID.setLinktype(LinkType.PASSWORDRESET.getText());
        session.persist(linkUUID);
        session.flush();
    }

    private void sendResetPasswordLinkEmail(String toEmail, String currentLanguageKey) throws MessagingException {
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration(currentLanguageKey);
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }
        mailDispatcher.sendVerificationEmail(emailConfiguration.getFrom(), toEmail, emailConfiguration.getSubject_reset_password(), emailConfiguration.getContent_reset_password(), "http://localhost/reset/password/", emailConfiguration.getContentType());
    }

}
