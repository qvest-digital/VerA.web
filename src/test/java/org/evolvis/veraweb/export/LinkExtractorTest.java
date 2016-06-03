package org.evolvis.veraweb.export;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class LinkExtractorTest {


    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    
    @Mock
    private ResultSet rs;

    
    @Test
    public void createsLinkByPrependingAPrefix() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("prefix", "http://blabla.foo.de/bar/baz");
        when(rs.getString(43)).thenReturn("gnaaahahahaha");
        Object extractedValue = new LinkExtractor(properties).extractValue(rs, 42);
        assertEquals("http://blabla.foo.de/bar/baz/gnaaahahahaha",extractedValue);
    }
    
    @Test
    public void removesRedundantSlashes() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("prefix", "http://blabla.foo.de/bar/baz/");
        when(rs.getString(43)).thenReturn("gnaaahahahaha");
        Object extractedValue = new LinkExtractor(properties).extractValue(rs, 42);
        assertEquals("http://blabla.foo.de/bar/baz/gnaaahahahaha",extractedValue);
    }
    
    @Test
    public void returnsNullIfColumnContainsNowValue() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("prefix", "http://blabla.foo.de/bar/baz");
        when(rs.getString(43)).thenReturn(null);
        Object extractedValue = new LinkExtractor(properties).extractValue(rs, 42);
        assertNull(extractedValue);
    }
    
    @Test
    public void canReadPrefixFromCustomProperty() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("oa.path.delegation", "http://blabla.foo.de/bar/baz");
        properties.setProperty("prefixPropertyName", "oa.path.delegation");
        when(rs.getString(43)).thenReturn("gnaaahahahaha");
        Object extractedValue = new LinkExtractor(properties).extractValue(rs, 42);
        assertEquals("http://blabla.foo.de/bar/baz/gnaaahahahaha",extractedValue);
    }

}
