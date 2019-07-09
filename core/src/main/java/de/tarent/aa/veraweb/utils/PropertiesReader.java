package de.tarent.aa.veraweb.utils;
import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is used to read the properties for veraweb and online registration.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 * @author Max Marche, tarent solutions GmbH
 */
@Log4j2
public class PropertiesReader {
    private static final String PROPERTY_FILE = "/etc/veraweb/veraweb.properties";

    public Properties properties;

    public PropertiesReader() {
        this.properties = this.loadProperties();
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public boolean propertiesAreAvailable() {
        return this.properties != null;
    }

    private Properties loadProperties() {
        final Properties properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PROPERTY_FILE);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            logger.fatal("Elementary configuration file {} not found!", PROPERTY_FILE);
            return properties;
        } catch (IOException e) {
            logger.fatal("Exception occured while reading properties from elementary configuration file {}!", PROPERTY_FILE, e);
            return properties;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }

        return properties;
    }
}
