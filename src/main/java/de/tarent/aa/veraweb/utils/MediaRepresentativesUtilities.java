package de.tarent.aa.veraweb.utils;

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

        if(propertiesReader.propertiesAreAvailable() && event.mediarepresentatives != null) {
            Properties properties = propertiesReader.getProperties();
            URLGenerator url = new URLGenerator(properties);
            url.getURLForMediaRepresentatives();
            octopusContext.setContent("pressevertreterUrl", url.getURLForMediaRepresentatives() + event.mediarepresentatives);
        } else {
            octopusContext.setContent("pressevertreterUrl", "Nicht verf&uuml;gbar");
        }
    }
}
