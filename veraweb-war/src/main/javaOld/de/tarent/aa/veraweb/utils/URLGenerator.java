package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class URLGenerator {

    private static final String VERAWEB_MEDIA_PROTOCOL = "veraweb.press.protocol";
    private static final String VERAWEB_MEDIA_HOST = "veraweb.press.host";
    private static final String VERAWEB_MEDIA_PORT = "veraweb.press.port";
    private static final String VERAWEB_MEDIA_PATH = "veraweb.press.path";

    private Properties properties;

    public URLGenerator(Properties properties) {
        this.properties = properties;
    }

    public String getURLForMediaRepresentatives(Integer eventId) {
        String protocol = properties.getProperty(VERAWEB_MEDIA_PROTOCOL);
        String host = properties.getProperty(VERAWEB_MEDIA_HOST);
        String port = properties.getProperty(VERAWEB_MEDIA_PORT);
        String path = properties.getProperty(VERAWEB_MEDIA_PATH);
        return protocol + "://" + host + ":" + port + "/#/media/";
    }

    public String getURLForDelegation(){
        // TODO
        return null;
    }

    private String getURLForFreeVisitors(){
        // TODO
        return null;
    }
}
