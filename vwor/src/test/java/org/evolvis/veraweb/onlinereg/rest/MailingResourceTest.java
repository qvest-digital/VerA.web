package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonMailinglist;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatchMonitor;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * FIXME: Write an integration test with JerseyTest, see
 * https://blog.codecentric.de/en/2012/05/writing-lightweight-rest-integration-tests-with-the-jersey-test-framework/
 */
@RunWith(MockitoJUnitRunner.class)
public class MailingResourceTest {
    private static final String[] PATHS = { "ab", "aa\\bb", "aa.bb", "aa..bb" };
    private static final String[] INVALID_PATHS = { "../ab", "/aa/bb", "./aa" };

    @Mock
    private static SessionFactory sessionFactory;
    @Mock
    private static Session session;
    @Mock
    private static Transaction mockitoTransaction;

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
            final String requestedStart = tmpPath + File.separator + element;
            final String resultToTest = objectToTest.getTempFile(element).getCanonicalPath();
            assertTrue("Canonical path '" + resultToTest + "' does not start with: " + requestedStart,
              resultToTest.startsWith(requestedStart));
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
    public void testUploadFile() throws MessagingException, IOException {
        // GIVEN
        prepareSession();
        final MailDispatcher mailDispatcher = mock(MailDispatcher.class);
        objectToTest.setMailDispatcher(mailDispatcher);
        objectToTest.setEmailConfiguration(mock(EmailConfiguration.class));
        final FormDataBodyPart formDataBodyPart1 = mock(FormDataBodyPart.class);
        final FormDataBodyPart formDataBodyPart2 = mock(FormDataBodyPart.class);
        final FormDataBodyPart formDataBodyPart3 = mock(FormDataBodyPart.class);
        final FormDataBodyPart formDataBodyPart4 = mock(FormDataBodyPart.class);
        final FormDataBodyPart formDataBodyPart5 = mock(FormDataBodyPart.class);
        final Query query = mock(Query.class);
        final MailDispatchMonitor mailDispatchMonitor = mock(MailDispatchMonitor.class);
        final BodyPartEntity entity1 = mock(BodyPartEntity.class);
        final BodyPartEntity entity2 = mock(BodyPartEntity.class);
        final List<FormDataBodyPart> listFormDataBodyParts = new ArrayList<>();
        listFormDataBodyParts.add(formDataBodyPart4);
        listFormDataBodyParts.add(formDataBodyPart5);
        final PersonMailinglist mailinglist = new PersonMailinglist();
        mailinglist.setAddress("test@test.de");
        mailinglist.setMailinglistId(1);
        mailinglist.setPerson(mock(Person.class));
        mailinglist.setPk(1);
        final List ids = new ArrayList();
        ids.add(mailinglist);
        FormDataContentDisposition fdcd1 = mock(FormDataContentDisposition.class);
        FormDataContentDisposition fdcd2 = mock(FormDataContentDisposition.class);
        when(formData.getField("mail-subject")).thenReturn(formDataBodyPart1);
        when(formData.getField("mailtext")).thenReturn(formDataBodyPart2);
        when(formData.getField("mailinglist-id")).thenReturn(formDataBodyPart3);
        when(formDataBodyPart1.getEntityAs(String.class)).thenReturn("subject");
        when(formDataBodyPart2.getEntityAs(String.class)).thenReturn("text");
        when(formDataBodyPart3.getEntityAs(String.class)).thenReturn("1");
        when(formData.getFields("files")).thenReturn(listFormDataBodyParts);
        when(formDataBodyPart4.getFormDataContentDisposition()).thenReturn(fdcd1);
        when(formDataBodyPart5.getFormDataContentDisposition()).thenReturn(fdcd2);
        when(fdcd1.getFileName()).thenReturn("abcd");
        when(fdcd2.getFileName()).thenReturn("efgh");
        when(formDataBodyPart4.getEntity()).thenReturn(entity1);
        when(formDataBodyPart5.getEntity()).thenReturn(entity2);
        when(entity1.getInputStream()).thenReturn(new ByteArrayInputStream("file1".getBytes(StandardCharsets.UTF_8)));
        when(entity2.getInputStream()).thenReturn(new ByteArrayInputStream("file2".getBytes(StandardCharsets.UTF_8)));
        when(session.getNamedQuery("PersonMailinglist.findByMailinglist")).thenReturn(query);
        when(mailDispatcher.sendEmailWithAttachments(isNull(), any(String.class),
          any(String.class), any(String.class), anyMap())).thenReturn(mailDispatchMonitor);
        when(query.list()).thenReturn(ids);

        // WHEN
        objectToTest.uploadFile(formData);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
        verify(mailDispatcher, times(1)).sendEmailWithAttachments(isNull(), any(String.class),
          any(String.class), any(String.class), anyMap());
    }

    private void prepareSession() {
        when(objectToTest.context.getAttribute("SessionFactory")).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }
}
