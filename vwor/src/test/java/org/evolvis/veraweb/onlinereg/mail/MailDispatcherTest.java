package org.evolvis.veraweb.onlinereg.mail;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
    @SuppressWarnings("FieldCanBeLocal")
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
        assertEquals(PARAM_TEXT, message.getValue().getContent());
        assertEquals(PARAM_TO, (recipients.getValue()[0]).toString());
    }

    @Test
    public void sendEmailWithAttachments() throws Exception {
        final Map<String, File> fileMap = new HashMap<>();
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
        assertEquals(PARAM_TEXT, content.getBodyPart(0).getContent());
        assertEquals(msg, content.getBodyPart(1).getContent());
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
        final Map<String, File> emptyFileMap = new HashMap<>();

        classToTest.sendEmailWithAttachments("from", PARAM_TO, PARAM_SUBJECT, PARAM_TEXT, emptyFileMap);

        final ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        final ArgumentCaptor<Address[]> recipients = ArgumentCaptor.forClass(Address[].class);

        verify(transportMock).connect(PARAM_HOST, PARAM_USER, PARAM_PASS);
        verify(transportMock).sendMessage(message.capture(), recipients.capture());
        verify(transportMock).close();
    }
}
