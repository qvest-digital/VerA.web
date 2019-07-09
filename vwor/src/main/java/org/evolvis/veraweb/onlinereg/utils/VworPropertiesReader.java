package org.evolvis.veraweb.onlinereg.utils;
import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 28.09.15.
 */
@Log4j2
public class VworPropertiesReader {
    private static final String PROPERTY_FILE = "/etc/veraweb/vwor.properties";

    private Properties properties;

    public VworPropertiesReader() {
        loadProperties();
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        if (properties != null) {
            return properties.getProperty(key);
        } else {
            logger.warn("Property " + key + " is null.");
        }
        return null;
    }

    private Properties loadProperties() {
        properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PROPERTY_FILE);
            properties.load(inputStream);
        } catch (IOException e) {
            logger.warn("Could not read properties file", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.warn("Could not close stream", e);
            }
        }

        return properties;
    }
}
