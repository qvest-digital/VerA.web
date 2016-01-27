package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailResourceTest {

    private EmailResource emailResource;
    @Mock
    private EmailConfiguration emailConfiguration;
    @Mock
    private MailDispatcher mailDispatcher;

    @Before
    public void setUp() throws Exception {
        emailResource = new EmailResource();
        emailResource.setMailDispatcher(mailDispatcher);
        emailResource.setEmailConfiguration(emailConfiguration);
    }

    @Test
    public void testSendEmailVerification() throws Exception {
        emailResource.sendEmailVerification("test@test.com", "http://endpoint.de/rest/", "activation_token");
        doNothing().when(mailDispatcher).send(any(String.class),any(String.class),any(String.class),any(String.class),any(String.class));

        verify(mailDispatcher, times(1)).send(any(String.class),any(String.class),any(String.class),any(String.class),any(String.class));
        verify(emailConfiguration, times(1)).getSubject();
        verify(emailConfiguration, times(1)).getContent();
    }
}