package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.OsiamUserActivation;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/email")
@Produces(MediaType.APPLICATION_JSON)
public class EmailResource extends AbstractResource {

    private EmailConfiguration emailConfiguration;
    private MailDispatcher mailDispatcher;

    @POST
    @Path("/confirmation/send")
    public void sendEmailVerification(@FormParam("email") String email, @FormParam("endpoint") String endpoint, @FormParam("activation_token") String activation_token) throws MessagingException {
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration();
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher();
        }
        mailDispatcher.send(emailConfiguration.getFrom(), email, emailConfiguration.getSubject(), emailConfiguration.getContent(), getActivationLink(endpoint, activation_token));
    }

    private String getActivationLink(String endpoint, String activation_token) {
        return endpoint + "user/activate/" + activation_token;
    }

    public EmailConfiguration getEmailConfiguration() {
        return emailConfiguration;
    }

    public void setEmailConfiguration(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    public MailDispatcher getMailDispatcher() {
        return mailDispatcher;
    }

    public void setMailDispatcher(MailDispatcher mailDispatcher) {
        this.mailDispatcher = mailDispatcher;
    }
}
