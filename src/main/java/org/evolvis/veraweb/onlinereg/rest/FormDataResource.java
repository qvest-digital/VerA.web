package org.evolvis.veraweb.onlinereg.rest;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.jboss.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eugen on 16.06.16.
 */
public class FormDataResource extends AbstractResource {
    private static final Logger LOGGER = Logger.getLogger(FormDataResource.class);
    protected String tmpPath = System.getProperty("java.io.tmpdir");

    protected Map<String, File> getFiles(final List<FormDataBodyPart> fields) {
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
}
