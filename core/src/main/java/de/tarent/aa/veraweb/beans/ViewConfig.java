package de.tarent.aa.veraweb.beans;
import de.tarent.octopus.beans.MapBean;

import java.util.Properties;

public class ViewConfig extends MapBean {
    public Boolean showGlobalOpenTab;

    public final static String SHOW_GLOBAL_OPENTAB_KEY = "veraweb.show.global.opentab.button";

    public ViewConfig(Properties properties) {
        showGlobalOpenTab = booleanValueOf(properties.getProperty(SHOW_GLOBAL_OPENTAB_KEY), false);
    }

    public Boolean booleanValueOf(String propertyString, Boolean defaultValue) {
        if (propertyString != null || defaultValue == null) {
            return Boolean.valueOf(propertyString);
        }
        return defaultValue;
    }
}
