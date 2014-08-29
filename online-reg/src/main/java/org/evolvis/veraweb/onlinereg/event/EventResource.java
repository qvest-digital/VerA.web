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

    public static final String EVENT_RESOURCE = "/rest";

    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {
    };
    private static final TypeReference<List<Event>> EVENT_LIST = new TypeReference<List<Event>>() {
    };
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {
    };


    private Client client;
    private Config config;
    private ObjectMapper mapper = new ObjectMapper();

    public EventResource(Client client, Config config) {
        this.client = client;
        this.config = config;
    }

    private String path(Object... path) {
        String r = config.getVerawebEndpoint() + EVENT_RESOURCE;
        if (path != null) {
            for (Object p : path) {
                if (p != null) {
                    r += "/" + p;
                }
            }
        }
        return r;
    }

    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                // some times open, pooled connections time out and generate errors
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

    @GET
    @Path("/list")
    public List<Event> getEvents() throws IOException {
        return readResource(path("event"), EVENT_LIST);
    }

    @GET
    @Path("/{eventId}")
    public Event getEvent(@PathParam("eventId") int eventId) throws IOException {
        return readResource(path("event", eventId), EVENT);
    }

    @GET
    @Path("/{eventId}/register/{userId}")
    public Guest getRegistration(@PathParam("eventId") int eventId, @PathParam("userId") int userId) throws IOException {
        return readResource(path("guest", eventId, userId), GUEST);
    }

    @POST
    @Path("/{eventId}/register/{userId}")
    public Guest register(@PathParam("eventId") int eventId, @PathParam("userId") int userId, @QueryParam("invitationstatus") String invitationstatus, @QueryParam("notehost") String notehost) throws IOException {
        WebResource r = client.resource(path("guest", eventId, userId));
        String result = r.queryParam("invitationstatus", invitationstatus).queryParam("notehost", notehost).post(String.class);
        return mapper.readValue(result, GUEST);
    }

}
