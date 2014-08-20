package org.evolvis.veraweb.onlinereg.event;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.evolvis.veraweb.onlinereg.Config;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * Created by mley on 29.07.14.
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class EventResource {

    public static final String EVENT_RESOURCE = "/rest";

    private Client client;
    private Config config;

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

    private String readResource(String path) {
        WebResource resource;
        try {
            resource = client.resource(path);
            return resource.get(String.class);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                // some times open, pooled connections time out and generate errors
                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                return resource.get(String.class);
            } else {
                throw che;
            }

        }
    }

    @GET
    @Path("/list")
    public String getEvents() {
        return readResource(path("event"));
    }

    @GET
    @Path("/{eventId}")
    public String getEvent(@PathParam("eventId") int eventId) {
        return readResource(path("event", eventId));
    }

    @GET
    @Path("/{eventId}/register/{userId}")
    public String getRegistration(@PathParam("eventId") int eventId, @PathParam("userId") int userId) {
        return readResource(path("guest", eventId, userId));
    }

    @POST
    @Path("/{eventId}/register/{userId}")
    public String register(@PathParam("eventId") int eventId, @PathParam("userId") int userId, @QueryParam("invitationstatus") String invitationstatus, @QueryParam("notehost") String notehost) {
        WebResource r = client.resource(path("guest", eventId, userId));
        String result = r.queryParam("invitationstatus", invitationstatus).queryParam("notehost", notehost).post(String.class);
        return result;
    }

}
