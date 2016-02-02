package org.evolvis.veraweb.onlinereg.rest;

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
        doNothing().when(mailDispatcher).send(any(String.class),any(String.class),any(String.class),any(String.class),any(String.class), any(String.class));

        // WHEN
        emailResource.sendEmailVerification("test@test.com", "http://endpoint.de/rest/", "activation_token", "de_DE");

        // THEN
        verify(mailDispatcher, times(1)).send(any(String.class),any(String.class),any(String.class),any(String.class),any(String.class),any(String.class));
        verify(emailConfiguration, times(1)).getSubject();
        verify(emailConfiguration, times(1)).getContent();
    }
}