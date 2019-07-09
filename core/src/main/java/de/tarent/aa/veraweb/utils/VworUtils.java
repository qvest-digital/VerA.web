package de.tarent.aa.veraweb.utils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by csalib on 29.09.15.
 *
 * @author csalib
 * @author jnunez
 * @author tglase
 */
@Log4j2
public class VworUtils {
    final private static String BASE_RESOURCE = "/rest";

    /**
     * Jackson Object mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Jersey Client
     */
    private Client client;

    public VworUtils() {
        client = Client.create();
        client.addFilter(getAuthorization());
    }

    /**
     * @return Path of the saved images of the guests
     */
    public String getVworEndPoint() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String endpoint = propertiesReader.getProperty("vwor.endpoint");
        if (endpoint == null) {
            logger.warn("vwor.endpoint is nil");
        }
        return endpoint;
    }

    public HTTPBasicAuthFilter getAuthorization() {
        return new HTTPBasicAuthFilter(getVworAuthUsername(), getVworAuthPassword());
    }

    private String getVworAuthUsername() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vworUser = propertiesReader.getProperty("vwor.auth.user");
        if (vworUser == null) {
            logger.warn("vwor.auth.user is nil");
        }
        return vworUser;
    }

    private String getVworAuthPassword() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vworPassword = propertiesReader.getProperty("vwor.auth.password");
        if (vworPassword == null) {
            logger.warn("vwor.auth.password is nil");
        }
        return vworPassword;
    }

    /**
     * Method sending Requests to VWOR
     *
     * @param path URI from VworUtils.path()
     * @param type desired result type from JSON mapping
     * @param <T>  FIXME
     * @return Entities from the Vwor component
     * @throws IOException FIXME
     */
    public <T> T readResource(String path, TypeReference<T> type) throws IOException {
        final String json = readResource(path);
        try {
            return mapper.readValue(json, type);
        } catch (JsonParseException jpe) {
            return (T) json;
        }
    }

    /**
     * Method sending Requests to VWOR
     *
     * @param path URI from VworUtils.path()
     * @return String from the Vwor component
     */
    public String readResource(String path) {
        WebResource resource;
        try {
            resource = client.resource(path);
            return resource.get(String.class);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                resource = client.resource(path);
                return resource.get(String.class);
            } else {
                throw che;
            }
        }
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     */
    public String path(String... path) {
        StringBuilder r = new StringBuilder(getVworEndPoint() + BASE_RESOURCE);

        for (String p : path) {
            if (!p.startsWith("/")) {
                r.append("/");
            }
            r.append(p);
        }

        return r.toString();
    }
}
