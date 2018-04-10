package de.tarent.octopus.response;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
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
    /**
     * Param for a map response, must contain the filename.
     */
    public static final String PARAM_FILENAME = "filename";
    /**
     * Param for a map response, may cotain a last modified header date (as string).
     */
    // TODO Diese Header Information koennte auch direkt hier aus dem Header gelesen werden.
    public static final String PARAM_FILEDATE = "date";
    /**
     * Param for a map response, may contain the content-type.
     */
    public static final String PARAM_MIMETYPE = "mimetype";
    /**
     * Param for a map response, must contain a readable stream if type is stream.
     */
    public static final String PARAM_STREAM = "stream";
    /**
     * Param for a map response, may contain <code>true</code> if this is a download.
     */
    public static final String PARAM_IS_ATTACHMENT = "isAttachment";
    /**
     * Param for a map response, may contain the name suggested for the downloaded file, if it is an attachment. As default,
     * the local name of the PARAM_FILENAME will be used.
     */
    public static final String PARAM_ATTACHMENT_FILENAME = "attachmentFilename";

    /**
     * Param for a map response, may contain a Runnable, that is executed, when the BinaryResponse succeeds,
     * meaning that writing data to the respone's outputstream finished without any IOExceptions.
     */
    public static final String PARAM_RUNNABLE_SUCCEED = "succeededRunnable";

    /**
     * Param for a map response, may contain a Runnable, that is executed, when the BinaryResponse fails.
     * i.e. the user cancels the download, or other network problems occur and an IOException is thrown.
     */
    public static final String PARAM_RUNNABLE_FAIL = "failedRunnable";

    /**
     * Value for {@link #PARAM_TYPE}, will return a stream.
     */
    public static final String BINARY_RESPONSE_TYPE_STREAM = "stream";
    /**
     * Value for {@link #PARAM_TYPE}, will return a local file.
     */
    public static final String BINARY_RESPONSE_TYPE_LOCALFILE = "localfile";

    /**
     * HTTP header timestamp for the last modification date.
     */
    public static final DateFormat HTTP_DEFAULT_TIMESTAMP = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    /**
     * HTTP header content type for unknown content types.
     */
    public static final String HTTP_DETAULT_MIMETYPE = "application/octet-stream";

    /**
     * java util logger
     */
    private Log logger = LogFactory.getLog(TcCommonConfig.class);

    /**
     * @see TcResponseEngine#init(TcModuleConfig, TcCommonConfig)
     */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }

    /**
     * @see TcResponseEngine#sendResponse(TcConfig, TcResponse, TcContent, TcResponseDescription, TcRequest)
     */
    public void sendResponse(TcConfig tcConfig, TcResponse tcResponse, TcContent tcContent, TcResponseDescription desc,
            TcRequest tcRequest)
            throws ResponseProcessingException {

        try {
            Object data = tcContent.get(desc.getDescName());

            if (data instanceof File) {
                File file = (File) data;
                tcResponse.setContentType(getContentString(file.getName(), null));
                processFile(tcRequest, tcResponse, file, file.getName(), null);
            } else if (data instanceof String) {
                File file = new File((String) data);
                tcResponse.setContentType(getContentString(file.getName(), null));
                processFile(tcRequest, tcResponse, file, file.getName(), null);
            } else if (data instanceof Map) {
                Map info = (Map) data;
                String type = (String) info.get(PARAM_TYPE);
                String filename = (String) info.get(PARAM_FILENAME);
                String filedate = (String) info.get(PARAM_FILEDATE);
                String mimetype = (String) info.get(PARAM_MIMETYPE);
                Runnable succeededRunnable = (Runnable) info.get(PARAM_RUNNABLE_SUCCEED);
                Runnable failedRunnable = (Runnable) info.get(PARAM_RUNNABLE_FAIL);

                String attachmentFilename = (String) info.get(PARAM_ATTACHMENT_FILENAME);
                if (attachmentFilename == null && filename != null) {
                    attachmentFilename = new File(filename).getName();
                }

                if (isTrue(info.get(PARAM_IS_ATTACHMENT))) {
                    tcResponse.setHeader("Content-Disposition",
                            "attachment" + (attachmentFilename != null ? "; filename=\"" + attachmentFilename + "\"" : ""));
                }
                try {
                    tcResponse.setContentType(getContentString(attachmentFilename, mimetype));
                    if (BINARY_RESPONSE_TYPE_STREAM.equals(type)) {
                        processStream(tcResponse, info.get(PARAM_STREAM));
                    } else if (BINARY_RESPONSE_TYPE_LOCALFILE.equals(type)) {
                        File file = new File(filename);
                        processFile(tcRequest, tcResponse, file, attachmentFilename, filedate);
                    }
                } catch (IOException ioe) {
                    // if an IOExcption occurs, the response failed.
                    if (failedRunnable != null) {
                        failedRunnable.run();
                    }
                    throw ioe;
                }
                // when arriving here, the response succeeded.
                if (succeededRunnable != null) {
                    succeededRunnable.run();
                }

            } else {
                tcResponse.setContentType(getContentString(null, null));
                processStream(tcResponse, data);
            }
        } catch (Exception e) {
            logger.error(Resources.getInstance().get("BINARYRESPONSE_LOG_ERROR", tcRequest.getRequestID()), e);
            throw new ResponseProcessingException(Resources.getInstance().get("BINARYRESPONSE_EXC_ERROR"), e);
        } finally {
            try {
                tcResponse.close();
            } catch (IOException e) {
                logger.error(Resources.getInstance().get("BINARYRESPONSE_LOG_ERROR", tcRequest.getRequestID()), e);
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
    protected void processFile(TcRequest tcRequest, TcResponse tcResponse, File file, String filename, String filedate)
            throws IOException, ParseException {
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
                logger.debug(Resources.getInstance().get("BINARYRESPONSE_LOG_FILE_HANDLED", tcRequest.getRequestID(), filename));
            } else {
                // return file not modified
                tcResponse.setHeader("Pragma", null);
                tcResponse.setHeader("Date", dateFileString);
                tcResponse.setHeader("Last-Modified", dateFileString);
                tcResponse.setStatus(304);
                logger.debug(Resources.getInstance().get("BINARYRESPONSE_LOG_NOT_MODIFIED", tcRequest.getRequestID(), filename));
            }
        } else {
            // if no file found return http status 404
            tcResponse.setStatus(404);
            logger.warn(Resources.getInstance().get("BINARYRESPONSE_LOG_NOT_FOUND", tcRequest.getRequestID(), filename));
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
     * @throws ClassCastException   If input class is not supported.
     */
    protected void processStream(TcResponse tcResponse, Object input) throws IOException {
        tcResponse.flush();
        if (input instanceof InputStream) {
            InputStream is = (InputStream) input;
            OutputStream os = tcResponse.getOutputStream();
            int size = 0;
            byte b[] = new byte[1024];
            while (size != -1) {
                size = is.read(b);
                if (size != -1) {
                    os.write(b, 0, size);
                }
            }
            is.close();
            os.close();
        } else if (input instanceof Reader) {
            Reader r = (Reader) input;
            PrintWriter w = tcResponse.getWriter();
            int size = 0;
            char c[] = new char[1024];
            while (size != -1) {
                size = r.read(c);
                if (size != -1) {
                    w.write(c, 0, size);
                }
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
     * mime/type
     * mime/type; name="filename.txt"
     *
     * default:
     * {@link #HTTP_DETAULT_MIMETYPE} without filename
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
     * @return MIME type
     */
    protected String getMimeType(String filename) {
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
