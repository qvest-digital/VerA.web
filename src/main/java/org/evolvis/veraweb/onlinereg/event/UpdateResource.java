package org.evolvis.veraweb.onlinereg.event;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;

import com.sun.jersey.api.client.Client;

import lombok.Getter;
import lombok.extern.java.Log;

/**
 * New functions according to the page where the user can change his status and message to an event
 * 
 * @author jnunez
 *
 */

@Path("/update")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class UpdateResource {

    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";
    
    /**
     * Jersey client
     */
    private Client client;

    /**
     * configuration
     */
    private Config config;
    

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
    public UpdateResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }
}
