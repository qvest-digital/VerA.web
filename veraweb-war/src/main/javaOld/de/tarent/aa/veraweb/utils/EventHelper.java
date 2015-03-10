package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.Properties;

import de.tarent.octopus.server.OctopusContext;

public class EventHelper {

	
	/**
     * URL Associated directly to the event
     *
     * @param cntx
     * @param event
     * @throws IOException
     */
    public static void setEventUrl(OctopusContext cntx, String hash) throws IOException {
        PropertiesReader propertiesReader = new PropertiesReader();

        if(propertiesReader.propertiesAreAvailable() && hash != null) {
	        Properties properties = propertiesReader.getProperties();
	        URLGenerator url = new URLGenerator(properties);
	        cntx.setContent("eventUrl", url.getURLForFreeVisitors() + hash);
        } else {
	        cntx.setContent("eventUrl", "Nicht verf&uuml;gbar");
        }
    }
	
	
}
