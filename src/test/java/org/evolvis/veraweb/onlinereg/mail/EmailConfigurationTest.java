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
        emailConfiguration = new EmailConfiguration();
        emailConfiguration.setVworPropertiesReader(propertiesReader);
    }

    @Test
    public void testReadProperties() throws Exception {
        emailConfiguration.readProperties("de_DE", propertiesReader);

        verify(propertiesReader, atLeast(7)).getProperty(any(String.class));
        verify(propertiesReader, atMost(8)).getProperty(any(String.class));
    }
}