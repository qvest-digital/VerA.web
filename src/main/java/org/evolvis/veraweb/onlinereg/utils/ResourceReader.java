package org.evolvis.veraweb.onlinereg.utils;

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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.evolvis.veraweb.onlinereg.Config;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class ResourceReader {

    private Client client;

    private ObjectMapper mapper;

    /** Configuration */
    private Config config;

    /**
     * Custom constructor.
     *
     * @param client The {@link com.sun.jersey.api.client.Client}
     * @param mapper The {@link com.fasterxml.jackson.databind.ObjectMapper}
     * @param config The {@link org.evolvis.veraweb.onlinereg.Config}
     */
    public ResourceReader(Client client, ObjectMapper mapper, Config config) {
        this.client = client;
        this.mapper = mapper;
        this.config = config;
    }

    /**
     * Constructs a pathParts from VerA.web endpint, BASE_RESOURCE and given
     * pathParts fragmensts.
     *
     * @param pathParts    pathParts fragments
     * @param baseResource The base path to online registration app
     * @return Complete path as string
     */
    public String constructPath(String baseResource, Object... pathParts) {
        final URI base;
       
        try {
            base = new URI(config.getVerawebEndpoint() + baseResource);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
      
        final StringBuilder sb = new StringBuilder(base.getPath());
        for (Object p : pathParts) {
            // path segments must not include forward slashes. Period.
            // path += "/" + URLEncoder.encode(p.toString(),"utf-8");
            String segment = p.toString();
            if (segment.contains("/")) {
                throw new IllegalArgumentException("Don't include '/' in path segments!");
            }
            sb.append('/');
            sb.append(segment);
        }
        
        try {
            return new URI(base.getScheme(),base.getUserInfo(),base.getHost(),base.getPort(),sb.toString(),base.getQuery(),base.getFragment()).toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path
     *            path
     * @param type
     *            TypeReference of requested entity
     * @param <T>
     *            Type of requested entity
     * @return requested resource
     * @throws java.io.IOException
     *             TODO
     */
    public <T> T readStringResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            return readResource_(path, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                // FIXME some times open, pooled connections time out and
                // generate errors
                // log.warning("Retrying request to " + path + " once because of
                // SocketTimeoutException");
               return readResource_(path, type);
            } else {
                throw che;
            }

        } catch (UniformInterfaceException uie) {
            // log.warning(uie.getResponse().getEntity(String.class));
            throw uie;
        }
    }

    private <T> T readResource_(String path, TypeReference<T> type) throws IOException, JsonParseException, JsonMappingException {
        WebResource resource;
        resource = client.resource(path);
        final ClientResponse cr = resource.get(ClientResponse.class);
        if(204 == cr.getStatus()){
            return null;
        }
        if(cr.getStatus() >= 300){
            throw new WebApplicationException(cr.getStatus());
        }
        final String json = cr.getEntity(String.class);
        return mapper.readValue(json, type);
    }
}
