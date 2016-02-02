/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package org.evolvis.veraweb.onlinereg.utils;

/**
 * @author Jon Nuñez, tarent solutions GmbH on 02.07.15.
 */
public class VworConstants {

    /**Responses from File upload mechanismus */
    public static final String UPLOAD_OK = "OK";
    public static final String UPLOAD_ERROR = "FILE_UPLOAD_ERROR";

    /** File upload data types*/
    public static final String JPEG = "data:image/jpeg";
    public static final String JPG = "data:image/jpg";
    public static final String PNG = "data:image/png";
    /** File upload extensions */
    public static final String EXTENSION_JPEG = "jpeg";
    public static final String EXTENSION_JPG = "jpg";
    public static final String EXTENSION_PNG = "png";

    /**
     * Content types for email.
     */
    public static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";
    public static final String PLAINTEXT_CONTENT_TYPE = "text/plain; charset=utf-8";
}
