package org.evolvis.veraweb.onlinereg.mail;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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
        emailConfiguration = new EmailConfiguration(PARAM_HOST, PARAM_PORT, null, PARAM_USER, PARAM_PASS, "noreply@tarent.de", "Subject mit Umlat ü",
                "Plain text content mit Umlaut ü", "HTML", "resetPwSubect", "resetPwContent", "subjectResendLogin", "contentResendLogin");
        classToTest = new MailDispatcher(emailConfiguration);
        classToTest.setTransport(transportMock);
    }

    @Test
    public void sendVerificationEmail() throws Exception {
        classToTest.sendVerificationEmail("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, "http://link.de", emailConfiguration.getContentType());

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> receipents = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), receipents.capture());
        verify(transportMock).close();

        assertThat(message.getValue().getSubject(), equalTo(PARAM_SUBJECT));
        assertThat((String) message.getValue().getContent(), equalTo(PARAM_TEXT));
        assertThat((receipents.getValue()[0]).toString(), equalTo(PARAM_TO));
    }

    @Test
    public void sendEmailWithAttachments() throws Exception {
        final Map<String, File> fileMap = new HashMap<String, File>();
        final File tmpFile = testFolder.newFile(PARAM_TESTFILE);
        final String msg = "Test string for file";
        Files.write(Paths.get(tmpFile.getAbsolutePath()), msg.getBytes());
        fileMap.put(PARAM_TESTFILE, tmpFile);

        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, fileMap, "plain");

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> receipents = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), receipents.capture());
        verify(transportMock).close();

        final MimeMultipart content = (MimeMultipart) message.getValue().getContent();
        assertThat(content.getCount(), equalTo(2));
        assertThat((String) ((MimeBodyPart) content.getBodyPart(0)).getContent(), equalTo(PARAM_TEXT));
        assertThat((String) ((MimeBodyPart) content.getBodyPart(1)).getContent(), equalTo(msg));
    }

    @Test
    public void sendEmailWithAttachmentsNull() throws Exception {
        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, null, "plain");

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> receipents = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), receipents.capture());
        verify(transportMock).close();
    }

    @Test
    public void sendEmailWithAttachmentsNoFiles() throws Exception {
        final Map<String, File> emptyFileMap = new HashMap<String, File>();

        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, emptyFileMap, "plain");

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> receipents = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), receipents.capture());
        verify(transportMock).close();
    }
}