package org.evolvis.veraweb.export;
import de.tarent.extract.ResultSetValueExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class LinkExtractor implements ResultSetValueExtractor {

    public static final String MISSING_PREFIX_ALERT_FALLBACK = "<ONLINE_REGISTRATION_BASE_URL_MISSING>";
    private final String prefix;

    public LinkExtractor(Properties properties) {
        final String prefixPropertyName = properties.getProperty("prefixPropertyName", "prefix");
        String prefixProperty = properties.getProperty(prefixPropertyName);
        prefix = prefixProperty != null ? prefixProperty : MISSING_PREFIX_ALERT_FALLBACK;
    }

    public Object extractValue(ResultSet rs, int col) throws SQLException {
        final String value = rs.getString(col + 1);
        if (value == null) {
            return null;
        }
        if (prefix.endsWith("/")) {
            return prefix + value;
        }
        return prefix + "/" + value;
    }
}
