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
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class MediaRepresentativesUtilities {

    private Event event;
    private OctopusContext octopusContext;

    public MediaRepresentativesUtilities(final OctopusContext octopusContext, final Event event) {
        this.octopusContext = octopusContext;
        this.event = event;
    }


    public void setUrlForMediaRepresentatives() throws IOException {
        PropertiesReader propertiesReader = new PropertiesReader();

        if(event != null && propertiesReader.propertiesAreAvailable() && event.mediarepresentatives != null) {
            Properties properties = propertiesReader.getProperties();
            URLGenerator url = new URLGenerator(properties);
            url.getUrlForMediaRepresentatives();
            octopusContext.setContent("pressevertreterUrl", url.getUrlForMediaRepresentatives() + event.mediarepresentatives);
        } else {
            octopusContext.setContent("pressevertreterUrl", "Nicht verf&uuml;gbar");
        }
    }
}
