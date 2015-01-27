package org.evolvis.veraweb.onlinereg.event;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Delegation;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Person;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import lombok.extern.java.Log;

/**
 * @author Max Marche <m.marche@tarent.de>, tarent solutions GmbH
 */
@Path("/freevisitors")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class FreeVisitorsResource {
    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";
    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();
    
    private Config config;
	private Client client;
    
    public FreeVisitorsResource(Config config, Client client) {
		this.config = config;
		this.client = client;
	}
    
    /**
     * Get delegates of company/institution.
     *
     * @param uuid The delegation UUID
     * @return 
     *
     * @return List with delegates
     * @throws IOException TODO
     */
	@GET
    @Path("/{uuid}")
    public int getEvenByUUId(@PathParam("uuid") String uuid) throws IOException {
    	WebResource resource = client.resource(path("freevisitors", uuid));
        return resource.get(Event.class).getPk();
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
