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
import org.evolvis.veraweb.onlinereg.entities.PersonCategory;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Max Marche, m.marche@tarent.de on 22.12.14.
 */
@Path("/personcategory")
@Produces(MediaType.APPLICATION_JSON)
public class PersonCategoryResource extends AbstractResource {

	@POST
	@Path("/add")
	public PersonCategory createPersonCategory(@FormParam("personId") Integer personId,
											   @FormParam("categoryId") Integer categoryId) {

		final Session session = openSession();

		try {
			final PersonCategory personCategory = handleCreatePersonCategory(personId, categoryId);
			session.save(personCategory);
			session.flush();

			return personCategory;
		} finally {
			session.close();
		}
	}

	private PersonCategory handleCreatePersonCategory(Integer personId, Integer categoryId) {
		final PersonCategory personCategory = new PersonCategory();
		personCategory.setFk_person(personId);
		personCategory.setFk_categorie(categoryId);

		return personCategory;
	}
}
