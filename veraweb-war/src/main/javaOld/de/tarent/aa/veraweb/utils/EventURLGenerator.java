package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.Properties;

import de.tarent.octopus.server.OctopusContext;

/**
 * This class is used to generate URL for the free visitors of event.
 *
 * @author Jon Nunez Alvarez
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EventURLGenerator {

	
	/**
     * URL Associated directly to the event.
     *
     * @param cntx The {@link de.tarent.octopus.server.OctopusContext}
     * @param uuid The uuid to build URL for the free visitors.
     *
     * @throws IOException
     */
    public static void setEventUrl(OctopusContext cntx, String uuid) throws IOException {
        final PropertiesReader propertiesReader = new PropertiesReader();

        if(propertiesReader.propertiesAreAvailable() && uuid != null) {
	        final Properties properties = propertiesReader.getProperties();
	        final URLGenerator url = new URLGenerator(properties);
	        cntx.setContent("eventUrl", url.getURLForFreeVisitors() + uuid);
        } else {
	        cntx.setContent("eventUrl", "Nicht verf&uuml;gbar");
        }
    }
	
	
}
