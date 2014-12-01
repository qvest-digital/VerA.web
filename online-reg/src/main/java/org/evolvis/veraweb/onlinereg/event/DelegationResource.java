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
    	
    	// Store in tperson
    	Integer personId = insertIntoTPerson(nachname, vorname);
    	
    	// Assing person to event as guest
//    	Guest guest = getEventIdFromUuid(uuid);
//    	insertPersonIntoEvent(guest.getFk_event(), personId, "0", "");
    	
        return "OK";
    }

	private Guest getEventIdFromUuid(String uuid) throws IOException {
		return readResource(path("guest", uuid), GUEST);
	}
	@GET
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }

    /**
     * Includes a new person in the database - Table "tperson"
     * 
     * @param nachname Last name
     * @param vorname First name
     */
    private Integer insertIntoTPerson(String nachname, String vorname) {
        WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", "delegiert").queryParam("firstname", vorname).queryParam("lastname", nachname);
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
    private Guest insertPersonIntoEvent(int eventId, int userId, String invitationstatus, String notehost)
            throws IOException {
    	WebResource r = client.resource(path("guest", eventId, userId));
        String result = r.queryParam("invitationstatus", invitationstatus).queryParam("notehost", notehost).post(String.class);
        return mapper.readValue(result, GUEST);
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
    
}
