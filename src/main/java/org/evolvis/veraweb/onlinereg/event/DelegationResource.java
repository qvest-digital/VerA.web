package org.evolvis.veraweb.onlinereg.event;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

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
    private ObjectMapper mapper = new ObjectMapper();
    private Config config;
    private Client client;
    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";
    /**
     * Guest type
     */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};
    
    /**
     * Guest type
     */
    private static final TypeReference<List<Guest>> GUEST_LIST = new TypeReference<List<Guest>>() {};
    
    /**
     * Guest type
     */
    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};

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
    public List<Guest> showRegisterView(@PathParam("uuid") String uuid) throws IOException {
        return null;
    }

    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(@PathParam("uuid") String uuid,@QueryParam("nachname") String nachname,
    		@QueryParam("vorname") String vorname) throws IOException {

        Boolean delegationIsFound = checkForExistingDelegation(uuid);

        if(delegationIsFound) {
            return handleDelegationFound(uuid, nachname, vorname);
        } else {
            return "WRONG_DELEGATION";
        }
    }

    @GET
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }
    
    private Boolean checkForExistingDelegation(String uuid) throws IOException {
    	return readResource(path("guest","exist", uuid), BOOLEAN);
    }

    private String handleDelegationFound(String uuid, String nachname, String vorname) throws IOException {
        // Store in tperson
        Integer personId = createPerson(nachname, vorname);

        // Assing person to event as guest
        Guest guest = getEventIdFromUuid(uuid);

        if (guest==null) {
            return "NO_EVENT_DATA";
        }
        addGuestToEvent(uuid, String.valueOf(guest.getFk_event()), String.valueOf(personId), "0", "");

        return "OK";
    }

    private Guest getEventIdFromUuid(String uuid) throws IOException {
		return readResource(path("guest", uuid), GUEST);
	}
	

    /**
     * Includes a new person in the database - Table "tperson"
     * 
     * @param nachname Last name
     * @param vorname First name
     */
    private Integer createPerson(String nachname, String vorname) {
        WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", usernameGenerator()).queryParam("firstname", vorname).queryParam("lastname", nachname);
        Person person = r.post(Person.class);

    	return person.getPk();
    } 
    
    /**
     * Includes a new guest in the database - Table "tguest"
     * 
     * @param eventId Event id
     * @param userId User id
     * @param invitationstatus //TODO
     * @param notehost //TODO
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    private Guest addGuestToEvent(String uuid, String eventId, String userId, String invitationstatus, String notehost)
            throws IOException {
    	WebResource r = client.resource(path("guest", uuid, "einladen"));
        r = r.queryParam("eventId", eventId)
        	 .queryParam("userId", userId)
        	 .queryParam("invitationstatus", invitationstatus);
        Guest guest = r.post(Guest.class);
        		
        return guest;
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
    	StringBuilder sb = new StringBuilder();
    	sb.append("deleg");
    	Date current = new Date();
    	sb.append(current.getTime());
    	
    	return sb.toString();
    }
    
}
