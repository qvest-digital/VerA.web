package org.evolvis.veraweb.onlinereg.fileupload;

import com.sun.jersey.api.client.Client;

import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
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
    
    /**
     * image types
     */

	final String JPEG = "data:image/jpeg";
	final String JPG = "data:image/jpg";
	final String PNG = "data:image/png";

    public FileUploadResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }


	@POST
	@Path("/save")
	public String saveTempImage(@FormParam("file") String imageString) throws IOException {

		String imageType = imageType(imageString);
		String imageStringData = removeHeaderFromImage(imageString);
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
		// save jpeg as jpg file
		if (imageType.equals("jpeg")) {
			imageType = "jpg";
		}
		File outputfile = new File("/tmp/comeonbaby." + imageType);
		ImageIO.write(image, "jpg", outputfile);
		return StatusConverter.convertStatus("FILE_UPLOAD_ERROR");

	}
     
	private String removeHeaderFromImage(String imageString) {
		if (imageType(imageString).equals("jpg") || imageType(imageString).equals("png"))
			return imageString.substring(22);
		if (imageType(imageString).equals("jpeg"))
			return imageString.substring(23);

		return "ERROR REMOVING HEADER FROM IMAGE";
	}
     
	private String imageType(String imageString) {
		String imageHeader = imageString.substring(0, 15);
		if (imageHeader.contains(JPG)) {
			return "jpg";
		} else if (imageHeader.contains(JPEG)) {
			return "jpeg";
		} else if (imageHeader.contains(PNG)) {
			return "png";
		}
		return "ERROR PARSING IMAGE TYPE";
	}

}
