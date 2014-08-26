package org.evolvis.veraweb.onlinereg.event;

import java.net.SocketTimeoutException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

@Path( "user" )
@Produces( MediaType.APPLICATION_JSON )
@Log
public class UserResource {
	
    public static final String EVENT_RESOURCE = "/rest";
	private Client client;
	private Config config;
	
	public UserResource(Client client, Config config){
		this.client = client;
		this.config = config;
	}
	
    private String path(Object... path) {
        String r = config.getVerawebEndpoint() + EVENT_RESOURCE;
        if (path != null) {
            for (Object p : path) {
                if (p != null) {
                    r += "/" + p;
                }
            }
        }
        return r;
    }	
    
    private String readResource(String path) {
        WebResource resource;
        try {
            resource = client.resource(path);
            return resource.get(String.class);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                // some times open, pooled connections time out and generate errors
                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                return resource.get(String.class);
            } else {
                throw che;
            }

        }
    }
    
  @GET
  @Path("/{osiam_username}/")
  public String getUser(@PathParam("osiam_username") String osiam_username) {
      return readResource(path("user", osiam_username));
}
  
  @POST
  @Path("/{osiam_username}")
  public String registerUser(@PathParam("osiam_username") String osiam_username, 
		  					 @QueryParam("osiam_firstname") String osiam_firstname,
                             @QueryParam("osiam_secondname") String osiam_secondname,
                             @QueryParam("osiam_password1") String osiam_password1) {
      if(osiam_username == "existinguser") {
          return "USER_EXISTS";
      }

      return "OK";
  }
  
 /* @POST
  @Path("/{osiam_username}/")
  public String registerUser(@PathParam("osiam_username") String osiam_username, @QueryParam("osiam_firstname") String osiam_firstname, @QueryParam("osiam_secondname") String osiam_secondname, @QueryParam("osiam_password1") String osiam_password1) {
      WebResource r = client.resource(path("user", osiam_username));
      String result = r.queryParam("osiam_firstname", osiam_firstname).queryParam("osiam_secondname", osiam_secondname).queryParam("osiam_password1", osiam_password1).post(String.class);
      return result;
  }*/
}