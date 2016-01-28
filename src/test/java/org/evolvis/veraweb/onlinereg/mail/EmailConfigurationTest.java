package org.evolvis.veraweb.onlinereg.mail;

import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailConfigurationTest {

    private EmailConfiguration emailConfiguration;

    @Mock
    VworPropertiesReader propertiesReader;

    @Before
    public void setUp() throws Exception {
        emailConfiguration = new EmailConfiguration("de_DE");
        emailConfiguration.setVworPropertiesReader(propertiesReader);
    }

    @Test
    public void testReadProperties() throws Exception {
        // WHEN
        emailConfiguration.readProperties("de_DE", propertiesReader);

        // THEN
        verify(propertiesReader, atLeast(7)).getProperty(any(String.class));
        verify(propertiesReader, atMost(8)).getProperty(any(String.class));
    }

    @Test
    public void testReadPropertiesTheSecond() throws Exception {
        // GIVEN
        when(propertiesReader.getProperty("mail.smtp.port")).thenReturn("25");

        // WHEN
        emailConfiguration.readProperties("de_DE", propertiesReader);

        // THEN
        verify(propertiesReader, times(8)).getProperty(any(String.class));
    }
}