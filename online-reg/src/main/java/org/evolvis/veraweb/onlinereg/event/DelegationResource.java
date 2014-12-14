package org.evolvis.veraweb.onlinereg.event;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.OptionalFieldValue;
import org.evolvis.veraweb.onlinereg.entities.Person;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class DelegationResource {

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();
    private Config config;
    private Client client;

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
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
    private static final TypeReference<Integer> INTEGER = new TypeReference<Integer>() {};
    private static final TypeReference<List<Person>> GUEST_LIST = new TypeReference<List<Person>>() {};
    private static final TypeReference<List<OptionalFieldValue>> FIELDS_LIST = new TypeReference<List<OptionalFieldValue>>() {};

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

	@GET
    @Path("/{uuid}")
    public List<Person> getDelegates(@PathParam("uuid") String uuid) throws IOException {
		return readResource(path("person", uuid), GUEST_LIST);
    }


	@GET
    @Path("/{uuid}/{personId}/data")
    public List<OptionalFieldValue> getExtraDataFields(
    		@PathParam("uuid") String uuid,
    		@PathParam("personId") Integer personId) throws IOException {

		return getLabels(uuid, personId);
    }


    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(
            @PathParam("uuid") String uuid,
            @QueryParam("nachname") String nachname,
    		@QueryParam("vorname") String vorname,
            @QueryParam("gender") String gender) throws IOException {

        Boolean delegationIsFound = checkForExistingDelegation(uuid);

        if(delegationIsFound) {
            return handleDelegationFound(uuid, nachname, vorname, gender);
        } else {
            return "WRONG_DELEGATION";
        }
    }

    @POST
    @Path("/{uuid}/fields")
    public void saveOptionalFields(@PathParam("uuid") String uuid,
            @QueryParam("fields") String fields, @QueryParam("personId") Integer personId) throws IOException {
		if (fields != null || !"".equals(fields)) {
			Map<String, String> fieldMap = mapper.readValue(fields, new TypeReference<HashMap<String,String>>(){});

			Guest guest = getEventIdFromUuid(uuid, personId);

			for(Entry<String, String> entry : fieldMap.entrySet()){
				final int fieldId = Integer.parseInt(entry.getKey());
				final String fieldValue = entry.getValue();

				saveOptionalField(guest.getPk(), fieldId, fieldValue);
			}
		}
    }

    @POST
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }

	private List<OptionalFieldValue> getLabels(String uuid, Integer personId) throws IOException {
		try{
			Guest guest = getEventIdFromUuid(uuid, personId);
			List<OptionalFieldValue> fields = readResource(path("delegation", "fields", guest.getFk_event(), guest.getPk()), FIELDS_LIST);
			return fields;
		}
		catch (UniformInterfaceException uie) {
			return null;
		}
	}

    private Boolean checkForExistingDelegation(String uuid) throws IOException {
    	return readResource(path("guest","exist", uuid), BOOLEAN);
    }

    private String handleDelegationFound(String uuid, String nachname, String vorname, String gender) throws IOException {
        // Assing person to event as guest
        Guest guest = getEventIdFromUuid(uuid);

        // Store in tperson
        Integer personId = createPerson(guest.getFk_event(), nachname, vorname, gender);

        if (guest==null) {
            return "NO_EVENT_DATA";
        }
        addGuestToEvent(uuid, String.valueOf(guest.getFk_event()), String.valueOf(personId), gender);

        return "OK";
    }

    private Guest getEventIdFromUuid(String uuid) throws IOException {
		return readResource(path("guest", uuid), GUEST);
	}

    private Guest getEventIdFromUuid(String uuid, Integer personId) throws IOException {
		return readResource(path("guest", "delegation", uuid, personId), GUEST);
	}


    /**
     * Includes a new person in the database - Table "tperson"
     *
     * @param nachname Last name
     * @param vorname First name
     */
    private Integer createPerson(Integer eventId, String nachname, String vorname, String gender) {
        WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/delegate/");
        resource = resource
        		.queryParam("eventId", String.valueOf(eventId))
                .queryParam("username", usernameGenerator())
                .queryParam("firstname", vorname)
                .queryParam("lastname", nachname)
                .queryParam("gender", gender);
        final Person person = resource.post(Person.class);

    	return person.getPk();
    }

    /**
     * Includes a new guest in the database - Table "tguest"
     *
     * @param eventId Event id
     * @param userId User id
     */
    private void addGuestToEvent(String uuid, String eventId, String userId, String gender) {

    	WebResource resource = client.resource(path("guest", uuid, "register"));

        resource = resource.queryParam("eventId", eventId)
        	 .queryParam("userId", userId)
        	 .queryParam("invitationstatus", "0")
             .queryParam("invitationtype", INVITATION_TYPE)
        	 .queryParam("gender", gender)
        	 .queryParam("category", "0");

        resource.post(Guest.class);
    }

    /**
     * Persist a new optional fields value
     *
     * @param guestId guest pk
     * @param fieldId optional field pk
     * @param fieldValue value
     */
    private void saveOptionalField(Integer guestId, Integer fieldId, String fieldValue) {
    	WebResource resource = client.resource(path("delegation","field", "save"));

        resource = resource.queryParam("guestId", guestId.toString())
        	 .queryParam("fieldId", fieldId.toString())
        	 .queryParam("fieldValue", fieldValue);

        resource.post(Delegation.class);
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
        Date current = new Date();

    	return "deleg" + current.getTime();
    }
}