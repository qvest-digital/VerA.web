package org.evolvis.veraweb.onlinereg.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.evolvis.veraweb.onlinereg.Config;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class ResourceReader {

    private Client client;

    private ObjectMapper mapper;

    /** Configuration */
    private Config config;

    /**
     * Custom constructor.
     *
     * @param client The {@link com.sun.jersey.api.client.Client}
     * @param mapper The {@link com.fasterxml.jackson.databind.ObjectMapper}
     * @param config The {@link org.evolvis.veraweb.onlinereg.Config}
     */
    public ResourceReader(Client client, ObjectMapper mapper, Config config) {
        this.client = client;
        this.mapper = mapper;
        this.config = config;
    }


    /**
     * Constructs a pathParts from vera.web endpint, BASE_RESOURCE and given pathParts fragmensts.
     *
     * @param pathParts pathParts fragments
     * @param baseResource The base path to online registration app
     *
     * @return Complete path as string
     */
    public String constructPath(String baseResource, Object... pathParts) {
        String path = config.getVerawebEndpoint() + baseResource;
        for (Object p : pathParts) {
            path += "/" + p;
        }
        return path;
    }

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T>  Type of requested entity
     * @return requested resource
     * @throws java.io.IOException TODO
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
//                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                final String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }

        } catch (UniformInterfaceException uie) {
//            log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }
}
