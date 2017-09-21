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
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * This class handles requests about functions.
 */
@Path("/function")
@Produces(MediaType.APPLICATION_JSON)
public class FunctionResource extends AbstractResource {

    /**
     * Get function name by event hash
     *
     * @param eventId Event id
     * @return all function names of this event
     */
    @GET
    @Path("/fields/list/function")
    public List<String> getFunctionsByEventId(@PathParam("eventId") int eventId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Function.getFunctions");

            return (List<String>) query.list();
        } finally {
            session.close();
        }
    }
}
