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
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.octopus.server.OctopusContext;

import java.util.Properties;

/**
 * This class is used to generate URL for the free visitors of event.
 *
 * @author Jon Nunez Alvarez
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventURLHandler {

    private PropertiesReader propertiesReader = new PropertiesReader();

    /**
     * URL Associated directly to the event
     *
     * @param event The {@link de.tarent.aa.veraweb.beans.Event}
     *
     * @return The url
     */
    public String generateEventUrl(Event event) {
        if(propertiesReader.propertiesAreAvailable() && event.getHash() != null) {
            final URLGenerator urlGenerator = getUrlGenerator();
            return urlGenerator.getUrlForFreeVisitors() + event.getHash();
        } else {
            return "";
        }
    }

	/**
     * URL Associated directly to the event.
     *
     * @param octopusContext The {@link de.tarent.octopus.server.OctopusContext}
     * @param uuid The uuid to build URL for the free visitors.
     */
    public void setEventUrl(OctopusContext octopusContext, String uuid) {
        if(propertiesReader.propertiesAreAvailable() && uuid != null) {
            final URLGenerator urlGenerator = getUrlGenerator();
	        octopusContext.setContent("eventUrl", urlGenerator.getUrlForFreeVisitors() + uuid);
        } else {
            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);

	        octopusContext.setContent("eventUrl", languageProvider.getProperty("GENERIC_NOT_AVAILABLE"));
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
