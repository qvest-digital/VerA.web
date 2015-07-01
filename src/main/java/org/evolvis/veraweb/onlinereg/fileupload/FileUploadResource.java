package org.evolvis.veraweb.onlinereg.fileupload;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.jersey.api.client.Client;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MultipartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import lombok.extern.java.Log;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import org.evolvis.veraweb.onlinereg.Config;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

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
     @Consumes(MediaType.MULTIPART_FORM_DATA)
     @Path("/save")
     public void saveTempImage(@FormDataParam("file") InputStream file) throws IOException {

     OutputStream outputStream = null;
        try {

            // write the inputStream to a FileOutputStream
            outputStream =
                    new FileOutputStream(new File("/tmp/octopus.jpg"));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = file.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            System.out.println("Done!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
//                     outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }


}
