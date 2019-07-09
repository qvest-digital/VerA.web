package org.evolvis.veraweb.export;
import de.tarent.extract.ResultSetValueExtractor;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CsvExporterTest {

    private CsvExporter csvExporter;
    private Writer writer;
    private JdbcDataSource dataSource;
    private InputStreamReader reader;
    private Properties properties;

    public static class MyColumn implements ResultSetValueExtractor {

        final private Properties props;

        public MyColumn(Properties props) {
            this.props = props;
        }

        public Object extractValue(ResultSet rs, int col) throws SQLException {
            return getBaseUrl() + "?id=" + rs.getInt(col + 1);
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
        reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("test-query.jsn"), "utf-8");
        properties = new Properties();
        properties.setProperty("baseUrl", "http://fnord-west.eu/api");
        csvExporter = new CsvExporter(reader, writer, dataSource, properties);
    }

    @Test
    public void test() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        csvExporter.export(map, new HashMap<>());
        assertEquals("id,firstname,lastname,link\r\n1,Ford,Prefect,http://fnord-west.eu/api?id=1\r\n", writer.toString());
    }
}
