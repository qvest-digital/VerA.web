package org.evolvis.veraweb.export;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.evolvis.veraweb.util.DelegationPasswordGenerator;

import de.tarent.extract.ResultSetValueExtractor;

public class DelegationPasswordExtractor implements ResultSetValueExtractor {

    final private Properties properties;
    final private DelegationPasswordGenerator generator;
    final private String eventName;
    final private Date eventBegin;

    public DelegationPasswordExtractor(Properties properties) {
        this(properties, new DelegationPasswordGenerator());
    }

    public DelegationPasswordExtractor(Properties properties, DelegationPasswordGenerator generator) {
        this.properties = properties;
        this.generator = generator;
        final String eventNameProperty = properties.getProperty("eventNamePropertyName", "event.shortname");
        final String eventBeginProperty = properties.getProperty("eventBeginPropertyName", "event.begin");
        eventName = properties.getProperty(eventNameProperty);
        eventBegin = new Date(Long.parseLong(properties.getProperty(eventBeginProperty)));
    }

    public Object extractValue(ResultSet rs, int col) throws SQLException {
        final String companyName = rs.getString(col + 1);
        if (companyName == null) {
            return null;
        }
        return generator.generatePassword(eventName, eventBegin, companyName);
    }
}
