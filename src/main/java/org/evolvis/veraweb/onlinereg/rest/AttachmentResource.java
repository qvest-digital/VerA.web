package org.evolvis.veraweb.onlinereg.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jboss.logging.Logger;

public class AttachmentResource {
    private static final Logger LOGGER = Logger.getLogger(AttachmentResource.class);

    private String tmpPath = System.getProperty("java.io.tmpdir");

    @POST
    @Path("/attachment")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(final FormDataMultiPart formData) {
        final List<FormDataBodyPart> fields = formData.getFields("files");
        if (fields == null) {
            return Response.noContent().build();
        }

        for (final FormDataBodyPart part : fields) {
            final String filename = part.getFormDataContentDisposition().getFileName();
            try {
                final File destination = getTempFile(filename);
                final InputStream inStream = (InputStream) part.getEntity();
                writeToFile(inStream, destination);
            } catch (final IOException e) {
                LOGGER.error(e);
                return Response.status(Status.BAD_REQUEST).build();
            }
        }

        return Response.status(200).entity("").build();
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
