/**
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
package org.evolvis.veraweb.onlinereg.rest;


import org.evolvis.veraweb.onlinereg.utils.VworConstants;
import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;
import org.jboss.logging.Logger;
import org.postgresql.util.Base64;

import javax.imageio.ImageIO;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    private static final Logger LOGGER = Logger.getLogger(FileUploadResource.class);
    private static final String FILES_LOCATION = "filesLocation";
    
    private VworPropertiesReader vworPropertiesReader;

    /**
     * Storing incomming image into file system.
     *
     * @param imageStringData Image as String
     * @param extension Png or jpg
     * @param imgUUID Image UUID
     * @throws IOException FIXME
     */
    @POST
    @Path("/save")
    public void saveImageIntoDataSystem(@FormParam("imageStringData") String imageStringData,
                                        @FormParam("extension") String extension,
                                        @FormParam("imageUUID") String imgUUID) throws IOException {

        if (vworPropertiesReader == null) {
            vworPropertiesReader = new VworPropertiesReader();
        }
        final String filesLocation = getFilesLocation();
        BufferedImage image = null;
        try {
            image = createTempImage(imageStringData);
        } catch (Exception e) {
            LOGGER.error("Could not create temp image", e);
        }

        if (extension.equals(VworConstants.EXTENSION_PNG)) {
            image = convertTempImageToJpg(image);
        }

        final String imageName = generateImageName(imgUUID);
        final File fileToStore = new File(filesLocation + imageName);
        ImageIO.write(image, VworConstants.EXTENSION_JPG, fileToStore);
    }

    /**
     * Getting the picture by image UUID
     * @param imgUUID image uuid
     * @return Base64 data
     */
    @GET
    @Path("/download/{imgUUID}")
    public String getImageByUUID(@PathParam("imgUUID") String imgUUID) {
        StringBuilder encodedImage = null;
        try {
            encodedImage = getImage(imgUUID);
        } catch (IOException e) {
            LOGGER.error("Image not found", e);
            // image not found
            // 1. Delete imageUUID
        }
        if (encodedImage != null) {
            return encodedImage.toString();
        }
        return null;
    }

    private String getFilesLocation() {
        final String filesLocation;
        if (vworPropertiesReader.getProperty(FILES_LOCATION) != null) {
            filesLocation = vworPropertiesReader.getProperty(FILES_LOCATION);
        } else {
            filesLocation = "/tmp/";
        }
        return filesLocation;
    }

    private BufferedImage createTempImage(String imageStringData) throws IOException {
        byte[] imageBytes = Base64.decode(imageStringData);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        byteArrayInputStream.close();
        return image;
    }

    private BufferedImage convertTempImageToJpg(BufferedImage pngImage) {
        final BufferedImage jpgImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        jpgImage.createGraphics().drawImage(pngImage, 0, 0, Color.WHITE, null);
        return jpgImage;
    }

    private StringBuilder getImage(String imgUUID) throws IOException {
        final File file = getFile(imgUUID);

        final BufferedImage image = ImageIO.read(file);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        final byte[] imageBytes = byteArrayOutputStream.toByteArray();
        final String imageString = Base64.encodeBytes(imageBytes);
        final StringBuilder encodedImage = new StringBuilder("data:image/jpg;base64,").append(imageString);
        byteArrayOutputStream.close();
        return encodedImage;
    }

    private File getFile(String imgUUID) {
        final VworPropertiesReader propertiesReader = new VworPropertiesReader();
        final String filesLocation = propertiesReader.getProperty(FILES_LOCATION);
        return new File(filesLocation + generateImageName(imgUUID));
    }

    public String generateImageName(String imgUUID) {
        StringBuilder sb = new StringBuilder();
        sb.append(imgUUID);
        sb.append(".");
        sb.append(VworConstants.EXTENSION_JPG);

        return sb.toString();
    }
}
