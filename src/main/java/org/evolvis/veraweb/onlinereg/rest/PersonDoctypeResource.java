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

import org.evolvis.veraweb.onlinereg.entities.PersonDoctype;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Max Marche <m.marche@tarent.de> on 22.12.14.
 */
@Path("/personDoctype")
@Produces(MediaType.APPLICATION_JSON)
public class PersonDoctypeResource extends AbstractResource {

	@POST
	@Path("/")
	public PersonDoctype createPersonDoctype(@FormParam("personId") int personId,
											 @FormParam("firstName") String firstName,
											 @FormParam("lastName") String lastName) {
		int fk_doctype = 1;
		int addresstype = 1;
		int locale = 1;
		String textfield = firstName + " " + lastName;
		Session session = openSession();

		try {
			return handleCreatePersonDoctype(personId,
					fk_doctype, addresstype, locale, textfield, session);
		} finally {
			session.close();
		}
	}

	private PersonDoctype handleCreatePersonDoctype(int fkPerson, int fkDoctype, int addresstype, int locale,
													String textfield, Session session) {

		PersonDoctype personDoctype = new PersonDoctype(fkPerson, fkDoctype, addresstype, locale);
		personDoctype.setTextfield(textfield);

		Query query = getSelectPersonDoctypeByDoctypeIdAndPersonIdQuery(personDoctype, session);

		if (!query.list().isEmpty()) {
			// user already exists
			return null;
		}

		persistPersonDoctype(personDoctype, session);
		
		return personDoctype;
	}

	private void persistPersonDoctype(PersonDoctype personDoctype, Session session) {
		session.persist(personDoctype);
		session.flush();
	}

	private Query getSelectPersonDoctypeByDoctypeIdAndPersonIdQuery(PersonDoctype personDoctype, Session session) {

		Query query = session.getNamedQuery("PersonDoctype.findByDoctypeIdAndPersonId");
		query.setInteger("fk_person", personDoctype.getFk_person());
		query.setInteger("fk_doctype", personDoctype.getFk_doctype());

		return query;
	}
}
