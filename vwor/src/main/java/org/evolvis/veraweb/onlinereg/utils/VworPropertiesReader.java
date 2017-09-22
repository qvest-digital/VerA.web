package org.evolvis.veraweb.onlinereg.utils;

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
import org.jboss.logging.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 28.09.15.
 */
public class VworPropertiesReader {
	private static final Logger LOGGER = Logger.getLogger(VworPropertiesReader.class);
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
        	LOGGER.warn("Property " + key + " is null.");
        }
        return null;
    }

    public boolean propertiesAreAvailable() {
        return (this.properties == null) ? false : true;
    }

    private Properties loadProperties() {
        properties = new Properties();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PROPERTY_FILE);
            properties.load(inputStream);
        } catch (IOException e) {
        	LOGGER.warn("Could not read properties file", e);
            return null;
        } finally {
            try {
            	if(inputStream != null) {
            		inputStream.close();
            	}
            } catch (Exception e) {
            	LOGGER.warn("Could not close stream", e);
            }
        }

        return properties;
    }

}
