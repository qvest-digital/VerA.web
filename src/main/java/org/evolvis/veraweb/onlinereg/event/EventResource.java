package org.evolvis.veraweb.onlinereg.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by mley on 29.07.14.
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class EventResource {

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
    public EventResource(Config config, Client client) {
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
    @Path("/list")
    public List<Event> getEvents() throws IOException {
        return readResource(path("event"), EVENT_LIST);
    }

    /**
     * Returns an event with given id.
     *
     * @param eventId event id
     * @return Event object
     * @throws IOException
     */
    @GET
    @Path("/{eventId}")
    public Event getEvent(@PathParam("eventId") int eventId) throws IOException {
        return readResource(path("event", eventId), EVENT);
    }

    /**
     * Gets the current registration to an event of a user
     *
     * @param eventId event id
     * @param userId  user id
     * @return Guest object
     * @throws IOException
     */
    @GET
    @Path("/{eventId}/register/{userId}")
    public Guest getRegistration(@PathParam("eventId") int eventId, @PathParam("userId") int userId) throws IOException {
        return readResource(path("guest", eventId, userId), GUEST);
    }

    /**
     * Save the registration to an event
     *
     * @param eventId          event id
     * @param userId           user id
     * @param invitationstatus invitation status
     * @param notehost         note to host
     * @return updated Guest object
     * @throws IOException
     */
    @POST
    @Path("/{eventId}/register/{userId}")
    public Guest register(@PathParam("eventId") int eventId, @PathParam("userId") int userId, @QueryParam("invitationstatus") String invitationstatus, @QueryParam("notehost") String notehost) throws IOException {
        WebResource r = client.resource(path("guest", eventId, userId));
        String result = r.queryParam("invitationstatus", invitationstatus).queryParam("notehost", notehost).post(String.class);
        return mapper.readValue(result, GUEST);
    }

}
