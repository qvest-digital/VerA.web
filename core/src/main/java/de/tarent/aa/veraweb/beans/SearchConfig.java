package de.tarent.aa.veraweb.beans;
import de.tarent.octopus.beans.MapBean;

import java.util.Properties;

public class SearchConfig extends MapBean {
    public Boolean personPartSearch = Boolean.FALSE;

    public final static String PERSON_PART_SEARCH_KEY = "veraweb.person.part.search";

    public SearchConfig(Properties properties) {
        if(properties.getProperty(PERSON_PART_SEARCH_KEY) != null) {
            personPartSearch = Boolean.valueOf(properties.getProperty(PERSON_PART_SEARCH_KEY));
        }
    }
}
