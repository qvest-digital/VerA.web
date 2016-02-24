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
package org.evolvis.veraweb.onlinereg.user;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.Config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.entities.Salutation;

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
    private static final TypeReference<List<Salutation>> SALUTATION_LIST = new TypeReference<List<Salutation>>() {};

    /**
     * Jersey client
     */
    private Client client;

    /**
     * Configuration
     */
    private Config config;

    /**
     * Jackson Object Mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new KontaktdatenResource with configuration and Jersey client
     *
     * @param client jersey client
     * @param config configuration
     */
    public KontaktdatenResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }

    /**
     * Get all salutations
     *
     * @return List of all salutations, if salutations exists
     * @throws IOException TODO
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

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T>  Type of requested entity
     * @return requested resource
     * @throws IOException
     */
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
                resource = client.resource(path);
                String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }

        } catch (UniformInterfaceException uie) {
            log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }
}
