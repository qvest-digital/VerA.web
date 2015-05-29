package org.evolvis.veraweb.onlinereg.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.evolvis.veraweb.onlinereg.entities.OptionalFieldTypeContent;
import org.hibernate.Query;
import org.hibernate.Session;

@Path("/typecontent")
@Produces(MediaType.APPLICATION_JSON)
public class OptionalFieldTypeContentResource extends AbstractResource {

	/**
	 * Getting type contents by optional field ID
	 * 
	 * @param optionalFieldId optional field ID
	 * @return List<OptionalFieldTypeContent>
	 */
	@GET
	@Path("/{optionalFieldId}")
	public List<OptionalFieldTypeContent> getTypeContentsByOptionalField(@PathParam("optionalFieldId") Integer optionalFieldId) {
        final Session session = openSession();
        try {
            final Query query = session.getNamedQuery("OptionalFieldTypeContent.findTypeContentsByOptionalField");
            query.setInteger("optionalFieldId", optionalFieldId);
            final List<OptionalFieldTypeContent> typeContents = (List<OptionalFieldTypeContent>) query.list();

            return typeContents;
        } finally {
            session.close();
        }
	}
}
