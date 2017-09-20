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
            @FormParam("activation_token") String activationToken,
            @FormParam("language") String currentLanguageKey,
            @FormParam("usertype") Boolean isPressUser) throws MessagingException {
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration(currentLanguageKey);
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }
        executeSendEmail(email, endpoint, activationToken, isPressUser);
    }

    private void executeSendEmail(String email, String endpoint, String activationToken, Boolean isPressUser)
            throws MessagingException {

        final String activationLink = getActivationLink(endpoint, activationToken, isPressUser);
        final String from = emailConfiguration.getFrom();
        final String subject = emailConfiguration.getSubject();
        final String content = emailConfiguration.getContent();
        final String contentType = emailConfiguration.getContentType();

        mailDispatcher.sendVerificationEmail(from, email, subject, content, activationLink, contentType);
    }

    private String getActivationLink(String endpoint, String activationToken, Boolean isPressUser) {
        if(isPressUser) {
            return endpoint + "media/activation/confirm/" + activationToken;
        }
        return endpoint + "user/activate/" + activationToken;
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
