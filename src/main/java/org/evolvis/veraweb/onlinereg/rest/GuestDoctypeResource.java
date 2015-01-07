package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.GuestDoctype;
import org.evolvis.veraweb.onlinereg.entities.PersonDoctype;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by Max Marche <m.marche@tarent.de> on 30.12.14.
 */
@Path("/guestDoctype")
@Produces(MediaType.APPLICATION_JSON)
public class GuestDoctypeResource extends AbstractResource {

	@POST
	@Path("/")
	public GuestDoctype createGuestDoctype(
			@QueryParam("guestId") int guestId,
			@QueryParam("firstName") String firstName,
			@QueryParam("lastName") String lastName) {
		int fk_doctype = 1;
		int addresstype = 1;
		int locale = 1;
		Session session = openSession();

		try {
			GuestDoctype guestDoctype = handleCreateGuestDoctype(guestId,
					fk_doctype, addresstype, locale, firstName, lastName, session);
			return guestDoctype;
		} finally {
			session.close();
		}
	}

	private GuestDoctype handleCreateGuestDoctype(int fk_guest,
			int fk_doctype, int addresstype, int locale, 
			String firstname, String lastname, Session session) {
		
		GuestDoctype guestDoctype = new GuestDoctype(fk_guest, fk_doctype, addresstype, locale);
		guestDoctype.setFirstname(firstname);
		guestDoctype.setLastname(lastname);
		Query query = getSelectGuestDoctypeByDoctypeIdAndPersonIdQuery(guestDoctype,  session);
		
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

	private Query getSelectGuestDoctypeByDoctypeIdAndPersonIdQuery(
			GuestDoctype guestDoctype, Session session) {

		Query query = session
				.getNamedQuery("GuestDoctype.findByDoctypeIdAndGuestId");
		query.setInteger("fk_guest", guestDoctype.getFk_guest());
		query.setInteger("fk_doctype", guestDoctype.getFk_doctype());
		return query;
	}
}
