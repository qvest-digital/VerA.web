package org.evolvis.veraweb.export;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import de.tarent.extract.ResultSetValueExtractor;

public class LinkExtractor implements ResultSetValueExtractor{

    private final String prefix;

    public LinkExtractor(Properties properties) {
        final String prefixPropertyName = properties.getProperty("prefixPropertyName", "prefix");
        prefix = properties.getProperty(prefixPropertyName);
    }

    public Object extractValue(ResultSet rs, int col) throws SQLException {
        final String value = rs.getString(col+1);
        if(value==null){
            return null;
        }
        if(prefix.endsWith("/")){
            return prefix+value;
        }
        return prefix+"/"+value;
    }

}
