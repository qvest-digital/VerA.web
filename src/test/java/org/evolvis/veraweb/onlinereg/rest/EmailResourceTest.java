package org.evolvis.veraweb.onlinereg.rest;

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
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailResourceTest {

    private EmailResource emailResource;

    @Before
    public void setUp() throws Exception {
        emailResource = new EmailResource();
    }

    @Test
    public void testSendEmailVerification() throws Exception {
        // GIVEN
        final MailDispatcher mailDispatcher = mock(MailDispatcher.class);
        final EmailConfiguration emailConfiguration = mock(EmailConfiguration.class);
        emailResource.setMailDispatcher(mailDispatcher);
        emailResource.setEmailConfiguration(emailConfiguration);
        doNothing().when(mailDispatcher).sendVerificationEmail(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class));

        // WHEN
        emailResource.sendEmailVerification("test@test.com", "http://endpoint.de/rest/", "activation_token", "de_DE", false);

        // THEN
        verify(mailDispatcher, times(1)).sendVerificationEmail(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class));
        verify(emailConfiguration, times(1)).getSubject();
        verify(emailConfiguration, times(1)).getContent();
    }
}