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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This class handles requests about category.
 */
@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource extends AbstractResource {

    private static final String CATEGORY_NAME = "catname";
    private static final String PERSON_ID = "personId";

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
    public Integer getCategoryId(@PathParam(CATEGORY_NAME) String catname, @PathParam("uuid") String uuid) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.findIdByCatname");
            query.setString(CATEGORY_NAME, catname);
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
     * Get category ID by category name.
     *
     * @param catname The name of the category
     *
     * @return Category ID
     */
    @GET
    @Path("/identify")
    public Integer getCategoryIdByCategoryName(@QueryParam(CATEGORY_NAME) String catname) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.getCategoryIdByCategoryName");
            query.setString(CATEGORY_NAME, catname);

            return (Integer) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get category name by person ID and Delegation UUID
     * 
     * @param uuid delegation UUID
     * @param personId person ID
     * @return String catname
     */
    @GET
    @Path("/catname/{uuid}/{personId}")
    public String getCatnameByPersonIdAndDelegationUUID(@PathParam("uuid") String uuid,
                                                        @PathParam(PERSON_ID) String personId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("Category.findCatnameByUserAndDelegation");
            query.setString("uuid", uuid);
            query.setInteger(PERSON_ID, Integer.parseInt(personId));
            return (String) query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @GET
    @Path("person/data")
    public Integer getCategoryByCatnameAndOrgunit(@QueryParam(CATEGORY_NAME) String categoryName,
                                                  @QueryParam(PERSON_ID) String personId) {
        final Session session = openSession();
        try {
            final Query queryCategory = session.getNamedQuery("Category.findCategoryByPersonIdAndCatname");
            queryCategory.setString(CATEGORY_NAME, categoryName);
            queryCategory.setInteger(PERSON_ID, Integer.valueOf(personId));
            return (Integer) queryCategory.uniqueResult();
        } finally {
            session.close();
        }
    }
}
