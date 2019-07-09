package org.evolvis.veraweb.onlinereg.rest;
import lombok.extern.log4j.Log4j2;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonMailinglist;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatchMonitor;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Session;
import org.hibernate.query.Query;

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
import java.util.regex.Pattern;

//FIXME: it's not "attachment", actually this is the whole shebang, including body, subject, recipients etc...
@Path(RestPaths.REST_MAILING)
@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Log4j2
public class MailingResource extends FormDataResource {
    public static final String PARAM_MAILINGLIST_ID = "mailinglist-id";
    public static final String PARAM_MAIL_TEXT = "mailtext";
    public static final String PARAM_MAIL_SUBJECT = "mail-subject";

    //http://emailregex.com/
    private static final Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
      "*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:" +
      "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\" +
      ".){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
      "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    private MailDispatcher mailDispatcher;
    private EmailConfiguration emailConfiguration;

    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
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
            logger.error("Email-Adress is not valid", e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (final MessagingException e) {
            logger.error("Sending email failed", e);
            return Response.status(Status.BAD_GATEWAY).build();
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
            emailConfiguration = new EmailConfiguration();
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }

        boolean thrownAddressException = false;

        for (final PersonMailinglist recipient : recipients) {
            final String from = getFrom(recipient);
            if (pattern.matcher(recipient.getAddress().toLowerCase()).matches()) {
                try {
                    final MailDispatchMonitor monitor = mailDispatcher.sendEmailWithAttachments(from, recipient.getAddress(),
                      subject, substitutePlaceholders(text, recipient.getPerson()), files);
                    sb.append(monitor.toString());
                } catch (AddressException e) {
                    logger.error("Email-Adress is not valid" + recipient.getAddress(), e);
                    // #VERA-382: der String mit "ADDRESS_SYNTAX_NOT_CORRECT:" wird in veraweb-core/mailinglistWrite.vm
                    // zum parsen der Fehlerhaften E-Mail Adressen verwendet. Bei Änderungen also auch anpassen.
                    sb.append("ADDRESS_SYNTAX_NOT_CORRECT:" + recipient.getAddress() + "\n\n");
                    thrownAddressException = true;
                }
            } else {
                logger.error("Email-Adress is not valid" + recipient.getAddress());
                sb.append("ADDRESS_SYNTAX_NOT_CORRECT:" + recipient.getAddress() + "\n\n");
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
                logger.error(new StringBuilder("The file ").append(file.toPath()).append("could not be deleted!"), e);
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
                    logger.error("Could not write data to temp file!", e);
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
