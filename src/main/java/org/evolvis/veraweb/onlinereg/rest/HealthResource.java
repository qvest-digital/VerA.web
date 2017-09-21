package org.evolvis.veraweb.onlinereg.rest;

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
import org.evolvis.veraweb.onlinereg.entities.Config;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by mley on 02.09.14.
 */
@Path("/available")
public class HealthResource extends AbstractResource {

    @GET
    @Path("/")
    public  String health() {
        Session session = openSession();
        try {

            Query q = session.getNamedQuery("Config.find");
            q.setString("key", "SCHEMA_VERSION");
            Config c = (Config) q.uniqueResult();
            if (c != null) {
                return "OK";
            } else {
                return "NO SCHEMA_VERSION";
            }
        } finally {
            session.close();
        }

    }
}
