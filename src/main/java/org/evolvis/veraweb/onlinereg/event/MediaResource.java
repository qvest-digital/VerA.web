package org.evolvis.veraweb.onlinereg.event;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.utils.PressTransporter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import lombok.extern.java.Log;

/**
 * @author jnunez
 */
@Path("/media")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class MediaResource {


    private static final TypeReference<Boolean> BOOLEAN = new TypeReference<Boolean>() {};
    private static final TypeReference<BigInteger> BIG_INTEGER = new TypeReference<BigInteger>() {};
    private static final TypeReference<List<Person>> GUEST_LIST = new TypeReference<List<Person>>() {};
    private static final String INVITATION_TYPE = "2";
	
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

    
    public MediaResource() {
    }

	public MediaResource(Config config, Client client) {
		super();
		this.config = config;
		this.client = client;
	}
	
	@GET
    @Path("/{uuid}")
    public List<Person> getGuests(@PathParam("uuid") String uuid) throws IOException {
		return readResource(path("person", uuid), GUEST_LIST);
    }
	
    @POST
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(
            @PathParam("uuid") String uuid,
            @QueryParam("nachname") String nachname,
    		@QueryParam("vorname") String vorname,
            @QueryParam("gender") String gender,
            @QueryParam("email") String email,
    		@QueryParam("address") String address,
    		@QueryParam("plz") String plz,
    		@QueryParam("city") String city,
            @QueryParam("country") String country) throws IOException {

        Boolean delegationIsFound = checkForExistingPressEvent(uuid);

        if(delegationIsFound) {
        	PressTransporter transporter = new PressTransporter(uuid, nachname, vorname, gender, email, address, plz, city, country);
            return handlePressEvent(transporter);
        } else {
            return "WRONG_EVENT";
        }
        	
        
    }
	
    @POST
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }
    

    private String handlePressEvent(PressTransporter transporter) throws IOException {
        // Store in tperson
        Integer personId = createPerson(transporter);

        // Assing person to event as guest
        BigInteger eventId = getEventIdFromUuid(transporter.getUuid());

        if (eventId==null) {
            return "NO_EVENT_DATA";
        }
        addGuestToEvent(transporter.getUuid(), String.valueOf(eventId), String.valueOf(personId), transporter.getGender());

        return "OK";
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
        	 .queryParam("gender", gender);

        resource.post(Guest.class);
    }
    
    
    private BigInteger getEventIdFromUuid(String uuid) throws IOException {
		return readResource(path("event", "require", uuid), BIG_INTEGER);
	}
    
    /**
     * Includes a new person in the database - Table "tperson"
     * 
     * @param nachname Last name
     * @param vorname First name
     */
    private Integer createPerson(PressTransporter transporter) {
        WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/person/press/");
        
        resource = resource
            .queryParam("username", usernameGenerator())
            .queryParam("firstname", transporter.getVorname())
            .queryParam("lastname", transporter.getNachname())
	        .queryParam("gender", correctGender(transporter.getGender()))
	        .queryParam("email", transporter.getEmail())
	        .queryParam("address", transporter.getAddress())
	        .queryParam("plz", transporter.getPlz())
	        .queryParam("city", transporter.getCity())
	        .queryParam("country", transporter.getCountry());
        
        final Person person = resource.post(Person.class);

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
    	
    	return "press" + current.getTime();
    }
    
    private String correctGender(String gender) {
		String dbGender = null;    
		if (gender.equals("Herr")) {
			dbGender = "m";
		}
		else {
			dbGender = "w";
		}
		
		return dbGender;
	} 
    
}
