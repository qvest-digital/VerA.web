package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public void sendEmailVerification(
            @FormParam("email") String email,
            @FormParam("endpoint") String endpoint,
            @FormParam("activation_token") String activation_token,
            @FormParam("language") String currentLanguageKey,
            @FormParam("usertype") Boolean isPressUser) throws MessagingException {
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration(currentLanguageKey);
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }
        executeSendEmail(email, endpoint, activation_token, isPressUser);
    }

    private void executeSendEmail(String email, String endpoint, String activation_token, Boolean isPressUser)
            throws MessagingException {

        final String activationLink = getActivationLink(endpoint, activation_token, isPressUser);
        final String from = emailConfiguration.getFrom();
        final String subject = emailConfiguration.getSubject();
        final String content = emailConfiguration.getContent();
        final String contentType = emailConfiguration.getContentType();

        mailDispatcher.send(from, email, subject, content, activationLink, contentType);
    }

    private String getActivationLink(String endpoint, String activation_token, Boolean type) {
        if(type) {
            return endpoint + "media/activation/confirm/" + activation_token;
        }
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
