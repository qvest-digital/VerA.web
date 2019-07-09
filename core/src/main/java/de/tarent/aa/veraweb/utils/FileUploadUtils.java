package de.tarent.aa.veraweb.utils;
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
