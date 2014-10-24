package org.evolvis.veraweb.onlinereg.event;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import lombok.extern.java.Log;

@Path("/veranstaltungen")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class VeranstaltungenResource {
	 /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    /**
     * Event type
     */
    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {
    };
    /**
     * List of Events type
     */
    private static final TypeReference<List<Event>> EVENT_LIST = new TypeReference<List<Event>>() {
    };
    /**
     * Guest type
     */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {
    };

    /**
     * Jersey client
     */
    private Client client;

    /**
     * configuration
     */
    private Config config;

    /**
     * Jackson Object Mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public VeranstaltungenResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }

    /**
     * Constructs a path from vera.web endpint, BASE_RESOURCE and given path fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     */
    private String path(Object... path) {
        String r = config.getVerawebEndpoint() + BASE_RESOURCE;
        for (Object p : path) {
            r += "/" + p;
        }
        return r;
    }

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T>  Type of requested entity
     * @return requested resource
     * @throws IOException
     */
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }

        } catch (UniformInterfaceException uie) {
            log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }

    /**
     * Returns a list of events
     *
     * @return List of Event objects
     * @throws IOException
     */
    @GET
    @Path("/dum")
    public List<Event> getEvents() throws IOException {
        return readResource(path("veranstaltungen"), EVENT_LIST);
    }
}
