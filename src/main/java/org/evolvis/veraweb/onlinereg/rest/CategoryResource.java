package org.evolvis.veraweb.onlinereg.rest;

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
}
