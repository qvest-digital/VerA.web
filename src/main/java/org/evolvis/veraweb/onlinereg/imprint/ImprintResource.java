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
package org.evolvis.veraweb.onlinereg.imprint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import lombok.Getter;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.utils.ImprintTransporter;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/imprint")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class ImprintResource {

    /** base path of all resource */
    public static final String BASE_RESOURCE = "/rest";

    /** List of Events type */
    private static final TypeReference<HashMap<String, String>> IMPRINT_LIST = new TypeReference<HashMap<String, String>>() {};

    /** Jersey client */
    private Client client;

    /** Configuration */
    private Config config;

    /** Jackson Object Mapper */
    private ObjectMapper mapper = new ObjectMapper();

    /** Servlet context */
    @javax.ws.rs.core.Context
    @Getter
    private HttpServletRequest request;

    private final ResourceReader resourceReader;

    /**
     * Creates a new EventResource
     *
     * @param client jersey client
     * @param config configuration
     */
    public ImprintResource(Config config, Client client) {
        this.client = client;
        this.config = config;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    @GET
    @Path("/")
    public List<ImprintTransporter> getImprint(@QueryParam("current_language") String currentLanguageKey) throws IOException {

        final String imprintString = resourceReader.constructPath(BASE_RESOURCE, "imprint", currentLanguageKey);
        final HashMap<String, String> listImprint = resourceReader.readStringResource(imprintString, IMPRINT_LIST);
        final List<ImprintTransporter> listTransporter = new ArrayList<>();

        for (Map.Entry<String, String> imprint : listImprint.entrySet()) {
            listTransporter.add(new ImprintTransporter(imprint.getKey(), imprint.getValue()));
        }

        Collections.sort(listTransporter);

        return listTransporter;
    }

}
