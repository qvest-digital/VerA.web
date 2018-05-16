package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.beans.MapBean;

import java.util.Properties;

public class ViewConfig extends MapBean {

    public Boolean SHOW_INTERNAL_ID;
    public Boolean SHOW_VERAWEB_ID = Boolean.TRUE;

    public final static String SHOW_INTERNAL_ID_KEY = "veraweb.show.person.internalId";
    public final static String SHOW_VERWEB_ID_KEY = "veraweb.show.person.verawebId";

    public ViewConfig(Properties properties) {
        SHOW_INTERNAL_ID = Boolean.valueOf(properties.getProperty(SHOW_INTERNAL_ID_KEY));
        if(properties.getProperty(SHOW_VERWEB_ID_KEY) != null) {
            SHOW_VERAWEB_ID = Boolean.valueOf(properties.getProperty(SHOW_VERWEB_ID_KEY));
        }
    }
}
