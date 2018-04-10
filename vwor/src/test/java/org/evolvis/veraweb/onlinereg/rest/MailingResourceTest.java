package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
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
        mailinglist.setAddress("test@test");
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
                any(String.class), any(String.class), anyMap(), isNull())).thenReturn(mailDispatchMonitor);
        when(query.list()).thenReturn(ids);

        // WHEN
        objectToTest.uploadFile(formData);

        // THEN
        verify(sessionFactory, times(1)).openSession();
        verify(session, times(1)).close();
        verify(mailDispatcher, times(1)).sendEmailWithAttachments(isNull(), any(String.class),
                any(String.class), any(String.class), anyMap(), isNull());
    }

    private void prepareSession() {
        when(objectToTest.context.getAttribute("SessionFactory")).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
    }
}
