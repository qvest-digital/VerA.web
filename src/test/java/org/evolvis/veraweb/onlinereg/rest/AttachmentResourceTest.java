package org.evolvis.veraweb.onlinereg.rest;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * FIXME: Write an integration test with JerseyTest, see
 * https://blog.codecentric
 * .de/en/2012/05/writing-lightweight-rest-integration-tests
 * -with-the-jersey-test-framework/
 */
public class AttachmentResourceTest {
    private static final String[] PATHS = { "ab", "aa\\bb", "aa.bb", "aa..bb" };
    private static final String[] INVALID_PATHS = { "../ab", "/aa/bb", "./aa" };

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
}
