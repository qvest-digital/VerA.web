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
import static org.mockito.Mockito.mock;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

import de.tarent.extract.ResultSetValueExtractor;

public class CsvExporterTest {

    private CsvExporter csvExporter;
    private Writer writer;
    private JdbcDataSource dataSource;
    private InputStreamReader reader;
    private Properties properties;

    public static class MyColumn implements ResultSetValueExtractor{

        final private Properties props;

        public MyColumn(Properties props) {
            this.props = props;
        }
        
        public Object extractValue(ResultSet rs, int col) throws SQLException {
            return getBaseUrl()+"?id="+rs.getInt(col + 1);
        }

        private Object getBaseUrl() {
            return props != null && props.containsKey("baseUrl") ? props.get("baseUrl") : "/";
        }
        
    }
    
    @Before
    public void setup() throws UnsupportedEncodingException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:fnord;INIT=RUNSCRIPT FROM 'classpath:test-schema.sql'");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        writer = new StringWriter();
        reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("test-query.yaml"), "utf-8");
        properties = new Properties();
        properties.setProperty("baseUrl", "http://fnord-west.eu/api");
        csvExporter = new CsvExporter(reader, writer, dataSource,properties);

    }

    @Test
    public void test() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        csvExporter.export(map);
        assertEquals("id,firstname,lastname,link\r\n1,Ford,Prefect,http://fnord-west.eu/api?id=1\r\n", writer.toString());
    }

}
