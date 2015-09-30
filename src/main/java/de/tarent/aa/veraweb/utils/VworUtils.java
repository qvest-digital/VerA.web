package de.tarent.aa.veraweb.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.UUID;

/**
 * Created by csalib on 29.09.15.
 */
public class VworUtils {

    final private static String BASE_RESOURCE = "/rest";

    /** Jackson Object mapper */
    private ObjectMapper mapper = new ObjectMapper();

    /** Jersey Client */
    final Client client = Client.create();

    /**
     * @return Path of the saved images of the guests
     * @throws IOException
     */
    public String getVworEndPoint() throws IOException {
        PropertiesReader propertiesReader = new PropertiesReader();
        String vwor = propertiesReader.getProperty("vwor.endpoint");
        return vwor;
    }

    /**
     * Method sending Requests to VWOR
     * @return Entities from the Vwor component
     * @throws IOException
     */
    private <T> T readResource(String path, TypeReference<T> type) throws IOException {
        WebResource resource;
        try {
            resource = client.resource(path);
            final String json = resource.get(String.class);
            return mapper.readValue(json, type);
        } catch (ClientHandlerException che) {
            if (che.getCause() instanceof SocketTimeoutException) {
                //FIXME some times open, pooled connections time out and generate errors
                resource = client.resource(path);
                final String json = resource.get(String.class);
                return mapper.readValue(json, type);
            } else {
                throw che;
            }
        } catch (UniformInterfaceException uie) {
            throw uie;
        }
    }

    public String getImageType(String imageString) {
        String imageHeader = imageString.substring(0, 15);
        if (imageHeader.contains("data:image/jpg")) {
            return "jpg";
        } else if (imageHeader.contains("data:image/jpeg")) {
            return "jpeg";
        } else if (imageHeader.contains("data:image/png")) {
            return "png";
        }

        return "ERROR_PARSING_IMAGE_TYPE";
    }

    public String removeHeaderFromImage(String imageString) {
        if (getImageType(imageString).equals("jpg") || getImageType(imageString).equals("png")) {
            return imageString.substring(22);
        }
        if (getImageType(imageString).equals("jpeg")) {
            return imageString.substring(23);
        }

        return "ERROR REMOVING HEADER FROM IMAGE";
    }

    /**
     * Constructs a path from VerA.web endpint, BASE_RESOURCE and given path fragmensts.
     *
     * @param path path fragments
     * @return complete path as string
     */
    public String path(Object... path) throws IOException {
        String r = getVworEndPoint() + BASE_RESOURCE;

        for (Object p : path) {
            r += "/" + p;
        }

        return r;
    }

    public String generateImageUUID() {
        UUID imageUUID = UUID.randomUUID();
        return imageUUID.toString();
    }

}
