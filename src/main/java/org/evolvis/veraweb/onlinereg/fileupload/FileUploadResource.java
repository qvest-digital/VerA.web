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

    public FileUploadResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }


     @POST
     @Path("/save")
     public String saveTempImage(@FormParam("file") String imageString) throws IOException {
    	 

    	        BufferedImage image = null;
    	        byte[] imageByte;
    	        try {
    	            BASE64Decoder decoder = new BASE64Decoder();
    	            imageByte = decoder.decodeBuffer(imageString.substring(23));
    	            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
    	            image = ImageIO.read(bis);
    	            bis.close();
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        } finally {
    	        	System.out.println("Chech image here");
    	        }
    	     File outputfile = new File ("/tmp/comeonbaby.jpg");
    	     ImageIO.write(image, "jpg", outputfile);

            return StatusConverter.convertStatus("FILE_UPLOAD_ERROR");


     }

}
