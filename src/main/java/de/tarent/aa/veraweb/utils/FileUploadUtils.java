package de.tarent.aa.veraweb.utils;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import java.util.UUID;

/**
 * @author jnunez Jon Nuñez, tarent solutions GmbH on 01.10.15.
 */
public class FileUploadUtils {

    public static String getImageType(String imageString) {
        if (imageString != null && !imageString.equals("")) {
            String imageHeader = imageString.substring(0, 15);
            if (imageHeader.contains("data:image/jpg")) {
                return "jpg";
            } else if (imageHeader.contains("data:image/jpeg")) {
                return "jpeg";
            } else if (imageHeader.contains("data:image/png")) {
                return "png";
            }

        }
        return "ERROR_PARSING_IMAGE_TYPE";
    }

    public static String removeHeaderFromImage(String imageString) {
        if (getImageType(imageString).equals("jpg") || getImageType(imageString).equals("png")) {
            return imageString.substring(22);
        }
        if (getImageType(imageString).equals("jpeg")) {
            return imageString.substring(23);
        }

        return "ERROR REMOVING HEADER FROM IMAGE";
    }

    public static String generateImageUUID() {
        UUID imageUUID = UUID.randomUUID();
        return imageUUID.toString();
    }
}
