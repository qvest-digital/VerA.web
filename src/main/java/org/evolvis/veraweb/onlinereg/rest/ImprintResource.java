package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Imprint;
import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by mweier on 27.12.16.
 */
@Path("/imprint")
@Produces(MediaType.APPLICATION_JSON)
public class ImprintResource extends AbstractResource{

    private VworPropertiesReader vworPropertiesReader;

    @GET
    @Path("/{language}")
    public Map<String, Imprint> getImprintList(@PathParam("language") String languageKey) {

        if(languageKey == null || languageKey.equals("")) {
            return null;
        }

        final VworPropertiesReader propertiesReader = getVworPropertiesReader();

        final Map<String, String> mapHeading = new HashMap<>();
        final Map<String, String> mapContent = new HashMap<>();
        final List<String> keyList = new ArrayList<>();

        for(String key : propertiesReader.getProperties().stringPropertyNames()){
            if(key.startsWith("imprint." + languageKey + ".heading.")){
                mapHeading.put(getSubKey(keyList, key), propertiesReader.getProperty(key));
            } else if (key.startsWith("imprint." + languageKey + ".content.")){
                mapContent.put(getSubKey(keyList, key), propertiesReader.getProperty(key));
            }
        }

        return collapseMaps(mapHeading, mapContent, keyList);
    }

    private Map<String, Imprint> collapseMaps(Map<String, String> mapHeading, Map<String, String> mapContent, List<String> keyList) {
        final Map<String, Imprint> map = new HashMap<>();

        for(String key : keyList){
            map.put(key, new Imprint(mapHeading.get(key), mapContent.get(key)));
        }

        return map;
    }

    private String getSubKey(List keyList, String key){
        String subKey = key.substring(22);
        keyList.add(subKey);
        return subKey;
    }

    private VworPropertiesReader getVworPropertiesReader() {
        if (vworPropertiesReader == null) {
            vworPropertiesReader = new VworPropertiesReader();
        }
        return vworPropertiesReader;
    }
}
