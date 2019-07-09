package org.evolvis.veraweb.onlinereg.utils;
/**
 * @author Jon Nuñez, tarent solutions GmbH on 02.07.15.
 */
public class VworConstants {
    /**
     * File upload extensions
     */
    public static final String EXTENSION_JPG = "jpg";
    public static final String EXTENSION_PNG = "png";

    /**
     * Content types for email.
     */
    public static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    /**
     * Content type for the guest list export file.
     */
    public static final String TEXT_CSV_CONTENT_TYPE = "text/csv";
    public static final String APPLICATION_PDF_CONTENT_TYPE = "application/pdf";

    /**
     * Http status codes
     */
    public static final Integer HTTP_OK = 200;
    public static final Integer HTTP_PRECONDITION_FAILED = 414;
    public static final Integer HTTP_POLICY_NOT_FULFILLED = 420;

    private VworConstants() {
    }
}
