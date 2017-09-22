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
import java.util.Properties;

/**
 * This class is used to generate URL for person (for example: reset password url in the online registration).
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class PersonURLHandler {

    private PropertiesReader propertiesReader = new PropertiesReader();

    /**
     * URL Associated directly to the event
     *
     * @param uuid The uuid of the person who will reset own password.
     *
     * @return URL to reset password
     */
    public String generateResetPasswordUrl(String uuid) {
        if(propertiesReader.propertiesAreAvailable() && uuid != null) {
            final URLGenerator urlGenerator = getUrlGenerator();
            return urlGenerator.getUrlForPasswordReset() + uuid;
        } else {
            return "";
        }
    }

    private URLGenerator getUrlGenerator() {
        final Properties properties = propertiesReader.getProperties();
        return new URLGenerator(properties);
    }

    public void setPropertiesReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }
}
