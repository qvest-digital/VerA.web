package org.evolvis.veraweb.onlinereg.event;

import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.osiam.OsiamClient;
import org.hibernate.Query;
import org.hibernate.Session;
import org.osiam.resources.scim.User;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

/**
 * Created by Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/delegation")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class DelegationResource {

	private Config config;
    private Client client;
	
    @GET
    @Path("/{uuid}")
    public List<Guest> showRegisterView(@PathParam("uuid") String uuid) throws IOException {
        return null;
    }

    
    @Path("/{uuid}/register")
    public String registerDelegateForEvent(@PathParam("uuid") String uuid,@FormParam("username") String username,
    		@FormParam("vorname") String vorname,
    		@FormParam("nachname") String nachname) throws IOException {
    	
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
    	WebResource r = client.resource(config.getVerawebEndpoint() + "/rest/person/");
        r = r.queryParam("username", username).queryParam("firstname", vorname).queryParam("lastname", nachname);
        Person person = r.post(Person.class);
        
        
        return "OK";
    }

    @GET
    @Path("/{uuid}/remove/{userid}")
    public List<Guest> removeDelegateFromEvent(@PathParam("uuid") String uuid, @PathParam("userid") Long userid) throws IOException {
        return null;
    }
}
