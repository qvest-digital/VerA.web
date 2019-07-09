package org.evolvis.veraweb.onlinereg.rest;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by eugen on 16.06.16.
 */
public class FormDataResource extends AbstractResource {
    protected String tmpPath = System.getProperty("java.io.tmpdir");

    File saveTempFile(final FormDataBodyPart part) throws IOException {
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
        try {
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } finally {
            out.close();
        }
    }
}
