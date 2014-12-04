package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class URLGenerator {

    private static final String VERAWEB_ONLINEREG_PROTOCOL = "veraweb.onlinereg.protocol";
    private static final String VERAWEB_ONLINEREG_HOST = "veraweb.onlinereg.host";
    private static final String VERAWEB_ONLINEREG_PORT = "veraweb.onlinereg.port";

    private Properties properties;

    public URLGenerator(Properties properties) {
        this.properties = properties;
    }

    public String getURLForMediaRepresentatives(Integer eventId) {
        String protocol = properties.getProperty(VERAWEB_ONLINEREG_PROTOCOL);
        String host = properties.getProperty(VERAWEB_ONLINEREG_HOST);
        String port = properties.getProperty(VERAWEB_ONLINEREG_PORT);
        return protocol + "://" + host + ":" + port + "/#/media/";
    }

    public String getURLForDelegation(){
        String protocol = properties.getProperty(VERAWEB_ONLINEREG_PROTOCOL);
        String host = properties.getProperty(VERAWEB_ONLINEREG_HOST);
        String port = properties.getProperty(VERAWEB_ONLINEREG_PORT);
        return protocol + "://" + host + ":" + port + "/#/delegation/";
    }

    private String getURLForFreeVisitors(){
        String protocol = properties.getProperty(VERAWEB_ONLINEREG_PROTOCOL);
        String host = properties.getProperty(VERAWEB_ONLINEREG_HOST);
        String port = properties.getProperty(VERAWEB_ONLINEREG_PORT);
        return protocol + "://" + host + ":" + port + "/#/freevisitors/";
    }
}
