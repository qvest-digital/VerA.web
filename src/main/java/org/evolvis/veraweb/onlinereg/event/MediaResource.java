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
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.utils.PressTransporter;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

/**
 * @author jnunez
 */
@Path("/media")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class MediaResource {
    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";

    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {};
    private static final TypeReference<List<Person>> GUEST_LIST = new TypeReference<List<Person>>() {};
    private static final String INVITATION_TYPE = "2";

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();
    private Config config;
    private Client client;

    /**
     * Default constructor.
     */
    public MediaResource() {
    }

    /**
     * Alternative constructor.
     *
     * @param config The configuration
     * @param client Client
     */
	public MediaResource(Config config, Client client) {
		super();
		this.config = config;
		this.client = client;
	}

	@GET
    @Path("/{uuid}")
    public List<Person> getGuests(@PathParam("uuid") String uuid) throws IOException {
		return null;
    }

    /**
     * Adds delegate to event
     *
     * @param uuid     Event uuid
     * @param nachname family name
     * @param vorname  first name
     * @param gender   gender
     * @param email    E-Mail Address
     * @param address  address
     * @param plz      zip code
     * @param city     city
     * @param country  country
     *
     * @throws java.io.IOException If creating person and saving it as guest fails.
     *
     * @return result of delegation. Values can be "WRONG_EVENT"
     */
    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(
            @PathParam("uuid") String uuid,
            @FormParam("nachname") String nachname,
            @FormParam("vorname") String vorname,
            @FormParam("gender") String gender,
            @FormParam("email") String email,
            @FormParam("address") String address,
            @FormParam("plz") String plz,
            @FormParam("city") String city,
            @FormParam("country") String country) throws IOException {

        final Boolean delegationIsFound = checkForExistingPressEvent(uuid);
        
        if(delegationIsFound) {
        	final PressTransporter transporter = new PressTransporter(uuid, nachname, vorname, gender, email, address, plz, city, country, usernameGenerator());
            return StatusConverter.convertStatus(createAndAssignMediaRepresentativeGuest(transporter));
        }

        return StatusConverter.convertStatus("WRONG_EVENT");
    }

    private String createAndAssignMediaRepresentativeGuest(PressTransporter transporter) throws IOException {
    	// Assing person to event as guest
        final Integer eventId = getEventIdFromUuid(transporter.getUuid());
    	// Store in tperson
        final Integer personId = createPerson(eventId, transporter);

        if (eventId==null) {
            return "NO_EVENT_DATA";
        }
        addGuestToEvent(transporter.getUuid(), String.valueOf(eventId), String.valueOf(personId), transporter.getGender(), transporter.getUsername());

        return "OK";
    }



    /**
     * Includes a new guest in the database - Table "tguest"
     *
     * @param uuid
     * @param eventId Event id
     * @param userId User id
     * @param gender gender
     * @throws IOException
     */
    private void addGuestToEvent(String uuid, String eventId, String userId, String gender, String username) throws IOException {
        final Integer categoryID = getCategoryIdFromCatname("Pressevertreter", uuid);
        final WebResource resource = client.resource(path("guest", uuid, "register"));
        final Form postBody = new Form();

        postBody.add("eventId", eventId);
        postBody.add("userId", userId);
        postBody.add("invitationstatus", "0");
        postBody.add("invitationtype", INVITATION_TYPE);
        postBody.add("gender", gender);
        postBody.add("category", String.valueOf(categoryID));
        postBody.add("username", username);

        resource.post(Guest.class, postBody);
    }

    /**
     * Searching an event ID using the UUID
     */
    private Integer getEventIdFromUuid(String uuid) throws IOException {
		return readResource(path("event", "require", uuid), INTEGER);
	}

    /**
     * Searching the ID of one category using the catname
     */
    private Integer getCategoryIdFromCatname(String catname, String uuid) throws IOException {
    	return readResource(path("category", catname, uuid), INTEGER);
    }
    /**
     * Includes a new person in the database - Table "tperson"
     *
     * @param eventId   event ID
     * @param transporter transporter
     *
     * return primary key for a person
     */
    private Integer createPerson(Integer eventId, PressTransporter transporter) {
        final WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/press/");
        final Form postBody = new Form();

        postBody.add("eventId", String.valueOf(eventId));
        postBody.add("username", transporter.getUsername());
        postBody.add("firstname", transporter.getVorname());
        postBody.add("lastname", transporter.getNachname());
	    postBody.add("gender", correctGender(transporter.getGender()));
	    postBody.add("email", transporter.getEmail());
	    postBody.add("address", transporter.getAddress());
	    postBody.add("plz", transporter.getPlz());
	    postBody.add("city", transporter.getCity());
	    postBody.add("country", transporter.getCountry());

        final Person person = resource.post(Person.class, postBody);

    	return person.getPk();
    }



    private Boolean checkForExistingPressEvent(String uuid) throws IOException {
    	return readResource(path("event","exist", uuid), BOOLEAN);
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

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
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

    private String usernameGenerator() {
        final Date current = new Date();

    	return "press" + current.getTime();
    }

    private String correctGender(String gender) {
		String dbGender = null;
		if (gender.equals("Herr")) {
			dbGender = "m";
		}
		else {
			dbGender = "f";
		}

		return dbGender;
	}

}
