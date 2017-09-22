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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * @author Max Marche, m.marche@tarent.de, tarent solutions GmbH
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
    
    private final Config config;
	private final Client client;
    private final ResourceReader resourceReader;
    
    public FreeVisitorsResource(Config config, Client client) {
		this.config = config;
		this.client = client;
		this.resourceReader = new ResourceReader(client, mapper, config);
	}

    /**
     * Get
     *
     * @param noLoginRequiredUUID The free visitors UUID
     * @return Event id
     * @throws IOException TODO
     */
    @GET
    @Path("/{noLoginRequiredUUID}")
    public String getEvenByUUId(@PathParam("noLoginRequiredUUID") String noLoginRequiredUUID) throws IOException {
        final Integer guestId = checkGuestExistsByNoLoginRequiredUUID(noLoginRequiredUUID);

        if (guestId == null) {
            return StatusConverter.convertStatus("ERROR");
        }
        return StatusConverter.convertStatus("OK");
    }

    private Integer checkGuestExistsByNoLoginRequiredUUID(String noLoginRequiredUUID) {
        final WebResource resource = client.resource(path("freevisitors", "noLoginRequired", noLoginRequiredUUID));
        final Integer guestId = resource.get(Integer.class);
        if (guestId > 0) {
            return guestId;
        }
        return null;
    }

   
    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }
}
