package org.evolvis.veraweb.onlinereg.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonMailinglist;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatchMonitor;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.logging.Logger;

//FIXME: it's not "attachment", actually this is the whole shebang, including body, subject, recipients etc...
@Path("/mailing")
@Consumes({MediaType.MULTIPART_FORM_DATA})
public class MailingResource extends AbstractResource {
    private static final Logger LOGGER = Logger.getLogger(MailingResource.class);

    public static final String PARAM_MAILINGLIST_ID = "mailinglist-id";
    public static final String PARAM_MAIL_TEXT = "mail-text";
    public static final String PARAM_MAIL_SUBJECT = "mail-subject";

    private String tmpPath = System.getProperty("java.io.tmpdir");
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
        final String msg = sendEmails(recipients, subject, text, files);
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

    private String sendEmails(final List<PersonMailinglist> recipients, final String subject, final String text, final Map<String, File> files) {
        final StringBuilder sb = new StringBuilder();
        try {
            if (emailConfiguration == null) {
                emailConfiguration = initEmailConfiguration("de_DE");
            }
            if (mailDispatcher == null) {
                mailDispatcher = new MailDispatcher(emailConfiguration);
            }
            for (final PersonMailinglist recipient : recipients) {
                final MailDispatchMonitor monitor = mailDispatcher.sendEmailWithAttachments(emailConfiguration.getFrom(), recipient.getAddress(), subject,
                        substitutePlaceholders(text, recipient.getPerson()), files);
                sb.append(monitor.toString());
            }
        } catch (final MessagingException e) {
            LOGGER.error("Sending email failed", e);
            e.printStackTrace();
        } finally {
            removeAttachmentsFromFilesystem(files);
        }
        return sb.toString();
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

    private File saveTempFile(final FormDataBodyPart part) throws IOException {
        final String filename = part.getFormDataContentDisposition().getFileName();
        final File destinationFile = getTempFile(filename);
        final BodyPartEntity entity = (BodyPartEntity) part.getEntity();
        writeToFile(entity.getInputStream(), destinationFile);
        return destinationFile;
    }

    private EmailConfiguration initEmailConfiguration(final String languageKey) {
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        emailConfiguration.loadProperties(languageKey);

        return emailConfiguration;
    }

    public File getTempFile(final String filename) throws IOException {
        final File file = Files.createTempFile(Paths.get(tmpPath), filename, ".tmp").toFile();
        if (!file.getParent().equals(tmpPath)) {
            throw new IOException("Not a valid filename");
        }
        file.deleteOnExit();
        return file;
    }

    // save uploaded file to new location
    private void writeToFile(final InputStream uploadedInputStream, final File destination) throws IOException {
        int read = 0;
        final byte[] bytes = new byte[1024];

        final OutputStream out = new FileOutputStream(destination);
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
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
