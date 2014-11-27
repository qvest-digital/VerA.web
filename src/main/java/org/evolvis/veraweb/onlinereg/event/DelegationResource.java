package org.evolvis.veraweb.onlinereg.event;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import lombok.Getter;
import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.osiam.resources.scim.User;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Created by Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class DelegationResource {

	private Config config;
    private Client client;

    /**
     * Empty constructor
     */
    public DelegationResource() {
	}
    
    /**
     * Constructor using parameters - required in Main class
     * 
     * @param config
     * @param client
     */
    public DelegationResource(Config config, Client client) {
		super();
		this.config = config;
		this.client = client;
	}

	@GET
    @Path("/{uuid}")
    public List<Guest> showRegisterView(@PathParam("uuid") String uuid) throws IOException {
        return null;
    }

    @POST
    @Path("/{uuid}/registerdelegiert")
    public String registerDelegateForEvent(@PathParam("uuid") String uuid,@QueryParam("nachname") String nachname,@QueryParam("username") String username,
    		@QueryParam("vorname") String vorname) throws IOException {
    	
    	if (!username.matches("\\w+")) {
            return "INVALID_USERNAME";
        }
    	
    	OsiamClient osiamClient = config.getOsiam().getClient(client);
        String accessToken = osiamClient.getAccessToken("GET POST");
    	
    	User user = osiamClient.getUser(accessToken, username);
        if (user != null) {
            return "USER_EXISTS";
        }
    	
    	// TODO Store in tperson
    	insertIntoTPerson(nachname, username, vorname);
        
        
        return "OK";
    }


    @GET
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }
    
    /**
     * Includes a new person in the database - Table "tperson"
     * 
     * @param nachname String
     * @param username String
     * @param vorname String
     */
    private void insertIntoTPerson(String nachname, String username,
    		String vorname) {
    	WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
    	r = r.queryParam("username", username).queryParam("firstname", vorname).queryParam("lastname", nachname);
    	Person person = r.post(Person.class);
    }
}
