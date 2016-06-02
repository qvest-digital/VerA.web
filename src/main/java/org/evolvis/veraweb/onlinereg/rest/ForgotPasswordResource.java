package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
            Person person = (Person) query.uniqueResult();
            if (person != null && person.getMail_a_e1() != null) {
                sendResetPasswordLinkEmail(person.getMail_a_e1(), currentLanguageKey);
            }
        } finally {
            session.close();
        }
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
