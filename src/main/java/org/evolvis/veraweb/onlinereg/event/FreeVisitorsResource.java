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

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
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
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
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
