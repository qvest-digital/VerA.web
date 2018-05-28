package org.evolvis.veraweb.onlinereg.fileupload;

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
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.extern.java.Log;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.utils.ResourceReader;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.evolvis.veraweb.onlinereg.utils.VerawebConstants;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * @author by Jon Nuñez, tarent solutions GmbH on 29.06.15.
 */
@Path("/fileupload")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class FileUploadResource {

    /**
     * Jersey client
     */
    private final Client client;

    /**
     * Configuration
     */
    private final Config config;

    /**
     * Jackson Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();

    private final ResourceReader resourceReader;

    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";

    /**
     * Return types
     */
    private static final TypeReference<String> STRING = new TypeReference<String>() {
    };

    public FileUploadResource(Config config, Client client) {
        this.client = client;
        this.config = config;
        this.resourceReader = new ResourceReader(client, mapper, config);
    }

    @POST
    @Path("/save")
    public String saveTempImage(@FormParam("file") String imageString,
      @FormParam("imgUUID") String imgUUID) throws IOException {

        String extension = getImageType(imageString);
        String imageStringData = removeHeaderFromImage(imageString);

        uploadImage(imageStringData, extension, imgUUID);

        return StatusConverter.convertStatus("OK");
    }

    @GET
    @Path("/download/{imgUUID}")
    public String downloadGuestImage(@PathParam("imgUUID") String imgUUID) throws IOException {

        WebResource resource = client.resource(config.getVerawebEndpoint() + BASE_RESOURCE +
          "/fileupload/download/" + imgUUID);

        String encodedImage;
        try {
            encodedImage = resource.get(String.class);
        } catch (UniformInterfaceException e) {
            int statusCode = e.getResponse().getStatus();
            if (statusCode == 204) {
                return null;
            }

            return null;
        }

        return StatusConverter.convertStatus(encodedImage);
    }

    public void uploadImage(String imageStringData, String extension, String imgUUID) {
        final WebResource resource = client.resource(path("fileupload", "save"));

        final Form postBody = new Form();

        postBody.add("imageStringData", imageStringData);
        postBody.add("extension", extension);
        postBody.add("imageUUID", imgUUID);

        resource.post(postBody);
    }

    private String removeHeaderFromImage(String imageString) {
        if (getImageType(imageString).equals(VerawebConstants.EXTENSION_JPG) ||
          getImageType(imageString).equals(VerawebConstants.EXTENSION_PNG)) {
            return imageString.substring(22);
        }
        if (getImageType(imageString).equals(VerawebConstants.EXTENSION_JPEG)) {
            return imageString.substring(23);
        }

        return "ERROR REMOVING HEADER FROM IMAGE";
    }

    private String getImageType(String imageString) {
        String imageHeader = imageString.substring(0, 15);
        if (imageHeader.contains(VerawebConstants.JPG)) {
            return VerawebConstants.EXTENSION_JPG;
        } else if (imageHeader.contains(VerawebConstants.JPEG)) {
            return VerawebConstants.EXTENSION_JPEG;
        } else if (imageHeader.contains(VerawebConstants.PNG)) {
            return VerawebConstants.EXTENSION_PNG;
        }
        return "ERROR_PARSING_IMAGE_TYPE";
    }

    private String path(Object... path) {
        return resourceReader.constructPath(BASE_RESOURCE, path);
    }
}
