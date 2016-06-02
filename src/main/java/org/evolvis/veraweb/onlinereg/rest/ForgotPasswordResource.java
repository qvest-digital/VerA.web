package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
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
    public void requestResetPasswordLink(@FormParam("username") String username) throws MessagingException {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findByUsername");
            Person person = (Person) query.uniqueResult();
            if (person != null) {
                sendResetPasswordLinkEmail(person.getMail_a_e1());
            }
        } finally {
            session.close();
        }
    }

    private void sendResetPasswordLinkEmail(String toEmail) throws MessagingException {
        if (mailDispatcher == null) {
            if (emailConfiguration == null) {
                emailConfiguration = new EmailConfiguration("de_DE");
            }
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }
        mailDispatcher.sendVerificationEmail("me", toEmail, "resetPassword", "resset password ${link}", "http://localhost/reset/password/", "html");
    }

}
