/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package de.tarent.aa.veraweb.utils;

import java.util.Properties;

/**
 * This class is used to generate URLs for delegation, media representatives and free visitors.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class URLGenerator {

    public static final String VERAWEB_ONLINEREG_PROTOCOL = "veraweb.onlinereg.protocol";
    public static final String VERAWEB_ONLINEREG_HOST = "veraweb.onlinereg.host";
    public static final String VERAWEB_ONLINEREG_PORT = "veraweb.onlinereg.port";

    private final String urlPrefix;

    public URLGenerator(Properties properties) {
        String protocol = properties.getProperty(VERAWEB_ONLINEREG_PROTOCOL);
        String host = properties.getProperty(VERAWEB_ONLINEREG_HOST);
        String port = properties.getProperty(VERAWEB_ONLINEREG_PORT);
        urlPrefix = protocol + "://" + host + ":" + port;
    }

    public String getURLForMediaRepresentatives() {
        return urlPrefix + "/#/media/";
    }

    public String getURLForDelegation(){
        return urlPrefix + "/#/delegation/";
    }

    public String getURLForFreeVisitors(){
        return urlPrefix + "/#/freevisitors/";
    }

    public String getURLForPasswordReset(){
        return urlPrefix + "/#/reset/password/";
    }
}
