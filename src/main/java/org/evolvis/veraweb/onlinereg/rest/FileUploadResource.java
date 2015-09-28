/**
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
package org.evolvis.veraweb.onlinereg.rest;


import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Jon Nuñez, tarent solutions GmbH on 02.07.15.
 */
@Path("fileupload")
@Produces(MediaType.APPLICATION_JSON)
public class FileUploadResource extends AbstractResource {

    /**
     * Storing incomming image into file system
     *
     * @param imageStringData
     * @param extension
     * @param imgUUID
     * @param filesLocation
     * @throws IOException
     */
    @POST
    @Path("/save")
    public void saveImageIntoDataSystem(@FormParam("imageStringData") String imageStringData,
                                        @FormParam("extension") String extension,
                                        @FormParam("imageUUID") String imgUUID,
                                        @FormParam("filesLocation") String filesLocation) throws IOException {

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
        File fileToStore = new File(filesLocation + imageName);
        ImageIO.write(image, VworConstants.EXTENSION_JPG, fileToStore);

    }

    /**
     * Getting the picture by image UUID
     * @param imgUUID image uuid
     * @return Base64 data
     */
    @GET
    @Path("/download")
    public String getImageByUUID(@QueryParam("imgUUID") String imgUUID, @QueryParam("filesLocation") String filesLocation)
            throws IOException {

        File file = new File(filesLocation + generateImageName(imgUUID));

        BufferedImage image = ImageIO.read(file);

        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StringBuilder encodedImage = null;
        try {
            ImageIO.write(image, "jpg", bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            encodedImage = new StringBuilder("data:image/jpg;base64,").append(imageString);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage.toString();
    }

    public String generateImageName(String imgUUID) {
        StringBuilder sb = new StringBuilder();
        sb.append(imgUUID);
        sb.append(".");
        sb.append(VworConstants.EXTENSION_JPG);

        return sb.toString();
    }
}
