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
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.logging.Logger;
//FIXME: it's not "attachment", actually this is the whole shebang, including body, subject, recipients etc...
@Path("/attachment")
@Consumes({ MediaType.MULTIPART_FORM_DATA })
public class AttachmentResource extends AbstractResource {
    private static final Logger LOGGER = Logger.getLogger(AttachmentResource.class);

    private String tmpPath = System.getProperty("java.io.tmpdir");

    @POST
    @Path("/")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response uploadFile(final FormDataMultiPart formData) {
        final String subject = formData.getField("mail-subject").getEntityAs(String.class);
        final String text = formData.getField("mail-text").getEntityAs(String.class);
        final int mailinglistId = Integer.parseInt(formData.getField("mailinglist-id").getEntityAs(String.class));

        final List<PersonMailinglist> recipients = getRecipients(mailinglistId);

        final List<FormDataBodyPart> fields = formData.getFields("files");
        if (fields == null) {
            return Response.noContent().build();
        }

        final Map<String, File> files = getFiles(fields);
        if (files.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        sendEmails(recipients, subject, text, files);
        return Response.status(Status.OK).entity("").build();
    }

    @SuppressWarnings("unchecked")
    private List<PersonMailinglist> getRecipients(final int listId) {
        final Session session = openSession();
        final Query query = session.getNamedQuery("PersonMailinglist.findByMailinglist");
        query.setParameter("listId", listId);
        return query.list();
    }

    private void sendEmails(final List<PersonMailinglist> recipients, final String subject, final String text, final Map<String, File> files) {
        try {
            final EmailConfiguration emailConfiguration = initEmailConfiguration("de_DE");
            final MailDispatcher mailDispatcher = new MailDispatcher(emailConfiguration);
            for (final PersonMailinglist recipient : recipients) {
                
                LOGGER.info("Recipient: " + recipient.getAddress());
                mailDispatcher.sendEmailWithAttachments(emailConfiguration.getFrom(), recipient.getAddress(), subject, substitutePlaceholders(text,recipient.getPerson()), files);
            }
        } catch (final MessagingException e) {
            LOGGER.error("Sending email failed", e);
            e.printStackTrace();
        } finally {
            removeAttachmentsFromFilesystem(files);
        }
    }

    private String substitutePlaceholders(String text, Person person) {
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
        for (final FormDataBodyPart part : fields) {
            try {
                final File destinationFile = saveTempFile(part);
                files.put(part.getFormDataContentDisposition().getFileName(), destinationFile);
            } catch (final IOException e) {
                LOGGER.error(e);
                return new HashMap<>();
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
        return new EmailConfiguration(languageKey);
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
}
