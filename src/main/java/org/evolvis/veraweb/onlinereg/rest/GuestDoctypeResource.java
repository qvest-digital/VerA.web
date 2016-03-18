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

import org.evolvis.veraweb.onlinereg.entities.GuestDoctype;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Max Marche <m.marche@tarent.de> on 30.12.14.
 */
@Path("/guestDoctype")
@Produces(MediaType.APPLICATION_JSON)
public class GuestDoctypeResource extends AbstractResource {

	/**
	 * Create guest doctype.
	 *
	 * @param guestId Guest id
	 * @param firstName First name
	 * @param lastName Last name
	 * @return The {@link GuestDoctype}
	 */
	@POST
	@Path("/")
	public GuestDoctype createGuestDoctype(
			@FormParam("guestId") int guestId,
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName) {
		final int fkDoctype = 1;
		final int addresstype = 1;
		final int locale = 1;
		final Session session = openSession();

		try {
			return handleCreateGuestDoctype(guestId,
					fkDoctype, addresstype, locale, firstName, lastName, session);
		} finally {
			session.close();
		}
	}

	private GuestDoctype handleCreateGuestDoctype(int fkGuest, int fkDoctype, int addresstype, int locale,
												  String firstname, String lastname, Session session) {

		final GuestDoctype guestDoctype = new GuestDoctype(fkGuest, fkDoctype, addresstype, locale);
		guestDoctype.setFirstname(firstname);
		guestDoctype.setLastname(lastname);
		final Query query = getSelectGuestDoctypeByDoctypeIdAndPersonIdQuery(guestDoctype, session);

		if (!query.list().isEmpty()) {
			// user already exists
			return null;
		}

		persistGuestDoctype(guestDoctype, session);
		return guestDoctype;
	}

	private void persistGuestDoctype(GuestDoctype guestDoctype, Session session) {
		session.persist(guestDoctype);
		session.flush();
	}

	private Query getSelectGuestDoctypeByDoctypeIdAndPersonIdQuery(GuestDoctype guestDoctype, Session session) {

		final Query query = session
				.getNamedQuery("GuestDoctype.findByDoctypeIdAndGuestId");
		query.setInteger("fk_guest", guestDoctype.getFk_guest());
		query.setInteger("fk_doctype", guestDoctype.getFk_doctype());
		return query;
	}
}
