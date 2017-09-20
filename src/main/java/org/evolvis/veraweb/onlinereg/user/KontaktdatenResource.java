package org.evolvis.veraweb.onlinereg.user;

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
import org.evolvis.veraweb.onlinereg.entities.Salutation;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

/**
 * New functions according to the page where the user can change his core data
 *
 * @author sweiz, tarent solutions GmbH
 */
@Path("/kontaktdaten")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class KontaktdatenResource {
    /**
     * base path of all resource
     */
    public static final String BASE_RESOURCE = "/rest";

    /** List of Salutations with attributes */
    private static final TypeReference<List<Salutation>> SALUTATION_LIST = new TypeReference<List<Salutation>>() {
    };

    /**
     * Jersey client
     */
    private Client client;

    /**
     * Configuration
     */
    final private Config config;

    /**
     * Jackson Object Mapper
     */
    final private ObjectMapper mapper = new ObjectMapper();

    final private ResourceReader resourceReader;

    /**
     * Creates a new KontaktdatenResource with configuration and Jersey client
     *
     * @param client
     *            jersey client
     * @param config
     *            configuration
     */
    public KontaktdatenResource(Config config, Client client) {
        this.client = client;
        this.config = config;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    /**
     * Get all salutations
     *
     * @return List of all salutations, if salutations exists
     * @throws IOException
     *             TODO
     */
    @GET
    @Path("/getallsalutations")
    public List<Salutation> getAllSalutations() throws IOException {
        final List<Salutation> salutationList = readResource(path("salutation", "getallsalutations"), SALUTATION_LIST);

        if (salutationList != null) {
            return salutationList;
        }

        return null;
    }

    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }

    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        return resourceReader.readStringResource(path, type);
    }
}
