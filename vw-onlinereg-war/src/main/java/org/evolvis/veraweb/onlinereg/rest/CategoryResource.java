package org.evolvis.veraweb.onlinereg.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Query;
import org.hibernate.Session;

@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource extends AbstractResource {


    @GET
    @Path("/{catname}")
    public Integer getGuest(@PathParam("catname") String catname) {
        Session session = openSession();
        try {
            Query query = session.getNamedQuery("Category.findIdByCatname");
            query.setString("pcatname", catname);
            Integer returnedValue = (Integer) query.uniqueResult(); 
            if (returnedValue != null) {
            	return returnedValue;
            }
            return 0;
        } finally {
            session.close();
        }
    }
}
