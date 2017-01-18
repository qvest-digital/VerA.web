package org.evolvis.veraweb.onlinereg.mail;

import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
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


    @Mock
    VworPropertiesReader propertiesReader;

    @Before
    public void setUp() throws Exception {
        when(propertiesReader.getProperty(any(String.class))).thenReturn("42");

        
        
    }

    @Test
    public void testReadProperties() throws Exception {
         new EmailConfiguration("de_DE",propertiesReader);
        // THEN
        verify(propertiesReader, atLeast(8)).getProperty(any(String.class));
        verify(propertiesReader, atMost(13)).getProperty(any(String.class));
    }

    @Test
    public void testReadPropertiesTheSecond() throws Exception {
        // GIVEN
        when(propertiesReader.getProperty("mail.smtp.port")).thenReturn("25");

        // WHEN
        new EmailConfiguration("de_DE",propertiesReader);

        // THEN
        verify(propertiesReader, times(13)).getProperty(any(String.class));
    }
}