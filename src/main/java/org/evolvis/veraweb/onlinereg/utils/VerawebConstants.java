package org.evolvis.veraweb.onlinereg.utils;

/**
 * Util constants class
 *
 * @author Jon Nuñez, tarent solutions GmbH on 12.06.15.
 */
public class VerawebConstants {

    /** Guest business logic*/
        public static final int GUEST_STATUS_OPEN = 0;
        public static final int GUEST_STATUS_ACCEPT = 1;
        public static final int GUEST_STATUS_REJECT = 2;

    /** File upload data types*/
        public static final String JPEG = "data:image/jpeg";
        public static final String JPG = "data:image/jpg";
        public static final String PNG = "data:image/png";
    /** File upload extensions */
        public static final String EXTENSION_JPEG = "jpeg";
        public static final String EXTENSION_JPG = "jpg";
        public static final String EXTENSION_PNG = "png";

    /** Responses of reserve and guest list checks */
        public static final String GUEST_LIST_OK = "GUEST_LIST_OK";
        public static final String WAITING_LIST_OK = "WAITING_LIST_OK";
        public static final String WAITING_LIST_FULL = "WAITING_LIST_FULL";

}
