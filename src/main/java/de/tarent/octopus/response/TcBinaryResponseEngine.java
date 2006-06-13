/* $Id: TcBinaryResponseEngine.java,v 1.4 2006/06/13 10:16:58 asteban Exp $
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
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

/**
 * This class return a stream from the filesystem or the octopus context.
 * 
 * The parameter must be defined in an map whose key context-key is
 * defined as the name attribute of the repoonse tag.
 * 
 * The input type must be a {@link Map} (with the <code>PARAM_*</code> keys,
 * a {@link File}, a {@link String} (which point on a file), an
 * {@link InputStream} or a {@link Reader}.
 * 
 * @author <a href="mailto:c.jerolimov@tarent.de">Christoph Jerolimov</a>, tarent GmbH
 */
public class TcBinaryResponseEngine implements TcResponseEngine {
	/**
	 * Param for a map response, define the response type, see
	 * {@link #BINARY_RESPONSE_TYPE_STREAM} and
	 * {@link #BINARY_RESPONSE_TYPE_LOCALFILE}.
	 */
	public static final String PARAM_TYPE = "type";
	/** Param for a map response, must contain the filename. */
	public static final String PARAM_FILENAME = "filename";
	/** Param for a map response, may cotain a last modified header date (as string). */
	// TODO Diese Header Information koennte auch direkt hier aus dem Header gelesen werden.
	public static final String PARAM_FILEDATE = "date";
	/** Param for a map response, may contain the content-type. */
	public static final String PARAM_MIMETYPE = "mimetype";
	/** Param for a map response, must contain a readable stream if type is stream. */
	public static final String PARAM_STREAM = "stream";
	/** Param for a map response, may contain <code>true</code> if this is a download. */
	public static final String PARAM_IS_ATTACHMENT = "isAttachment";
	/** Param for a map response, may contain the name suggested for the downloaded file, if it is an attachment. As default, the local name of the PARAM_FILENAME will be used.*/
	public static final String PARAM_ATTACHMENT_FILENAME = "attachmentFilename";

	/** Value for {@link #PARAM_TYPE}, will return a stream. */
	public static final String BINARY_RESPONSE_TYPE_STREAM = "stream";
	/** Value for {@link #PARAM_TYPE}, will return a local file. */
	public static final String BINARY_RESPONSE_TYPE_LOCALFILE = "localfile";

	/** HTTP header timestamp for the last modification date. */
	public static final DateFormat HTTP_DEFAULT_TIMESTAMP = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	/** HTTP header content type for unknown content types. */
	public static final String HTTP_DETAULT_MIMETYPE = "application/octet-stream";

	/** java util logger */
	private Logger logger = Logger.getLogger(TcCommonConfig.class.getName());

