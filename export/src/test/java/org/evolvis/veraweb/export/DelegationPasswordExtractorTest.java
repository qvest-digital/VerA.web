package org.evolvis.veraweb.export;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), export module is
 * Copyright © 2016, 2017
 * 	Атанас Александров <a.alexandrov@tarent.de>
 * Copyright © 2016
 * 	Lukas Degener <l.degener@tarent.de>
 * 	Max Weierstall <m.weierstall@tarent.de>
 * Copyright © 2017
 * 	mirabilos <t.glaser@tarent.de>
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.evolvis.veraweb.util.DelegationPasswordGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class DelegationPasswordExtractorTest {


    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    
    @Mock
    private ResultSet rs;

    @Mock
    private DelegationPasswordGenerator generator;

    private Date eventBegin;
    private String companyName;
    private String eventName;
    
    @Before
    public void setup(){
        
        final Calendar cal = Calendar.getInstance();
        cal.set(1978, 0, 9);
        eventBegin = cal.getTime();
        eventName = "3. Treffen der annynomen Scrum-Leugner";
        companyName = "tarent solutions GmbH";
        when(generator.generatePassword(anyString(), any(Date.class), anyString())).thenReturn("generated password");
    }
    
    @Test
    public void createsPasswordByCallingTheDelegationPasswordGenerator() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("event.shortname", eventName);
        properties.setProperty("event.begin", String.valueOf(eventBegin.getTime()));

        when(rs.getString(43)).thenReturn(companyName);
        Object extractedValue = new DelegationPasswordExtractor(properties, generator).extractValue(rs, 42);
        verify(generator).generatePassword(eventName, eventBegin, companyName);
        assertEquals("generated password", extractedValue);
    }
    
    @Test
    public void returnsNullIfColumnValueIsNull() throws SQLException{
        Properties properties = new Properties();
        properties.setProperty("event.shortname", eventName);
        properties.setProperty("event.begin", String.valueOf(eventBegin.getTime()));

        when(rs.getString(43)).thenReturn(null);
        Object extractedValue = new DelegationPasswordExtractor(properties, generator).extractValue(rs, 42);
        verify(generator,times(0)).generatePassword(eventName, eventBegin, companyName);
        assertNull(extractedValue);
        
    }
    
    @Test
    public void canReadEventBeginAndEventNameFromCustomProperties() throws SQLException{
        Properties properties = new Properties();
        properties.setProperty("eventNamePropertyName", "custom.shortname");
        properties.setProperty("eventBeginPropertyName", "custom.begin");
        properties.setProperty("custom.shortname", eventName);
        properties.setProperty("custom.begin", String.valueOf(eventBegin.getTime()));

        when(rs.getString(43)).thenReturn(companyName);
        Object extractedValue = new DelegationPasswordExtractor(properties, generator).extractValue(rs, 42);
        verify(generator).generatePassword(eventName, eventBegin, companyName);
        assertEquals("generated password", extractedValue);
    }

}
