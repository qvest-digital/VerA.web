/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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

    /** base path of all resource */
    public static final String BASE_RESOURCE = "/rest";

    /* Context data */
    	public static final String USERNAME = "USERNAME";
    /* ************ */

    /* RETURN TYPES */
	    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
	    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {};
	    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {};
	    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};
    /* ************ */

    /* RESPONSE MESSAGES */
	    private static final String RESPONSE_SUCCESS = "OK";
	    private static final String RESPONSE_ERROR_NOT_REGISTERED = "NOT_REGISTERED";
    /* ***************** */

    /** Jersey client */
    private Client client;

    /** Configuration */
    private Config config;

    /** Jackson Object Mapper */
    private ObjectMapper mapper = new ObjectMapper();

    /** Servlet context */
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
     * @param invitationstatus status of user
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

    		Integer userId = getUserData(username);

    		if (userId != null) {
    			updateGuest(eventId, userId, invitationstatus, notehost);
    		}

    		return StatusConverter.convertStatus(RESPONSE_SUCCESS);
    	}

    	return StatusConverter.convertStatus(RESPONSE_ERROR_NOT_REGISTERED);
    }

    /**
     * Update guest
     *
     * @param eventId int
     * @param userId int
     * @param invitationstatus String
     * @param notehost String
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

    private Integer getUserData(String username) throws IOException {
    	return readResource(path("person", "userdata", "lite", username), INTEGER);
    }

    /**
     * Getting the transporter to show data according to the layout
     *
     * @param eventId int
     * @param username String
     * @return EventTransporter
     * @throws IOException
     */
	private EventTransporter getEventData(int eventId, String username)
			throws IOException {

		Integer personId = getUserData(username);
        Guest guest = readResource(path("guest", eventId, personId), GUEST);
    	Event event = readResource(path("event", eventId), EVENT);
    	EventTransporter transporter = createEventTransporter(guest, event);

		return transporter;
	}

	/**
	 * Converting Event-Guest to EventTransporter to show data into the layout
	 *
	 * @param guest Guest
	 * @param event Event
	 * @return EventTransporter
	 */
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
