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
