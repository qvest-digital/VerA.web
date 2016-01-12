/**
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
package org.evolvis.veraweb.onlinereg.rest;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.entities.PersonCategory;
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
			query.setString("catname", catname);
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
     * Get category name by event hash.
     *
     * @param eventId The event id
     *
     * @return All category names for this event
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

    /**
     * Get category ID by category name.
     *
     * @param catname The name of the category
     *
     * @return Category ID
     */
    @GET
    @Path("/identify")
    public Integer getCategoryIdByCategoryName(@QueryParam("catname") String catname) {
		final Session session = openSession();
		try {
			final Query query = session.getNamedQuery("Category.getCategoryIdByCategoryName");
			query.setString("catname", catname);

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
														@PathParam("personId") String personId) {
		final Session session = openSession();
		try {
			final Query query = session.getNamedQuery("Category.findCatnameByUserAndDelegation");
			query.setString("uuid", uuid);
			query.setInteger("personId", Integer.parseInt(personId));
			String catname = (String) query.uniqueResult();

			return catname;
		} finally {
			session.close();
		}
	}

	/**
     * Update delegate's category
     * 
     * @param uuid Delegation UUID
     * @param personId Person ID
     * @param categoryName Category name
     */
    @POST
    @Path("update/delegate/category")
    public void updateDelegateCategory(
            @FormParam("uuid") String uuid,
            @FormParam("personId") Integer personId,
            @FormParam("category") String categoryName) {
		final Session session = openSession();
		try {
			final Guest guest = updateGuestCategory(uuid, personId, categoryName, session);
			final Integer categoryId = guest.getFk_category();
			updatePersonCategory(personId, session, categoryId);

			session.flush();
		} finally {
			session.close();
		}
	}

	@GET
	@Path("person/data")
	public Integer getCategoryByCatnameAndOrgunit(@QueryParam("catname") String categoryName,
												  @QueryParam("personId") String personId) {
		final Session session = openSession();
		try {
			final Query queryCategory = session.getNamedQuery("Category.findCategoryByPersonIdAndCatname");
			queryCategory.setString("catname", categoryName);
			queryCategory.setInteger("personId", Integer.valueOf(personId));

			final Integer category = (Integer) queryCategory.uniqueResult();

			return category;
		} finally {
			session.close();
		}
	}

	private void updatePersonCategory(Integer personId, final Session session, Integer categoryId) {
		if (checkExistingPersonCategory(personId, categoryId, session)) {
			PersonCategory personCategory = new PersonCategory();
			personCategory.setFk_person(personId);
			personCategory.setFk_categorie(categoryId);
			session.saveOrUpdate(personCategory);
		}
	}

	private Guest updateGuestCategory(String uuid, Integer personId, String categoryName, final Session session) {
		Integer category = getCategoryByPersonIdAndCatname(personId, categoryName, session);
		Guest guest = getCurrentGuest(uuid, personId, session);
		guest.setFk_category(category);
		session.saveOrUpdate(guest);

		return guest;
	}
	
	private Boolean checkExistingPersonCategory(final Integer personId,
												final Integer categoryId,
												final Session session) {
		final Query queryCategory = session.getNamedQuery("PersonCategory.personCategoryExists");
		queryCategory.setInteger("personId", personId);

		if (categoryId != null) {
			queryCategory.setInteger("categoryId", categoryId);
		} else {
			return false; // if categoryId is null, then the category can't be an existing for the user.
		}

		return queryCategory.list().isEmpty();
	}
	
	private Integer getCategoryByPersonIdAndCatname(final Integer personId, final String categoryName,
													final Session session) {
		final Query queryCategory = session.getNamedQuery("Category.findCategoryByPersonIdAndCatname");
		queryCategory.setString("catname", categoryName);
		queryCategory.setInteger("personId", personId);

		final Integer category = (Integer) queryCategory.uniqueResult();
		return category;
	}

	private Guest getCurrentGuest(final String uuid, final Integer personId, final Session session) {
		final Query query = session.getNamedQuery("Guest.findEventIdByDelegationUUIDandPersonId");
		query.setString("uuid", uuid);
		query.setInteger("personId", personId);

		final Guest guest = (Guest) query.uniqueResult();
		return guest;
	}
}
