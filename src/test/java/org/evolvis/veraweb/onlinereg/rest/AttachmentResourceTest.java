package org.evolvis.veraweb.onlinereg.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AttachmentResourceTest {
    private static final String[] PATHS = { "ab", "aa\\bb", "aa.bb", "aa..bb" };
    private static final String[] INVALID_PATHS = { "../ab", "/aa/bb", "./aa" };
    private static final String TEST_DATA = "Das sind jetzt Testdaten...";
    private static final String FILE_NAME = "test.txt";

    private AttachmentResource objectToTest;
    private String tmpPath;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        objectToTest = new AttachmentResource();
        tmpPath = testFolder.getRoot().getCanonicalPath();
        objectToTest.setTmpPath(tmpPath);
    }

    @Test
    public void getTempFileWithValidPaths() throws IOException {
        for (final String element : PATHS) {
            assertThat(objectToTest.getTempFile(element).getCanonicalPath(), startsWith(tmpPath + File.separator + element));
        }
    }

    @Test
    public void getTempFileWithInvalidPaths() throws IOException {
        for (final String element : INVALID_PATHS) {
            try {
                objectToTest.getTempFile(element).getCanonicalPath();
                fail("Expected an exception!");
            } catch (final Exception e) {
                // OK
            }
        }
    }

    @Test
    public void uploadFile() throws IOException {
        final StreamDataBodyPart filePart = new StreamDataBodyPart("files", new ByteArrayInputStream(TEST_DATA.getBytes()), FILE_NAME);
        final FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("file-name", FILE_NAME).bodyPart(filePart);

        final Response response = objectToTest.uploadFile(multipart);

        formDataMultiPart.close();
        multipart.close();

        assertThat(response.getStatus(), equalTo(200));

        final String[] files = testFolder.getRoot().list();
        assertThat(files.length, equalTo(1));
        assertThat(files[0], startsWith(FILE_NAME));
    }
}
