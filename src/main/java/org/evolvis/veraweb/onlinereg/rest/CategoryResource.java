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
package org.evolvis.veraweb.onlinereg.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * This class handles requests about category.
 */
@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource extends AbstractResource {

    /**
     * Get category id by media representatives uuid and category name.
     *
     * @param catname Category name
     * @param uuid UUID for the media representatives
     *
     * @return The category id
     */
    @GET
    @Path("/{catname}/{uuid}")
    public Integer getCategoryId(@PathParam("catname") String catname, @PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.findIdByCatname");
            query.setString("pcatname", catname);
            query.setString("uuid", uuid);
            final Integer categoryId = (Integer) query.uniqueResult();
            if (categoryId != null) {
            	return categoryId;
            }
            return 0;
        } finally {
            session.close();
        }
    }

    /**
     * Get category name by event hash
     *
     * @param eventId
     * @return all category names of this event
     */
    @GET
    @Path("/fields/list/{eventId}")
    public List<String> getCategoriesByEventId(@PathParam("eventId") int eventId) {
    	final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.findCatnamesByEventId");
            query.setInteger("eventId", eventId);

            final List<String> categoryName = (List<String>) query.list();

            //TODO NULL-Check?
           	return categoryName;
        } finally {
            session.close();
        }
    }
}
