package de.tarent.aa.veraweb.utils;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by csalib on 29.09.15.
 *
 * @author csalib
 * @author jnunez
 * @author tglase
 */
@Log4j2
public class VworUtils {
    final private static String BASE_RESOURCE = "/rest";

    /**
     * Jackson Object mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Jersey Client
     */
    private Client client;

    public VworUtils() {
        client = Client.create();
        client.addFilter(getAuthorization());
    }

    /**
     * @return Path of the saved images of the guests
     */
    public String getVworEndPoint() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String endpoint = propertiesReader.getProperty("vwor.endpoint");
        if (endpoint == null) {
            logger.warn("vwor.endpoint is nil");
        }
        return endpoint;
    }

    public HTTPBasicAuthFilter getAuthorization() {
        return new HTTPBasicAuthFilter(getVworAuthUsername(), getVworAuthPassword());
    }

    private String getVworAuthUsername() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vworUser = propertiesReader.getProperty("vwor.auth.user");
        if (vworUser == null) {
            logger.warn("vwor.auth.user is nil");
        }
        return vworUser;
    }

    private String getVworAuthPassword() {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vworPassword = propertiesReader.getProperty("vwor.auth.password");
        if (vworPassword == null) {
            logger.warn("vwor.auth.password is nil");
        }
        return vworPassword;
    }

    /**
     * Method sending Requests to VWOR
     *
     * @param path URI from VworUtils.path()
     * @param type desired result type from JSON mapping
     * @param <T>  FIXME
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
     */
    public String readResource(String path) {
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
        }
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     */
    public String path(String... path) {
        StringBuilder r = new StringBuilder(getVworEndPoint() + BASE_RESOURCE);

        for (String p : path) {
            if (!p.startsWith("/")) {
                r.append("/");
            }
            r.append(p);
        }

        return r.toString();
    }
}
