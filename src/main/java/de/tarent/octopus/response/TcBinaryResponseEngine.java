/* $Id: TcBinaryResponseEngine.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Christoph Jerolimov.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;

/** Diese Klasse gibt eine Datei aus dem Filesystem zurück.
 * 
 * Der Dateiname muss im Content hinterlegt werden.
 * Hinter welchem Key dieser gespeichert ist muss dann beim
 * Response-Tag in der Config-XML-Datei dann als name
 * angegeben werden.
 *
 * @author Christoph Jerolimov, <b>tarent GmbH</b>
 */
public class TcBinaryResponseEngine implements TcResponseEngine {
    private static Logger logger = Logger.getLogger(TcCommonConfig.class.getName());

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }

    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent tcContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {

        try {
        	int size = 0;
        	byte[] buffer = new byte[1024];
        	
            Map info = (Map)tcContent.get(desc.getDescName());
            String type = (String)info.get("type");
            
            if (BINARY_RESPONSE_TYPE_STREAM.equals(type)) {
				String filename = (String)info.get("filename");
				String mimetype = (String)info.get("mimetype");

            	// Header setzten
				tcResponse.setContentType(getContentString(filename, mimetype));

				// Zurückgeben des Streams
				InputStream input = (InputStream)info.get("stream");
				OutputStream output = tcResponse.getOutputStream();
				while (size != -1) {
					size = input.read(buffer);
					if (size != -1)
						output.write(buffer, 0, size);
				}
				output.flush();
				output.close();
				input.close();
				input = null;
				logger.finer(Resources.getInstance().get("BINARYRESPONSE_LOG_FILE_HANDLED", request.getRequestID()));

            } else if (BINARY_RESPONSE_TYPE_LOCALFILE.equals(type)) {
				String filename = (String)info.get("filename");
				String filedate = (String)info.get("date");

				File file = new File(filename);
				if (file.exists() && file.isFile()) {
					// Überprüfen ob Datei geändert wurden ist und nur dann senden.
					SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
					Date dateFileIsModified = new Date(file.lastModified() / 1000 * 1000);
					Date dateBrowserSend = (filedate == null ? null : df.parse(filedate));
					String dateFileString = df.format(dateFileIsModified);

					if (dateBrowserSend == null || dateFileIsModified.after(dateBrowserSend)) {
						// Header setzten
						tcResponse.setHeader("Pragma", null);
						tcResponse.setHeader("Date", dateFileString);
						tcResponse.setHeader("Last-Modified", dateFileString);
						tcResponse.setHeader("Content-Length", String.valueOf(file.length()));
						tcResponse.setContentType(getContentString(file.getName(), null));

						// Zurückgeben der Datei
						InputStream input = new FileInputStream(file);
						OutputStream output = tcResponse.getOutputStream();
						while (size != -1) {
							size = input.read(buffer);
							if (size != -1)
								output.write(buffer, 0, size);
						}
						output.flush();
						output.close();
						input.close();
						input = null;
						logger.finer(Resources.getInstance().get("BINARYRESPONSE_LOG_FILE_HANDLED", request.getRequestID(), filename));
					} else {
						// Wenn Datei nicht gesendet werden muss, dies als HTTP-Response 304 (Not Modified) zurückgeben.
						tcResponse.setHeader("Pragma", null);
						tcResponse.setHeader("Date", dateFileString);
						tcResponse.setHeader("Last-Modified", dateFileString);
						tcResponse.setStatus(304);
						logger.finer(Resources.getInstance().get("BINARYRESPONSE_LOG_NOT_MODIFIED", request.getRequestID(), filename));
					}
	            } else {
    	            // Wenn Datei nicht vorhanden, dies als HTTP-Fehler 404 (File not found) zurückgeben.
        	        tcResponse.setStatus(404);
            	    logger.warning(Resources.getInstance().get("BINARYRESPONSE_LOG_NOT_FOUND", request.getRequestID(), filename));
            	}
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, Resources.getInstance().get("BINARYRESPONSE_LOG_ERROR", request.getRequestID()), e);
            throw new ResponseProcessingException(Resources.getInstance().get("BINARYRESPONSE_EXC_ERROR"), e);
        }
    }
	
	/**
	 * Generiert einen fertigen Content-String aus einem Dateinamen
	 * und einem MimeType.
	 * 
	 * Content-String-Format:
	 *   mime/type
	 *   mime/type; name="filename.txt"
	 * 
	 * Default:
	 *   MIMETYPE_DETAULT
	 *   ohne Dateiname
	 */
	private String getContentString(String filename, String mimetype) {
		if (filename == null && mimetype == null) {
			return MIMETYPE_DEFAULT;
		} else if (filename == null) {
			return mimetype;
		} else if (mimetype == null) {
			return getMimeType(filename) + "; name=\"" + filename + "\"";
		} else {
			return mimetype + "; name=\"" + filename + "\"";
		}
	}
	
	private String getMimeType(String filename) {
		if (filename.endsWith(".png")) {
			return "image/png";
		} else if (filename.endsWith(".gif")) {
			return "image/gif";
		} else if (filename.endsWith(".jpg")) {
			return "image/jpg";
		} else if (filename.endsWith(".txt")) {
			return "text/plain";
		} else if (filename.endsWith(".css")) {
			return "text/css";
		}
		return MIMETYPE_DEFAULT;
	}
	
	public static final String BINARY_RESPONSE_TYPE_STREAM = "stream";
	public static final String BINARY_RESPONSE_TYPE_LOCALFILE = "localfile";
	
	private static final String MIMETYPE_DEFAULT = "application/octet-stream";
}
