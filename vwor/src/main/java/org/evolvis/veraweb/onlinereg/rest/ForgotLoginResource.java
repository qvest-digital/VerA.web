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
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

/**
 * @author Max Weierstall, tarent solutions GmbH
 */
@Path("/forgotLogin")
public class ForgotLoginResource extends AbstractResource {

    private MailDispatcher mailDispatcher;
    private EmailConfiguration emailConfiguration;

    @POST
    @Path("/resend/login")
    public void resendLogin(@FormParam("mail") String mail, @FormParam("currentLanguageKey") String currentLanguageKey) throws MessagingException {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Person.findByMail");
            query.setString("email", mail);
            final List<Person> personList = query.list();

            if (personList != null && !personList.isEmpty()) {
                sendLoginDataEmail(mail, personList, currentLanguageKey);
            }
        } finally {
            session.close();
        }
    }

    private void sendLoginDataEmail(String toEmail, List<Person> personList, String currentLanguageKey) throws MessagingException {
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration(currentLanguageKey);
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }

        String usernameList = buildLink(personList);

        mailDispatcher.sendVerificationEmail(emailConfiguration.getFrom(),
                                            toEmail,
                                            emailConfiguration.getSubjectResendLogin(),
                                            emailConfiguration.getContentResendLogin(),
                                            usernameList,
                                            emailConfiguration.getContentType());
    }

    private String buildLink(List<Person> personList){
        String usernameList = "";
        for (Person person: personList){
            usernameList += "\n" + person.getUsername();
        }
        return usernameList;
    }
}
