package de.tarent.aa.veraweb.utils;

import java.util.Properties;

/**
 * This class is used to generate URLs for delegation, media representatives and free visitors.
 *
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class URLGenerator {

    private static final String VERAWEB_ONLINEREG_PROTOCOL = "veraweb.onlinereg.protocol";
    private static final String VERAWEB_ONLINEREG_HOST = "veraweb.onlinereg.host";
    private static final String VERAWEB_ONLINEREG_PORT = "veraweb.onlinereg.port";

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

    private String getURLForFreeVisitors(){
        return urlPrefix + "/#/freevisitors/";
    }
}
