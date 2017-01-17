package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public HashMap<String, String> getImprintList(@PathParam("language") String languageKey) {

        if(languageKey == null || languageKey.equals("")) {
            return null;
        }

        final VworPropertiesReader propertiesReader = getVworPropertiesReader();
        final HashMap<String, String> map = new HashMap<>();

        for(String key : propertiesReader.getProperties().stringPropertyNames()){
            if(key.startsWith("imprint." + languageKey)){
                map.put(key.substring(14), propertiesReader.getProperty(key));
            }
        }

        return map;
    }

    private VworPropertiesReader getVworPropertiesReader() {
        if (vworPropertiesReader == null) {
            vworPropertiesReader = new VworPropertiesReader();
        }
        return vworPropertiesReader;
    }
}
