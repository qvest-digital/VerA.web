package org.evolvis.veraweb.export;
import org.evolvis.veraweb.util.DelegationPasswordGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void setup() {
        final Calendar cal = Calendar.getInstance();
        cal.set(1978, 0, 9);
        eventBegin = cal.getTime();
        eventName = "3. Treffen der anonymen Scrum-Leugner";
        companyName = "tarent solutions GmbH";
        lenient().when(generator.generatePassword(anyString(), any(Date.class), anyString())).thenReturn("generated password");
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
    public void returnsNullIfColumnValueIsNull() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("event.shortname", eventName);
        properties.setProperty("event.begin", String.valueOf(eventBegin.getTime()));

        when(rs.getString(43)).thenReturn(null);
        Object extractedValue = new DelegationPasswordExtractor(properties, generator).extractValue(rs, 42);
        verify(generator, times(0)).generatePassword(eventName, eventBegin, companyName);
        assertNull(extractedValue);
    }

    @Test
    public void canReadEventBeginAndEventNameFromCustomProperties() throws SQLException {
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
