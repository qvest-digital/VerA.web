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
package org.evolvis.veraweb.onlinereg.fileupload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import lombok.extern.java.Log;

import org.evolvis.veraweb.onlinereg.Config;

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
import java.net.SocketTimeoutException;

/**
 * @author  by Jon Nuñez, tarent solutions GmbH on 29.06.15.
 */
@Path("/fileupload")
@Produces(MediaType.APPLICATION_JSON)
@Log
public class FileUploadResource {

    /** Jersey client */
    private Client client;

    /** Configuration */
    private Config config;

	/** Jackson Object Mapper */
	private final ObjectMapper mapper = new ObjectMapper();

    /** Base path of all resources. */
    private static final String BASE_RESOURCE = "/rest";

	/** Return types */
	private static final TypeReference<String> STRING = new TypeReference<String>() {};
    
    public FileUploadResource(Config config, Client client) {
        this.client = client;
        this.config = config;
    }

	@POST
	@Path("/save")
	public String saveTempImage(@FormParam("file") String imageString,
								@FormParam("imgUUID") String imgUUID) throws IOException {

		String extension = getImageType(imageString);
		String imageStringData = removeHeaderFromImage(imageString);

		uploadImage(imageStringData,extension,imgUUID);

		return StatusConverter.convertStatus("OK");
	}

	@GET
	@Path("/user/image/{delegationUUID}/{personId}")
	public String getImageUUIDByUser(@PathParam("delegationUUID") String delegationUUID,
									 @PathParam("personId") Integer personId) {

		WebResource resource = client.resource(path("guest","image", delegationUUID, personId));
		String imageUUID = null;

		try {
			imageUUID = resource.get(String.class);
		} catch (UniformInterfaceException e) {
			int statusCode = e.getResponse().getStatus();
			if(statusCode == 204) {
				return null;
			}

			return null;
		}

		return StatusConverter.convertStatus(imageUUID);
	}

	@GET
	@Path("/download/{imgUUID}")
	public String downloadGuestImage(@PathParam("imgUUID") String imgUUID) throws IOException {


		WebResource resource = client.resource(config.getVerawebEndpoint() + "/rest/fileupload/download")
				.queryParam("imgUUID", imgUUID).queryParam("filesLocation", config.getFilesLocation());

		String encodedImage = null;

		try {
			encodedImage = resource.get(String.class);
		} catch (UniformInterfaceException e) {
			int statusCode = e.getResponse().getStatus();
			if(statusCode == 204) {
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
		postBody.add("filesLocation", config.getFilesLocation());

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

	/**
	 * Reads the resource at given path and returns the entity.
	 *
	 * @param path path
	 * @param type TypeReference of requested entity
	 * @param <T>  Type of requested entity
	 * @return requested resource
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
				log.warning("Retrying request to " + path + " once because of SocketTimeoutException");
				resource = client.resource(path);
				final String json = resource.get(String.class);
				return mapper.readValue(json, type);
			} else {
				throw che;
			}
		} catch (UniformInterfaceException uie) {
			log.warning(uie.getResponse().getEntity(String.class));
			throw uie;
		}
	}
}
