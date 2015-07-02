package org.evolvis.veraweb.onlinereg.fileupload;

import com.sun.jersey.api.client.Client;
import lombok.extern.java.Log;
import org.apache.commons.codec.binary.Base64;
import org.evolvis.veraweb.onlinereg.Config;
import org.evolvis.veraweb.onlinereg.utils.StatusConverter;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     public String saveTempImage(@FormParam("file") String stringFile) throws IOException {

     OutputStream outputStream = null;
            byte[] data = Base64.decodeBase64(stringFile);

            try (OutputStream stream = new FileOutputStream("/tmp/flowyimage.png")) {
                stream.write(data);
            } finally {
                if (outputStream != null) {
                    try {
                        //                     outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }


            // write the inputStream to a FileOutputStream
//            outputStream =
//                    new FileOutputStream(new File("/tmp/octopus.jpg"));
//
//            int read = 0;
//            byte[] bytes = new byte[1024];
//
//            while ((read = file.read(bytes)) != -1) {
//                outputStream.write(bytes, 0, read);
//            }
//
//            System.out.println("Done!");
//            return StatusConverter.convertStatus("OK");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (file != null) {
//                try {
//                    file.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (outputStream != null) {
//                try {
////                     outputStream.flush();
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
            return StatusConverter.convertStatus("FILE_UPLOAD_ERROR");


     }

}