	/**
	 * @see TcResponseEngine#init(TcModuleConfig, TcCommonConfig)
	 */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }

	/**
	 * @see TcResponseEngine#sendResponse(TcConfig, TcResponse, TcContent, TcResponseDescription, TcRequest)
	 */
    public void sendResponse(TcConfig tcConfig, TcResponse tcResponse, TcContent tcContent, TcResponseDescription desc, TcRequest tcRequest)
        	throws ResponseProcessingException {
    	
        try {
        	Object data = tcContent.get(desc.getDescName());
        	
            if (data instanceof File) {
            	File file = (File)data;
            	tcResponse.setContentType(getContentString(file.getName(), null));
            	processFile(tcRequest, tcResponse, file, file.getName(), null);
            } else if (data instanceof String) {
            	File file = new File((String)data);
            	tcResponse.setContentType(getContentString(file.getName(), null));
            	processFile(tcRequest, tcResponse, file, file.getName(), null);
            } else if (data instanceof Map) {
            	Map info = (Map)data;
            	String type = (String)info.get(PARAM_TYPE);
				String filename = (String)info.get(PARAM_FILENAME);
				String filedate = (String)info.get(PARAM_FILEDATE);
				String mimetype = (String)info.get(PARAM_MIMETYPE);

				String attachmentFilename = (String)info.get(PARAM_ATTACHMENT_FILENAME);
                if (attachmentFilename == null && filename != null) {
                    attachmentFilename = new File(filename).getName();
                }
            	
            	if (isTrue(info.get(PARAM_IS_ATTACHMENT))) {
            		tcResponse.setHeader("Content-Disposition", "attachment" + (attachmentFilename != null ? "; filename=\"" + attachmentFilename + "\"" : ""));
            	}
            	
                tcResponse.setContentType(getContentString(attachmentFilename, mimetype));
	            if (BINARY_RESPONSE_TYPE_STREAM.equals(type)) {
					processStream(tcResponse, info.get(PARAM_STREAM));
	            } else if (BINARY_RESPONSE_TYPE_LOCALFILE.equals(type)) {
	            	File file = new File(filename);
	            	processFile(tcRequest, tcResponse, file, attachmentFilename, filedate);
	            }
            } else {
            	tcResponse.setContentType(getContentString(null, null));
            	processStream(tcResponse, data);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, Resources.getInstance().get("BINARYRESPONSE_LOG_ERROR", tcRequest.getRequestID()), e);
            throw new ResponseProcessingException(Resources.getInstance().get("BINARYRESPONSE_EXC_ERROR"), e);
        } finally {
        	try {
				tcResponse.close();
			} catch (IOException e) {
	            logger.log(Level.SEVERE, Resources.getInstance().get("BINARYRESPONSE_LOG_ERROR", tcRequest.getRequestID()), e);
			}
        }
    }

    /**
     * Process a file stream.
     * 
     * @param tcRequest
     * @param tcResponse
     * @param file
     * @param filename
     * @param filedate
     * @throws IOException
     * @throws ParseException
     */
    protected void processFile(TcRequest tcRequest, TcResponse tcResponse, File file, String filename, String filedate) throws IOException, ParseException {
		if (file.exists() && file.isFile()) {
			// send only if file was modified
			Date dateFileIsModified = new Date(file.lastModified() / 1000 * 1000);
			Date dateBrowserSend = (filedate == null ? null : HTTP_DEFAULT_TIMESTAMP.parse(filedate));
			String dateFileString = HTTP_DEFAULT_TIMESTAMP.format(dateFileIsModified);
			
			if (dateBrowserSend == null || dateFileIsModified.after(dateBrowserSend)) {
				// set header
				tcResponse.setHeader("Pragma", null);
				tcResponse.setHeader("Date", dateFileString);
				tcResponse.setHeader("Last-Modified", dateFileString);
				tcResponse.setHeader("Content-Length", String.valueOf(file.length()));
				tcResponse.setContentType(getContentString(file.getName(), null));
				
				// return stream
				processStream(tcResponse, new FileInputStream(file));
				logger.finer(Resources.getInstance().get("BINARYRESPONSE_LOG_FILE_HANDLED", tcRequest.getRequestID(), filename));
			} else {
				// return file not modified
				tcResponse.setHeader("Pragma", null);
				tcResponse.setHeader("Date", dateFileString);
				tcResponse.setHeader("Last-Modified", dateFileString);
				tcResponse.setStatus(304);
				logger.finer(Resources.getInstance().get("BINARYRESPONSE_LOG_NOT_MODIFIED", tcRequest.getRequestID(), filename));
			}
        } else {
            // if no file found return http status 404
	        tcResponse.setStatus(404);
    	    logger.warning(Resources.getInstance().get("BINARYRESPONSE_LOG_NOT_FOUND", tcRequest.getRequestID(), filename));
    	}
    }

    /**
     * Pipe the real data stream to the reponse. At the moment it can handle
     * {@link InputStream} and {@link Reader} instances.
     * 
     * @param tcResponse
     * @param input
     * @throws IOException
     * @throws NullPointerException If input is null.
     * @throws ClassCastException If input class is not supported.
     */
	protected void processStream(TcResponse tcResponse, Object input) throws IOException {
		tcResponse.flush();
		if (input instanceof InputStream) {
			InputStream is = (InputStream)input;
			OutputStream os = tcResponse.getOutputStream();
			int size = 0;
			byte b[] = new byte[1024];
			while (size != -1) {
				size = is.read(b);
				if (size != -1)
					os.write(b, 0, size);
			}
			is.close();
			os.close();
		} else if (input instanceof Reader) {
			Reader r = (Reader)input;
			PrintWriter w = tcResponse.getWriter();
			int size = 0;
			char c[] = new char[1024];
			while (size != -1) {
				size = r.read(c);
				if (size != -1)
					w.write(c, 0, size);
			}
			r.close();
			w.close();
		} else if (input == null) {
			throw new NullPointerException();
		} else {
			throw new ClassCastException();
		}
	}

	/**
	 * Create a http-like contenttype from the filename and mimetype.
	 * 
	 * format:
	 *   mime/type
	 *   mime/type; name="filename.txt"
	 * 
	 * default:
	 *   {@link #HTTP_DETAULT_MIMETYPE} without filename
	 */
	private String getContentString(String filename, String mimetype) {
		if (filename == null && mimetype == null) {
			return HTTP_DETAULT_MIMETYPE;
		} else if (filename == null) {
			return mimetype;
		} else if (mimetype == null) {
			return getMimeType(filename) + "; name=\"" + filename + "\"";
		} else {
			return mimetype + "; name=\"" + filename + "\"";
		}
	}

	/**
	 * Create some standard file types.
	 * 
	 * @param filename
	 * @return
	 */
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
		return HTTP_DETAULT_MIMETYPE;
	}

	/**
	 * Return true if the Parameter is "true".
	 * 
	 * @param o
	 * @return
	 */
	private boolean isTrue(Object o) {
		return o != null && o.toString().equals("true");
	}
}
