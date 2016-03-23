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

import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jboss.logging.Logger;

@Path("/attachment")
@Consumes({MediaType.MULTIPART_FORM_DATA})
public class AttachmentResource extends AbstractResource {
    private static final Logger LOGGER = Logger.getLogger(AttachmentResource.class);

    private String tmpPath = System.getProperty("java.io.tmpdir");

    @POST
    public Response uploadFile(final FormDataMultiPart formData) {
        final List<FormDataBodyPart> fields = formData.getFields("files");
        if (fields == null) {
            return Response.noContent().build();
        }

        final Map<String, File> files = getFiles(fields);
        if (files.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        sendEmails(files);
        return Response.status(Status.OK).entity("").build();
    }

    private void sendEmails(Map<String, File> files) {
        try {
            handleSendEmail(files);
        } finally {
            removeAttachmentsFromFilesystem(files);
        }
    }

    private void removeAttachmentsFromFilesystem(Map<String, File> files) {
        for (File file : files.values()) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                LOGGER.error(new StringBuilder("The file ").append(file.toPath()).append("could not be deleted!"), e);
            }
        }
    }

    private Map<String, File> getFiles(List<FormDataBodyPart> fields) {
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

    private void handleSendEmail(Map<String, File> files) {
        try {
            final EmailConfiguration emailConfiguration = initEmailConfiguration("de_DE");
            final MailDispatcher mailDispatcher = new MailDispatcher(emailConfiguration);
            mailDispatcher.sendEmailWithAttachments("from@tarent.de", "to@tarent.de", "subject", "content", files);
        } catch (MessagingException e) {
            LOGGER.error("Sending email failed", e);
            e.printStackTrace();
        }
    }

    private File saveTempFile(FormDataBodyPart part) throws IOException {
        final String filename = part.getFormDataContentDisposition().getFileName();
        final File destinationFile = getTempFile(filename);
        final InputStream inStream = (InputStream) part.getEntity();
        writeToFile(inStream, destinationFile);
        return destinationFile;
    }

    private EmailConfiguration initEmailConfiguration(String languageKey) {
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
