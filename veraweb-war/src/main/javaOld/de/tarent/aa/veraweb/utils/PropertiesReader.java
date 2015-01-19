package de.tarent.aa.veraweb.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Picture;

/**
 * This class is used to read the properties for veraweb and online registration.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 * @author Max Marche, tarent solutions GmbH
 */
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
