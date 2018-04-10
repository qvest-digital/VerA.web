package org.evolvis.veraweb.onlinereg.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

    /**
     * Configuration
     */
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
            return new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), sb.toString(), base.getQuery(),
                    base.getFragment())
                    .toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Reads the resource at given path and returns the entity.
     *
     * @param path path
     * @param type TypeReference of requested entity
     * @param <T>  Type of requested entity
     * @return requested resource
     * @throws java.io.IOException TODO
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
        if (204 == cr.getStatus()) {
            return null;
        }
        if (cr.getStatus() >= 300) {
            throw new WebApplicationException(cr.getStatus());
        }
        final String json = cr.getEntity(String.class);
        return mapper.readValue(json, type);
    }
}
