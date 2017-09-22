package org.evolvis.veraweb.onlinereg.event;

/*-
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.entities.Event;
import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/veranstaltungen")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class VeranstaltungenResource {

	 /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    /**
     * Event type
     */
    private static final TypeReference<Event> EVENT = new TypeReference<Event>() {};

    /**
     * List of Events type
     */
    private static final TypeReference<List<Event>> EVENT_LIST = new TypeReference<List<Event>>() {};

    /**
     * Guest type
     */
    private static final TypeReference<Guest> GUEST = new TypeReference<Guest>() {};

    /**
     * Jersey client
     */
    private Client client;

    /**
     * configuration
     */
    private Config config;

    /**
     * Jackson Object Mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    private final ResourceReader resourceReader;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public VeranstaltungenResource(Config config, Client client) {
        this.client = client;
        this.config = config;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    

    /**
     * Returns a list of events
     *
     * @return List of Event objects
     * @throws IOException FIXME
     */
    @GET
    @Path("/dum")
    public List<Event> getEvents() throws IOException {
        return readResource(path("veranstaltungen"), EVENT_LIST);
    }
    
    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        return resourceReader.readStringResource(path, type);
    }
}
