package org.evolvis.veraweb.onlinereg.mail;
import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailConfigurationTest {
    @Mock
    private VworPropertiesReader propertiesReader;

    @Before
    public void setUp() {
        when(propertiesReader.getProperty(any(String.class))).thenReturn("42");
    }

    @Test
    public void testReadProperties() {
        new EmailConfiguration(propertiesReader);
        // THEN
        verify(propertiesReader, times(6)).getProperty(any(String.class));
    }

    @Test
    public void testReadPropertiesTheSecond() {
        // GIVEN
        when(propertiesReader.getProperty("mail.smtp.port")).thenReturn("25");

        // WHEN
        new EmailConfiguration(propertiesReader);

        // THEN
        verify(propertiesReader, times(6)).getProperty(any(String.class));
    }
}
