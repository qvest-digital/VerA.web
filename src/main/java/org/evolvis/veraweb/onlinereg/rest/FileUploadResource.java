package org.evolvis.veraweb.onlinereg.rest;

import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @Author Jon Nuñez, tarent solutions GmbH on 02.07.15.
 */
@Path("fileupload")
@Produces(MediaType.APPLICATION_JSON)
public class FileUploadResource extends AbstractResource {

    /**
     * Storing incomming image into file system
     *
     * @param image the image
     * @param extension the extension (.jpg, .jpeg or .png)
     * @param imgUUID the uuid of the photo (by guest)
     * @param personId the person ID of the guest
     * @return the response string
     */
    @POST
    @Path("/save")
    public void saveImageIntoDataSystem(@FormParam("imageStringData") String imageStringData,
                                        @FormParam("extension") String extension,
                                        @FormParam("imageUUID") String imgUUID) throws IOException {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageStringData);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // save png as jpg file
        if (extension.equals(VworConstants.EXTENSION_PNG)) {

            BufferedImage newImage = new BufferedImage(image.getWidth(),
            image.getHeight(), BufferedImage.TYPE_INT_RGB);
            newImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            image = newImage;
        }

        String imageName = generateImageName(imgUUID);
        File fileToStore = new File(VworConstants.DESTINY_PATH_FILE_UPLOAD + imageName);
        ImageIO.write(image, VworConstants.EXTENSION_JPG, fileToStore);

    }

    public String generateImageName(String imgUUID) {
        StringBuilder sb = new StringBuilder();
        sb.append(imgUUID);
        sb.append(".");
        sb.append(VworConstants.EXTENSION_JPG);

        return sb.toString();
    }
}
