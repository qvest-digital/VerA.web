package org.evolvis.veraweb.onlinereg.rest;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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
     * @param extension       Png or jpg
     * @param imgUUID         Image UUID
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
     *
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
