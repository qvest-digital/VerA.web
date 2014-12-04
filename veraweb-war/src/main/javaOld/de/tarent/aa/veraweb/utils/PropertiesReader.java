package de.tarent.aa.veraweb.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is used to read the properties for veraweb and online registration.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class PropertiesReader {

    private static final String PROPERTY_FILE = "/etc/veraweb/veraweb.properties";

    public Properties properties;

    public Properties getProperties() throws IOException {
        final Properties properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PROPERTY_FILE);
            properties.load(inputStream);
        } finally {
            try { inputStream.close(); } catch (Exception e) { }
        }

        return properties;
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
}
