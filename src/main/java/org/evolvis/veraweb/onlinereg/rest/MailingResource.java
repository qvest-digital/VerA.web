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
import org.evolvis.veraweb.onlinereg.entities.PersonMailinglist;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatchMonitor;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//FIXME: it's not "attachment", actually this is the whole shebang, including body, subject, recipients etc...
@Path("/mailing")
@Consumes({MediaType.MULTIPART_FORM_DATA})
public class MailingResource extends FormDataResource {
    private static final Logger LOGGER = Logger.getLogger(MailingResource.class);
    public static final String PARAM_MAILINGLIST_ID = "mailinglist-id";
    public static final String PARAM_MAIL_TEXT = "mailtext";
    public static final String PARAM_MAIL_SUBJECT = "mail-subject";

    private MailDispatcher mailDispatcher;
    private EmailConfiguration emailConfiguration;

    @POST
    @Path("/")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response uploadFile(final FormDataMultiPart formData) {
        final String subject = formData.getField(PARAM_MAIL_SUBJECT).getEntityAs(String.class);
        final String text = formData.getField(PARAM_MAIL_TEXT).getEntityAs(String.class);
        final int mailinglistId = Integer.parseInt(formData.getField(PARAM_MAILINGLIST_ID).getEntityAs(String.class));

        final List<PersonMailinglist> recipients = getRecipients(mailinglistId);

        final Map<String, File> files = getFiles(formData.getFields("files"));
        final String msg;

        try {
            msg = sendEmails(recipients, subject, text, files);
        } catch (AddressException e) {
            LOGGER.error("Email-Adress is not valid", e);
            return  Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (final MessagingException e) {
            LOGGER.error("Sending email failed", e);
            return  Response.status(Status.BAD_GATEWAY).build();
        } finally {
            removeAttachmentsFromFilesystem(files);
        }

        return Response.status(Status.OK).entity(msg).build();
    }

    @SuppressWarnings("unchecked")
    private List<PersonMailinglist> getRecipients(final int listId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("PersonMailinglist.findByMailinglist");
            query.setParameter("listId", listId);
            return query.list();
        } finally {
            session.close();
        }
    }

    private String sendEmails(final List<PersonMailinglist> recipients, final String subject, final String text,
                              final Map<String, File> files) throws MessagingException {
        final StringBuilder sb = new StringBuilder();
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration("de_DE");
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }

        boolean thrownAddressException = false;

        for (final PersonMailinglist recipient : recipients) {
            final String from = getFrom(recipient);
            try {
                final MailDispatchMonitor monitor = mailDispatcher.sendEmailWithAttachments(from, recipient.getAddress(), subject,
                        substitutePlaceholders(text, recipient.getPerson()), files, emailConfiguration.getContentType());
                sb.append(monitor.toString());
            } catch (AddressException e) {
                LOGGER.error("Email-Adress is not valid" + recipient.getAddress(), e);
                // #VERA-382: der String mit "ADDRESS_SYNTAX_NOT_CORRECT:" wird in veraweb-core/mailinglistWrite.vm
                // zum parsen der Fehlerhaften E-Mail Adressen verwendet. Bei Änderungen also auch anpassen.
                sb.append("ADDRESS_SYNTAX_NOT_CORRECT:" + recipient.getAddress()+"\n\n");
                thrownAddressException = true;
            }
        }

        if (thrownAddressException) {
            throw new AddressException(sb.toString());
        }

        return sb.toString();
    }

    private String getFrom(PersonMailinglist recipient) {
        
        return emailConfiguration.getFrom(recipient.getPerson().getFk_orgunit());
    }

    private String substitutePlaceholders(final String text, final Person person) {
        return new PlaceholderSubstitution(person).apply(text);
    }

    private void removeAttachmentsFromFilesystem(final Map<String, File> files) {
        for (final File file : files.values()) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (final IOException e) {
                LOGGER.error(new StringBuilder("The file ").append(file.toPath()).append("could not be deleted!"), e);
            }
        }
    }

    private Map<String, File> getFiles(final List<FormDataBodyPart> fields) {
        final Map<String, File> files = new HashMap<>();
        if (fields != null) {
            for (final FormDataBodyPart part : fields) {
                if (part.getFormDataContentDisposition().getFileName() == null) {
                    continue;
                }
                try {
                    final File destinationFile = saveTempFile(part);
                    files.put(part.getFormDataContentDisposition().getFileName(), destinationFile);
                } catch (final IOException e) {
                    LOGGER.error("Could not write data to temp file!", e);
                    break;
                }
            }
        }
        return files;
    }

    public void setTmpPath(final String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public void setMailDispatcher(MailDispatcher mailDispatcher) {
        this.mailDispatcher = mailDispatcher;
    }

    public void setEmailConfiguration(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }
}
