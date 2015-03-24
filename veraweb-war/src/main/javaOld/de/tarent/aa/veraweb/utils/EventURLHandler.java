package de.tarent.aa.veraweb.utils;

import java.util.Properties;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.octopus.server.OctopusContext;

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
            return urlGenerator.getURLForFreeVisitors() + event.getHash();
        } else {
            return "";
        }
    }

	/**
     * URL Associated directly to the event.
     *
     * @param cntx The {@link de.tarent.octopus.server.OctopusContext}
     * @param uuid The uuid to build URL for the free visitors.
     */
    public void setEventUrl(OctopusContext cntx, String uuid) {
        if(propertiesReader.propertiesAreAvailable() && uuid != null) {
            final URLGenerator urlGenerator = getUrlGenerator();
	        cntx.setContent("eventUrl", urlGenerator.getURLForFreeVisitors() + uuid);
        } else {
	        cntx.setContent("eventUrl", "Nicht verf&uuml;gbar");
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
