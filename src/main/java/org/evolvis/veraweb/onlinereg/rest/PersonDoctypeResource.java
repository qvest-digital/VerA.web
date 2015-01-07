package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.PersonDoctype;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by Max Marche <m.marche@tarent.de> on 22.12.14.
 */
@Path("/personDoctype")
@Produces(MediaType.APPLICATION_JSON)
public class PersonDoctypeResource extends AbstractResource {

	@POST
	@Path("/")
	public PersonDoctype createPersonDoctype(
			@QueryParam("personId") int personId,
			@QueryParam("firstName") String firstName,
			@QueryParam("lastName") String lastName) {
		int fk_doctype = 1;
		int addresstype = 1;
		int locale = 1;
		String textfield = firstName + " " + lastName;
		Session session = openSession();

		try {
			PersonDoctype personDoctype = handleCreatePersonDoctype(personId,
					fk_doctype, addresstype, locale, textfield, session);
			return personDoctype;
		} finally {
			session.close();
		}
	}

	private PersonDoctype handleCreatePersonDoctype(int fk_person,
			int fk_doctype, int addresstype, int locale, String textfield,
			Session session) {
		
		PersonDoctype personDoctype = new PersonDoctype(fk_person, fk_doctype, addresstype, locale);
		personDoctype.setTextfield(textfield);
		
		Query query = getSelectPersonDoctypeByDoctypeIdAndPersonIdQuery(personDoctype,  session);
		
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

	private Query getSelectPersonDoctypeByDoctypeIdAndPersonIdQuery(
			PersonDoctype personDoctype, Session session) {

		Query query = session.getNamedQuery("PersonDoctype.findByDoctypeIdAndPersonId");
		query.setInteger("fk_person", personDoctype.getFk_person());
		query.setInteger("fk_doctype", personDoctype.getFk_doctype());
		return query;
	}
}
