package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by csalib on 29.09.15.
 * @author csalib
 * @author jnunez
 * @author tglase
 */
public class VworUtils {

    final private static String BASE_RESOURCE = "/rest";

    /** Jackson Object mapper */
    private ObjectMapper mapper = new ObjectMapper();

    /** Jersey Client */
    private Client client;

    public VworUtils() {
        client = Client.create();
        client.addFilter(getAuthorization());
    }

    /**
     * @return Path of the saved images of the guests
     * @throws IOException FIXME
     */
    public String getVworEndPoint() throws IOException {
        PropertiesReader propertiesReader = new PropertiesReader();
        String endpoint = propertiesReader.getProperty("vwor.endpoint");
        return endpoint;
    }

    public HTTPBasicAuthFilter getAuthorization() {
        // FIXME We have to uncomment this line and delete the next line to allow HTTPBasicAuth as configurable
        return new HTTPBasicAuthFilter(getVworAuthUsername(),getVworAuthPassword());
    }

    private String getVworAuthUsername() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vworUser = propertiesReader.getProperty("vwor.auth.user");

        return vworUser;
    }

    private String getVworAuthPassword() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vworPassword = propertiesReader.getProperty("vwor.auth.password");

        return vworPassword;
    }

    /**
     * Method sending Requests to VWOR
     *
     * @param path URI from VworUtils.path()
     * @param type desired result type from JSON mapping
     * @param <T> FIXME
     * @return Entities from the Vwor component
     * @throws IOException FIXME
     */
    public <T> T readResource(String path, TypeReference<T> type) throws IOException {
        final String json = readResource(path);
        try {
            return mapper.readValue(json, type);
        } catch (JsonParseException jpe) {
            return (T) json;
        }
    }

    /**
     * Method sending Requests to VWOR
     *
     * @param path URI from VworUtils.path()
     * @return String from the Vwor component
     *
     * @throws IOException FIXME
     */
    public String readResource(String path) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            return resource.get(String.class);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                resource = client.resource(path);
                return resource.get(String.class);
            } else {
                throw che;
            }
        } catch (UniformInterfaceException uie) {
            throw uie;
        }
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     *
     * @throws IOException FIXME
     */
    public String path(Object... path) throws IOException {
        String r = getVworEndPoint() + BASE_RESOURCE;

        for (Object p : path) {
            r += "/" + p;
        }

        return r;
    }
}
