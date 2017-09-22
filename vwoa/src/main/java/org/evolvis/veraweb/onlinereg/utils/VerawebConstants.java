package org.evolvis.veraweb.onlinereg.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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

    /** Press user activation */
        public static final int MEDIA_REPRESENTATIVE_ACTIVE = 1;
        public static final int MEDIA_REPRESENTATIVE_INACTIVE = 0;

    /** java byte size */
        public static final int LONG_BYTE = (Long.SIZE / 8);

}
