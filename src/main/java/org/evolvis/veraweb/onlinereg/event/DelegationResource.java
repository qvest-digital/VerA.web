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

import org.apache.commons.lang.StringEscapeUtils;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonDoctype;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class DelegationResource {

    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";

    /**
     * Invitation type:
     *  1 - main person and partner are invited
     *  2 - only main person is invited
     *  3 - only partner is invited
     */
    private static final String INVITATION_TYPE = "2";
    
    /* RETURN TYPES */
	    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};
	    private static final TypeReference<Person> PERSON = new TypeReference<Person>() {};
	    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
	    private static final TypeReference<List<Person>> GUEST_LIST = new TypeReference<List<Person>>() {};
	    private static final TypeReference<List<OptionalFieldValue>> FIELDS_LIST =
	            										new TypeReference<List<OptionalFieldValue>>() {};
    /* ************ */

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Configuration
     */
    private Config config;
    
    /**
     * Jersey client
     */
    private Client client;

	/**
	 * Default constructor
	 */
	public DelegationResource() {
	}

	/**
	 * Constructor
	 *
	 * @param config Config
	 * @param client Client
	 */
    public DelegationResource(Config config, Client client) {
		this.config = config;
		this.client = client;
	}

    /**
     * Get delegates of company/institution.
     *
     * @param uuid The delegation UUID
     *
     * @return List with delegates
     * @throws IOException TODO
     */
	@GET
    @Path("/{uuid}")
    public List<Person> getDelegates(@PathParam("uuid") String uuid) throws IOException {
		return readResource(path("person", uuid), GUEST_LIST);
    }

    /**
     * Get optional fields.
     *
     * @param uuid The delegation UUID
     * @param personId The person id
     *
     * @return List with optional fields for delegates
     * @throws IOException TODO
     */
	@GET
    @Path("/{uuid}/{personId}/data")
    public List<OptionalFieldValue> getExtraDataFields(
    		@PathParam("uuid") String uuid,
    		@PathParam("personId") Integer personId) throws IOException {

		return getLabels(uuid, personId);
    }

    /**
     * Register delegate for event.
     *
     * @param uuid The delegation UUID of the company.
     *
     * @param lastname The last name of the delegare
     * @param firstname The first name of the delegare
     * @param gender The gender of the delegare
     *
     * @return Status message
     *
     * @throws IOException TODO
     */
    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(
            @PathParam("uuid") String uuid,
            @FormParam("lastname") String lastname,
            @FormParam("firstname") String firstname,
            @FormParam("gender") String gender) throws IOException {

        final Boolean delegationIsFound = checkForExistingDelegation(uuid);

        if(delegationIsFound) {
        	String returnedValue = handleDelegationFound(uuid, lastname, firstname, gender);
            return StatusConverter.convertStatus(returnedValue);
        } else {
            return StatusConverter.convertStatus("WRONG_DELEGATION");
        }
    }

    /**
     * Save optional fields.
     *
     * @param uuid The delegation UUID for the company.
     *
     * @param fields The optional fields
     * @param personId The delegate id
     *
     * @throws IOException TODO
     */
    @POST
    @Path("/{uuid}/fields/save")
    public void saveOptionalFields(
    		@PathParam("uuid") String uuid,
            @FormParam("fields") String fields, 
            @FormParam("personId") Integer personId) throws IOException {
		if (fields != null && !"".equals(fields)) {
            handleSaveOptionalFields(uuid, fields, personId);
		}
    }

    /**
     * Remove delegate from guest list for event.
     *
     * @param uuid The delegation UUID
     * @param userid The user id
     *
     * @return TODO
     */
    @POST
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) {
        return null;
    }

    private void handleSaveOptionalFields(String uuid, String fields, Integer personId) throws IOException {
        final TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {
        };
        final Map<String, String> fieldMap = mapper.readValue(fields, typeReference);

        final Guest guest = getEventIdFromUuid(uuid, personId);

        for(Entry<String, String> entry : fieldMap.entrySet()){
            final int fieldId = Integer.parseInt(entry.getKey());
            final String fieldValue = entry.getValue();

            saveOptionalField(guest.getPk(), fieldId, fieldValue);
        }
    }

	private List<OptionalFieldValue> getLabels(String uuid, Integer personId) throws IOException {
		try{
			final Guest guest = getEventIdFromUuid(uuid, personId);
			final List<OptionalFieldValue> fields =
                    readResource(path("delegation", "fields", "list", guest.getFk_event(), guest.getPk()), FIELDS_LIST);

            final List<OptionalFieldValue> filteredList = new ArrayList<OptionalFieldValue>();
            for (OptionalFieldValue field : fields) {
                if (field.getLabel() != null && !field.getLabel().equals("")) {
                    filteredList.add(field);
                }
            }
            return filteredList;
		}
		catch (UniformInterfaceException uie) {
			return null;
		}
	}

    private Boolean checkForExistingDelegation(String uuid) throws IOException {
    	return readResource(path("guest","exist", uuid), BOOLEAN);
    }

    private String handleDelegationFound(String uuid, String nachname, String vorname, String gender)
            throws IOException {
    	
        final Integer eventId = getEventId(uuid);
        final Person company = getCompanyFromUuid(uuid);
        
        String username = usernameGenerator();
        // Store in tperson
        final Integer personId = createPerson(company.getCompany_a_e1(), eventId, nachname, vorname, gender,username);

        if (eventId == null) {
            return "NO_EVENT_DATA";
        }
        addGuestToEvent(uuid, String.valueOf(eventId), String.valueOf(personId), gender, nachname, vorname, username);

        return "OK";
    }

    private Integer getEventId(String uuid) throws IOException {
        final Guest guest = readResource(path("guest", uuid), GUEST);
        return guest.getFk_event();
	}

    private Guest getEventIdFromUuid(String uuid, Integer personId) throws IOException {
		return readResource(path("guest", "delegation", uuid, personId), GUEST);
	}
    
    private Person getCompanyFromUuid(String uuid) throws IOException {
		return readResource(path("person", "company", uuid), PERSON);
	}

    /**
     * Includes a new person in the database - Table "tperson"
     * @param companyName 
     *
     * @param lastname Last name
     * @param firstname First name
     * @param gender Gender of the person
     */
    private Integer createPerson(String companyName, Integer eventId, String firstname, String lastname, String gender, String username) {
        WebResource personResource = client.resource(config.getVerawebEndpoint() + "/rest/person/delegate");
        Form postBody = new Form();
        
        postBody.add("company", companyName);
        postBody.add("eventId", String.valueOf(eventId));
        postBody.add("username", username);
        postBody.add("firstname", firstname);
        postBody.add("lastname", lastname);
        postBody.add("gender", gender);
        
        final Person person = personResource.post(Person.class, postBody);
        createPersonDoctype(person);
        
    	return person.getPk();
    }
    
    private void createPersonDoctype(Person person) {
        WebResource personDoctypeRsource = client.resource(config.getVerawebEndpoint() + "/rest/personDoctype");
        Form postBody = new Form();

		postBody.add("personId", Integer.toString(person.getPk()));
		postBody.add("firstName", person.getFirstname_a_e1());
        postBody.add("lastName", person.getLastname_a_e1());

        personDoctypeRsource.post(PersonDoctype.class, postBody);
    }

    /**
     * Includes a new guest in the database - Table "tguest".
     *
     * @param eventId Event id
     * @param userId User id
     * @param gender Gender of the person
     */
    private void addGuestToEvent(String uuid, String eventId, String userId, String gender, String lastName, String firstName, String username) {
		WebResource resource = client.resource(path("guest", uuid, "register"));
        Form postBody = new Form();

        postBody.add("userId", userId);
        postBody.add("eventId", eventId);
		postBody.add("invitationstatus", "0");
		postBody.add("invitationtype", INVITATION_TYPE);
		postBody.add("gender", gender);
		postBody.add("category", "0");
		postBody.add("username", username);

        final Guest guest = resource.post(Guest.class, postBody);
        
        createGuestDoctype(guest.getPk(), firstName, lastName);
	}
	
	private void createGuestDoctype(int guestId, String firstName, String lastName) {
		WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/guestDoctype"); 
		Form postBody = new Form();

		postBody.add("guestId", Integer.toString(guestId));
		postBody.add("firstName", firstName);
		postBody.add("lastName", lastName);

        resource.post(postBody);
	}

    /**
     * Persist a new optional fields value.
     *
     * @param guestId guest pk
     * @param fieldId optional field pk
     */
    private void saveOptionalField(Integer guestId, Integer fieldId, String fieldContent) {
    	WebResource resource = client.resource(path("delegation","field", "save"));
    	Form postBody = new Form();

    	fieldContent = StringEscapeUtils.escapeHtml(fieldContent);
    	postBody.add("guestId", guestId.toString());
    	postBody.add("fieldId", fieldId.toString());
    	postBody.add("fieldContent", fieldContent);

        resource.post(Delegation.class, postBody);
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

    private String usernameGenerator() {
        final Date currentDate = new Date();

    	return "deleg" + currentDate.getTime();
    }
}
