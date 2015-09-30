package de.tarent.aa.veraweb.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.tarent.octopus.server.OctopusContext;


import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by csalib on 29.09.15.
 */
public class VworUtils {

    /** Jackson Object mapper */
    private ObjectMapper mapper = new ObjectMapper();

    /** Jersey Client */
    final Client client = Client.create();


    /**
     * @return Path of the saved images of the guests
     * @throws IOException
     */
    private String getImagePath() throws IOException {
        PropertiesReader propertiesReader = new PropertiesReader();
        String imagePath = propertiesReader.getProperty("vwor.endpoint");
        return imagePath;
    }


    /**
     * Method sending Requests to VWOR
     * @return Entities from the Vwor component
     * @throws IOException
     */
    public <T> T readResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            final String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                resource = client.resource(path);
                final String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }
        } catch (UniformInterfaceException uie) {
            throw uie;
        }
    }
}
