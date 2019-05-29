package org.evolvis.veraweb.onlinereg.mail;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
        emailConfiguration = new EmailConfiguration(PARAM_HOST, PARAM_PORT, null, PARAM_USER, PARAM_PASS, "noreply@tarent.de",
          "Subject mit Umlat ü",
          "Plain text content mit Umlaut ü", "HTML", "resetPwSubect", "resetPwContent", "subjectResendLogin",
          "contentResendLogin");
        classToTest = new MailDispatcher(emailConfiguration);
        classToTest.setTransport(transportMock);
    }

    @Test
    public void sendVerificationEmail() throws Exception {
        classToTest.sendVerificationEmail("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, "http://link.de",
          emailConfiguration.getContentType());

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
