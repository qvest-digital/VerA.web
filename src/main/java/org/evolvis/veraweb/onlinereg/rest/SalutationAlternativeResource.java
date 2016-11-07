package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Salutation;
import org.evolvis.veraweb.onlinereg.entities.SalutationAlternative;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
@Path("/salutation/alternative")
@Produces(MediaType.APPLICATION_JSON)
public class SalutationAlternativeResource extends AbstractResource {

    /**
     * Get all alternative salutations for pdftemplate.
     *
     * @return list with all salutations and their attributes
     */
    @GET
    @Path("/list/{pdftemplate_id}")
    public List getSalutations(@PathParam("pdftemplate_id") Integer pdftemplateId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(SalutationAlternative.GET_SALUTATION_ALTERNATIVE_FACADE_BY_PDF_ID);
            query.setInteger(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdftemplateId);

            return query.list();
        } finally {
            session.close();
        }
    }


    @GET
    @Path("/unused/{pdftemplate_id}")
    public List getSalutationsWithoutAlternativeContent(@PathParam("pdftemplate_id") Integer pdftemplateId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(Salutation.GET_SALUTATIONS_WITHOUT_ALTERNATIVE_CONTENT);
            query.setInteger(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdftemplateId);

            return query.list();
        } finally {
            session.close();
        }
    }
}
