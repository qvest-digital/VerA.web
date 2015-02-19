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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import lombok.Getter;
import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.utils.EventTransporter;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
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

    public static final String USERNAME = "USERNAME";

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
     * Person type
     */
    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {
    };

    /**
     * Guest type
     */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {
    };

    /**
     * Guest type
     */
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {
    };

    /**
     * Guest type
     */
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {
    };

    private static final Integer INVITATIONSTATUS_OPEN = 0;
    private static final Integer INVITATIONSTATUS_COMMITMENT = 1;

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

    /**
     * Returns a list of events
     *
     * @return List of Event objects
     * @throws IOException
     */
    @GET
    @Path("/list/{username}")
    public List<EventTransporter> getEvents(@PathParam("username") String username) throws IOException {
    	List<Event> listEvents = readResource(path("event"), EVENT_LIST);
    	
    	List<EventTransporter> listTransporters = new ArrayList<EventTransporter>();
    	
    	for (Iterator<Event> iterator = listEvents.iterator(); iterator.hasNext();) {
            EventTransporter eventTransporter = createEventTransporter(username, iterator);
			listTransporters.add(eventTransporter);
		}
    	
        return listTransporters;
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
    public Guest getRegistration(
    		@PathParam("eventId") int eventId, 
    		@PathParam("userId") int userId) throws IOException {
        return readResource(path("guest", eventId, userId), GUEST);
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
    @Path("/{eventId}/register")
    public String register(
    		@PathParam("eventId") String eventId,
    		@FormParam("notehost") String notehost) throws IOException {
        String username = (String) context.getAttribute(USERNAME);
    	
    	// checking if the user is registered on the event
    	if (!isUserRegistered(username, eventId)) {
    		
    		Person person = getUserData(username);
    		Integer userId = person.getPk();
    		
    		if (person != null && userId != null) {
    			addGuestToEvent(eventId, userId.toString(), 
    					person.getSex_a_e1(), person.getFirstname_a_e1(), 
    					person.getLastname_a_e1(), username, notehost);
    		}
    		
    		return StatusConverter.convertStatus("OK");
    	}
    	
    	return StatusConverter.convertStatus("REGISTERED");
    }

    
	/**
     * Get user's subscribed events.
     *
     * @param username  username
     * @return Users' events list
     * @throws IOException
     */
    @GET
    @Path("/userevents/{username}")
    public List<Event> getUsersEvents(@PathParam("username") String username) throws IOException {
        return readResource(path("event", "userevents", username), EVENT_LIST);
    }

    private EventTransporter createEventTransporter(String username, Iterator<Event> iterator) throws IOException {
        Event event = (Event) iterator.next();
        String eventId = String.valueOf(event.getPk());
        return new EventTransporter(
                event.getPk(),
                event.getShortname(),
                event.getDatebegin(),
                isUserRegistered(username, eventId));
    }

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
    
    
    /**
     * Includes a new guest in the database - Table "tguest".
     *
     * @param eventId Event id
     * @param userId User id
     * @param gender Gender of the person
     * @throws IOException 
     */
    private Guest addGuestToEvent(String eventId, String userId, String gender, String lastName, String firstName, String username, String nodeHost) throws IOException {
		WebResource resource = client.resource(path("guest", "register"));
		Form postBody = new Form();

		postBody.add("eventId", eventId);
        postBody.add("userId", userId);
        postBody.add("invitationstatus", generateInvitationStatus(eventId));
        postBody.add("invitationtype", "2");
        postBody.add("gender", gender);
        postBody.add("category", "0");
        postBody.add("username", username);
        postBody.add("hostNode", nodeHost);

        final Guest guest = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Guest.class, postBody);
        
        updateGuestMessage(nodeHost, userId);
        createGuestDoctype(guest.getPk(), firstName, lastName);
        
        return guest;
	}
	
    private String generateInvitationStatus(String eventId) throws IOException {
    	Boolean isOpen = readResource(path("event", "isopen", eventId), BOOLEAN);
    	
    	if (isOpen) {
    		return INVITATIONSTATUS_COMMITMENT.toString();
    	}
    	
    	return INVITATIONSTATUS_OPEN.toString();
    }
    
    
	private void createGuestDoctype(int guestId, String firstName, String lastName) {
        
        WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/guestDoctype"); 
		
		Form postBody = new Form();

		postBody.add("guestId", Integer.toString(guestId));
		postBody.add("firstName", firstName);
		postBody.add("lastName", lastName);
		resource.post(postBody);
	}
	
	private void updateGuestMessage(String notehost, String personId) {
		WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/guestmsg");
		
		Form postBody = new Form();
		postBody.add("notehost", notehost);
		postBody.add("personId", personId);
		
		resource.post(postBody);
	}
}
