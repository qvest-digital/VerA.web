package org.evolvis.veraweb.onlinereg.event;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.utils.EventTransporter;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import lombok.Getter;
import lombok.extern.java.Log;

/**
 * New functions according to the page where the user can change his status and message to an event
 *
 * @author jnunez
 *
 */

@Path("/update")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class UpdateResource {

    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    public static final String USERNAME = "USERNAME";

    /* RETURN TYPES BEGIN*/

    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {};
    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {};
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};

    /* RETURN TYPES END */

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
     * Servlet context
     */
    @javax.ws.rs.core.Context
    @Getter
    private ServletContext context;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public UpdateResource(Config config, Client client) {
        this.client = client;
        this.config = config;
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
    public EventTransporter getEvent(@PathParam("eventId") int eventId) throws IOException {
    	String username = (String) context.getAttribute(USERNAME);

    	EventTransporter transporter = getEventData(eventId, username);

        return transporter;
    }

    /**
     * Save the registration to an event
     *
     * @param eventId          event id
     * @param notehost         note to host
     * @return updated Guest object
     * @throws IOException
     */
    @POST
    @Path("/{eventId}/update")
    public String update(
    		@PathParam("eventId") String eventId,
    		@FormParam("notehost") String notehost,
    		@FormParam("invitationstatus") String invitationstatus) throws IOException {
        String username = (String) context.getAttribute(USERNAME);

    	// checking if the user is registered on the event
    	if (isUserRegistered(username, eventId)) {

    		Person person = getUserData(username);
    		Integer userId = person.getPk();

    		if (person != null && userId != null) {
    			updateGuest(eventId, userId, invitationstatus, notehost);
    		}

    		return StatusConverter.convertStatus("OK");
    	}

    	return StatusConverter.convertStatus("REGISTERED");
    }

    /**
     * Update guest
     *
     * @param eventId int
     * @param userId int
     */
    private void updateGuest(String eventId, int userId, String invitationstatus, String notehost) {
    	WebResource resource = client.resource(path("guest", "update", eventId, userId));
		Form postBody = new Form();

        postBody.add("invitationstatus", invitationstatus);
        postBody.add("notehost", notehost);

        resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(postBody);
    }

    /**
     * Checking if the user is registered
     *
     * @param username String
     * @param eventId String
     * @return Boolean
     * @throws IOException the exception
     */
    private Boolean isUserRegistered(String username, String eventId) throws IOException {
    	return readResource(path("guest", "registered", username, eventId), BOOLEAN);
    }

    /**
     * Get Person instance from one username
     * @param username
     * @return
     * @throws IOException
     */
    private Person getUserData(String username) throws IOException {
    	return readResource(path("person", "userdata", username), PERSON);
    }


	private EventTransporter getEventData(int eventId, String username)
			throws IOException {

		// TODO IMPROVE with smaller Objects (getting concrete data) REST - API
		Person person = getUserData(username);
        Guest guest = readResource(path("guest", eventId, person.getPk()), GUEST);
    	Event event = readResource(path("event", eventId), EVENT);
    	EventTransporter transporter = createEventTransporter(guest, event);

		return transporter;
	}


	private EventTransporter createEventTransporter(Guest guest, Event event) {
		EventTransporter transporter = new EventTransporter();

    	transporter.setDatebegin(event.getDatebegin());
    	transporter.setMessage(guest.getNotehost());
    	transporter.setShortname(event.getShortname());
    	transporter.setStatus(guest.getInvitationstatus());
		return transporter;
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
//                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                String json = resource.get(String.class);
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
