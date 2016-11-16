package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.entities.Salutation;
import org.evolvis.veraweb.onlinereg.entities.SalutationAlternative;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("/list/{pdftemplateId}")
    public List getSalutations(@PathParam("pdftemplateId") Integer pdftemplateId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(SalutationAlternative.GET_SALUTATION_ALTERNATIVE_FACADE_BY_PDF_ID);
            query.setInteger(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdftemplateId);

            return query.list();
        } finally {
            session.close();
        }
    }


    /**
     * Get the list with salutations witout alternative content.
     *
     * @param pdftemplateId Pdf template id
     *
     * @return List with salutations
     */
    @GET
    @Path("/unused/{pdftemplateId}")
    public List getSalutationsWithoutAlternativeContent(@PathParam("pdftemplateId") Integer pdftemplateId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(Salutation.GET_SALUTATIONS_WITHOUT_ALTERNATIVE_CONTENT);
            query.setInteger(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdftemplateId);

            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Delete alternative salutation.
     *
     * @param salutationId Alternative salutation ID
     *
     * @return {@link Response}
     */
    @DELETE
    @Path("delete/{salutationId}")
    public Response deleteAlternativeSalutation(@PathParam("salutationId") Integer salutationId){
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(SalutationAlternative.DELETE_SALUTATION_ALTERNATIVE_BY_ID);
            query.setInteger(SalutationAlternative.PARAM_PK, salutationId);

            query.executeUpdate();
            session.flush();

            return Response.ok(salutationId).build();
        } finally {
            session.close();
        }
    }

    /**
     * Save alternative salutation.
     *
     * @param pdftemplateId Pdf template id
     * @param salutationId Salutation id
     * @param content Alternative salutation
     *
     * @return {@link Response}
     */
    @POST
    @Path("/save/{pdftemplateId}/")
    public Response saveAlternativeSalutation(@PathParam("pdftemplateId") Integer pdftemplateId,
                                              @FormParam("salutationId") Integer salutationId,
                                              @FormParam("content") String content) {

        if (pdftemplateId == null || salutationId == null || content == null || content.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final Session session = openSession();

        try {
            final SalutationAlternative salutation = initSalutationAlternative(pdftemplateId, salutationId, content);
            session.save(salutation);
            session.flush();

            return Response.ok(salutation).build();
        } finally {
            session.close();
        }
    }

    private SalutationAlternative initSalutationAlternative(Integer pdftemplateId, Integer salutationId, String content){
        SalutationAlternative salutationAlternative = new SalutationAlternative();
        salutationAlternative.setPdftemplate_id(pdftemplateId);
        salutationAlternative.setSalutation_id(salutationId);
        salutationAlternative.setContent(content);

        return salutationAlternative;
    }
}
