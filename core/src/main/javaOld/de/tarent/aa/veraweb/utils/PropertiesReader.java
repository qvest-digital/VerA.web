package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
    	return this.properties != null;
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
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }

        return properties;
    }
}
