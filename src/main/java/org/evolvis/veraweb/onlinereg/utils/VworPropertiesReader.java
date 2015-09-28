package org.evolvis.veraweb.onlinereg.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 28.09.15.
 */
public class VworPropertiesReader {

    private static final String PROPERTY_FILE = "/etc/veraweb/vwor.properties";

    public Properties properties;

    public VworPropertiesReader() {
        this.properties = this.loadProperties();
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public boolean propertiesAreAvailable() {
        return (this.properties == null) ? false : true;
    }

    private Properties loadProperties() {
        final Properties properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PROPERTY_FILE);
            properties.load(inputStream);
        } catch (IOException e) {
            return null;
        } finally {
            try { inputStream.close(); } catch (Exception e) { }
        }

        return properties;
    }

}
