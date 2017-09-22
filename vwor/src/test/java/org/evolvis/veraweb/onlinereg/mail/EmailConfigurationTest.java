package org.evolvis.veraweb.onlinereg.mail;

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