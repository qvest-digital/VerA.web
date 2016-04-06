package org.evolvis.veraweb.onlinereg.rest;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonMailinglist;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;

/**
 * FIXME: Write an integration test with JerseyTest, see
 * https://blog.codecentric
 * .de/en/2012/05/writing-lightweight-rest-integration-tests
 * -with-the-jersey-test-framework/
 */
@RunWith(MockitoJUnitRunner.class)
public class MailingResourceTest {
    private static final String[] PATHS = { "ab", "aa\\bb", "aa.bb", "aa..bb" };
    private static final String[] INVALID_PATHS = { "../ab", "/aa/bb", "./aa" };

    @Mock
    private static SessionFactory sessionFactory;
    @Mock
    private static Session session;

    private MailingResource objectToTest;
    private String tmpPath;

    @Mock
    FormDataMultiPart formData;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        objectToTest = new MailingResource();
        tmpPath = testFolder.getRoot().getCanonicalPath();
        objectToTest.setTmpPath(tmpPath);
        objectToTest.context = mock(ServletContext.class);
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
    public void testUploadFile() throws MessagingException {
        // GIVEN
        prepareSession();
        FormDataBodyPart formDataBodyPart1 = mock(FormDataBodyPart.class);
        FormDataBodyPart formDataBodyPart2 = mock(FormDataBodyPart.class);
        FormDataBodyPart formDataBodyPart3 = mock(FormDataBodyPart.class);
        when(formData.getField("mail-subject")).thenReturn(formDataBodyPart1);
        when(formData.getField("mail-text")).thenReturn(formDataBodyPart2);
        when(formData.getField("mailinglist-id")).thenReturn(formDataBodyPart3);
        when(formDataBodyPart1.getEntityAs(String.class)).thenReturn("subject");
        when(formDataBodyPart2.getEntityAs(String.class)).thenReturn("text");
        when(formDataBodyPart3.getEntityAs(String.class)).thenReturn("1");
        Query query = mock(Query.class);
        when(session.getNamedQuery("PersonMailinglist.findByMailinglist")).thenReturn(query);
        final PersonMailinglist e = new PersonMailinglist();
        e.setAddress("test@test");
        e.setMailinglistId(1);
        e.setPerson(mock(Person.class));
        e.setPk(1);
        List ids = new ArrayList();
        ids.add(e);
        when(query.list()).thenReturn(ids);

        // WHEN
        objectToTest.uploadFile(formData);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
    }

    private void prepareSession() {
        when(objectToTest.context.getAttribute("SessionFactory")).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }
}
