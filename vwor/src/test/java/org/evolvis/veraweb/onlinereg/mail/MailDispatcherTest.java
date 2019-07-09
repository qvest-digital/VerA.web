package org.evolvis.veraweb.onlinereg.mail;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class MailDispatcherTest {
    private static final String PARAM_TO = "to@fa.ke";
    private static final String PARAM_TEXT = "Plain text content mit Umlaut ä";
    private static final String PARAM_SUBJECT = "Subject mit Umlaut ä";
    private static final int PARAM_PORT = 2525;
    private static final String PARAM_HOST = "localhost";
    private static final String PARAM_USER = "user";
    private static final String PARAM_PASS = "pass";
    private static final String PARAM_TESTFILE = "test.txt";

    private Transport transportMock;
    private EmailConfiguration emailConfiguration;
    private MailDispatcher classToTest;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws NoSuchProviderException {
        transportMock = Mockito.mock(Transport.class);
        emailConfiguration = new EmailConfiguration(PARAM_HOST, PARAM_PORT, null, PARAM_USER, PARAM_PASS, "noreply@tarent.de");
        classToTest = new MailDispatcher(emailConfiguration);
        classToTest.setTransport(transportMock);
    }

    @Test
    public void sendVerificationEmail() throws Exception {
        classToTest.sendVerificationEmail("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, "http://link.de");

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> recipients = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), recipients.capture());
        verify(transportMock).close();

        assertEquals(PARAM_SUBJECT, message.getValue().getSubject());
        assertEquals(PARAM_TEXT, (String) message.getValue().getContent());
        assertEquals(PARAM_TO, (recipients.getValue()[0]).toString());
    }

    @Test
    public void sendEmailWithAttachments() throws Exception {
        final Map<String, File> fileMap = new HashMap<String, File>();
        final File tmpFile = testFolder.newFile(PARAM_TESTFILE);
        final String msg = "Test string for file";
        Files.write(Paths.get(tmpFile.getAbsolutePath()), msg.getBytes());
        fileMap.put(PARAM_TESTFILE, tmpFile);

        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, fileMap);

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> recipients = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), recipients.capture());
        verify(transportMock).close();

        final MimeMultipart content = (MimeMultipart) message.getValue().getContent();
        assertEquals(2, content.getCount());
        assertEquals(PARAM_TEXT, (String) ((MimeBodyPart) content.getBodyPart(0)).getContent());
        assertEquals(msg, (String) ((MimeBodyPart) content.getBodyPart(1)).getContent());
    }

    @Test
    public void sendEmailWithAttachmentsNull() throws Exception {
        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, null);

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> recipients = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), recipients.capture());
        verify(transportMock).close();
    }

    @Test
    public void sendEmailWithAttachmentsNoFiles() throws Exception {
        final Map<String, File> emptyFileMap = new HashMap<String, File>();

        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, emptyFileMap);

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> recipients = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), recipients.capture());
        verify(transportMock).close();
    }
}
