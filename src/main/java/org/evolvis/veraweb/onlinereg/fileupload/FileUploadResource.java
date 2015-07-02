package org.evolvis.veraweb.onlinereg.fileupload;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;

import javax.imageio.ImageIO;

import org.evolvis.veraweb.onlinereg.entities.Guest;
import org.evolvis.veraweb.onlinereg.utils.VerawebConstants;
import sun.misc.BASE64Decoder;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Jon Nuñez, tarent solutions GmbH on 29.06.15.
 */
@Path("/fileupload")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class FileUploadResource {

    /** Jersey client */
    private Client client;

    /** Configuration */
    private Config config;

    /**
     * Base path of all resources.
     */
    private static final String BASE_RESOURCE = "/rest";
    
    public FileUploadResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }

	@POST
	@Path("/save")
	public String saveTempImage(@FormParam("file") String imageString, @FormParam("imgUUID") String imgUUID) throws IOException {

		String extension = getImageType(imageString);
		String imageStringData = removeHeaderFromImage(imageString);

		uploadImage(imageStringData,extension,imgUUID);

		return "OK";
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
		if (getImageType(imageString).equals(VerawebConstants.EXTENSION_JPG) || getImageType(imageString).equals(VerawebConstants.EXTENSION_PNG))
			return imageString.substring(22);
		if (getImageType(imageString).equals(VerawebConstants.EXTENSION_JPEG))
			return imageString.substring(23);

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

	/**
	 * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
	 *
	 * @param path path fragments
	 * @return complete path as string
	 */
	private String path(Object... path) {
		String r = config.getVerawebEndpoint() + BASE_RESOURCE;

		for (Object p : path) {
			r += "/" + p;
		}

		return r;
	}
}
