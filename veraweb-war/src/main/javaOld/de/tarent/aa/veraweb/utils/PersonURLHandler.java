package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.octopus.server.OctopusContext;

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
            return urlGenerator.getURLForPasswordReset() + uuid;
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
