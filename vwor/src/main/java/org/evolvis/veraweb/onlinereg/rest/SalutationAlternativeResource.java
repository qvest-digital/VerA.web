package org.evolvis.veraweb.onlinereg.rest;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.onlinereg.entities.Salutation;
import org.evolvis.veraweb.onlinereg.entities.SalutationAlternative;
import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import org.hibernate.Session;
import org.hibernate.query.Query;

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
@Path(RestPaths.REST_SALUTATION_ALTERNATIVE)
@Produces(MediaType.APPLICATION_JSON)
public class SalutationAlternativeResource extends AbstractResource {
    private static final int MAX_SALUTATION_LENGTH = 100;

    /**
     * Get all alternative salutations for pdftemplate.
     *
     * @return list with all salutations and their attributes
     */
    @GET
    @Path(RestPaths.REST_SALUTATION_ALTERNATIVE_GET_ALL)
    public List getSalutations(@PathParam("pdftemplateId") Integer pdftemplateId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(SalutationAlternative.GET_SALUTATION_ALTERNATIVE_FACADE_BY_PDF_ID);
            query.setParameter(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdftemplateId);

            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get the list with salutations witout alternative content.
     *
     * @param pdftemplateId Pdf template id
     * @return List with salutations
     */
    @GET
    @Path(RestPaths.REST_SALUTATION_ALTERNATIVE_UNUSED)
    public List getSalutationsWithoutAlternativeContent(@PathParam("pdftemplateId") Integer pdftemplateId) {
        final Session session = openSession();

        try {
            final Query query = session.getNamedQuery(Salutation.GET_SALUTATIONS_WITHOUT_ALTERNATIVE_CONTENT);
            query.setParameter(SalutationAlternative.PARAM_PDFTEMPLATE_ID, pdftemplateId);

            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Delete alternative salutation.
     *
     * @param salutationId Alternative salutation ID
     * @return {@link Response}
     */
    @DELETE
    @Path(RestPaths.REST_SALUTATION_ALTERNATIVE_DELETE)
    public Response deleteAlternativeSalutation(@PathParam("salutationId") Integer salutationId) {
        final Session session = openSession();
        session.beginTransaction();

        try {
            final Query query = session.getNamedQuery(SalutationAlternative.DELETE_SALUTATION_ALTERNATIVE_BY_ID);
            query.setParameter(SalutationAlternative.PARAM_PK, salutationId);

            query.executeUpdate();
            session.flush();
            session.getTransaction().commit();

            return Response.ok(salutationId).build();
        } finally {
            session.close();
        }
    }

    /**
     * Save alternative salutation.
     *
     * @param pdftemplateId Pdf template id
     * @param salutationId  Salutation id
     * @param content       Alternative salutation
     * @return {@link Response}
     */
    @POST
    @Path(RestPaths.REST_SALUTATION_ALTERNATIVE_SAVE)
    public Response saveAlternativeSalutation(@PathParam("pdftemplateId") Integer pdftemplateId,
      @FormParam("salutationId") Integer salutationId,
      @FormParam("content") String content) {

        if (pdftemplateId == null || salutationId == null || "".equals(content)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (content.length() > MAX_SALUTATION_LENGTH) {
            return Response.status(VworConstants.HTTP_POLICY_NOT_FULFILLED).build();
        }

        final Session session = openSession();
        session.beginTransaction();

        try {
            final SalutationAlternative salutation = initSalutationAlternative(pdftemplateId, salutationId, content);
            session.save(salutation);
            session.flush();
            session.getTransaction().commit();

            return Response.ok(salutation).build();
        } finally {
            session.close();
        }
    }

    private SalutationAlternative initSalutationAlternative(Integer pdftemplateId, Integer salutationId, String content) {
        SalutationAlternative salutationAlternative = new SalutationAlternative();
        salutationAlternative.setPdftemplate_id(pdftemplateId);
        salutationAlternative.setSalutation_id(salutationId);
        salutationAlternative.setContent(content);

        return salutationAlternative;
    }
}
