package org.evolvis.veraweb.common;
public class RestPaths {
    /**
     * Health-Resource
     */
    public static final String REST_HEALTH_AVAILABLE = "/available";

    /**
     * Export-Resource
     */
    public static final String REST_EXPORT = "/export";
    public static final String REST_EXPORT_GET_GUESTLIST = "/guestList/{eventId}";

    /**
     * FileUpload-Resource
     */
    public static final String REST_FILEUPLOAD = "/fileupload";
    public static final String REST_FILEUPLOAD_SAVE = "/save";
    public static final String REST_FILEUPLOAD_DOWNLOAD = "/download";
    public static final String REST_FILEUPLOAD_DL_IMG = "/{imgUUID}";

    /**
     * Info-Resource
     */
    public static final String REST_INFO = "/info";

    /**
     * Mailing-Resource
     */
    public static final String REST_MAILING = "/mailing";

    /**
     * Mailtemplate-Resource
     */
    public static final String REST_MAILTEMPLATE = "/mailtemplate";

    /**
     * OptionalField-Resource
     */
    public static final String REST_OPTIONALFIELDS = "/optional/fields";
    public static final String REST_OPTIONALFIELDS_GET_ALL = "/list/{eventId}";

    /**
     * PdfTemplate-Resource
     */
    public static final String REST_PDFTEMPLATE = "/pdftemplate";
    public static final String REST_PDFTEMPLATE_EDIT = "/edit";
    public static final String REST_PDFTEMPLATE_DELETE = "/delete";
    public static final String REST_PDFTEMPLATE_GET_ALL = "/list";
    public static final String REST_PDFTEMPLATE_EXPORT = "/export";

    /**
     * SalutationAlternative-Resource
     */
    public static final String REST_SALUTATION_ALTERNATIVE = "/salutation/alternative";
    public static final String REST_SALUTATION_ALTERNATIVE_GET_ALL = "/list/{pdftemplateId}";
    public static final String REST_SALUTATION_ALTERNATIVE_UNUSED = "/unused/{pdftemplateId}";
    public static final String REST_SALUTATION_ALTERNATIVE_DELETE = "delete/{salutationId}";
    public static final String REST_SALUTATION_ALTERNATIVE_SAVE = "/save/{pdftemplateId}/";
}
