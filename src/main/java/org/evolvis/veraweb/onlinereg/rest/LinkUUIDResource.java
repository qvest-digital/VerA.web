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
import org.evolvis.veraweb.onlinereg.entities.LinkUUID;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/links")
@Produces(MediaType.APPLICATION_JSON)
public class LinkUUIDResource extends AbstractResource {

    @Path("/{uuid}")
    @GET
    public Integer getUserIdByUUID(@PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("LinkUUID.getUserIdByUUID");
            query.setString("uuid", uuid);
            if (query.list().isEmpty()) {
                // user does not exists
                return null;
            } else {
                return (int) query.uniqueResult();
            }

        } finally {
            session.close();
        }
    }

    @Path("/delete")
    @POST
    public void deleteUUID(@FormParam("personId") Integer personId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("LinkUUID.deleteUUIDByPersonid");
            query.setInteger("personid", personId);
            query.executeUpdate();
        } finally {
            session.close();
        }
    }

    @Path("/byPersonId/{personId}")
    @GET
    public List<LinkUUID> getLinkUuidsByPersonId(@PathParam("personId") Integer personId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("LinkUUID.getLinkUuidByPersonid");
            query.setParameter("personid", personId);
            return (List<LinkUUID>) query.list();
        } finally {
            session.close();
        }
    }
}
