package de.tarent.octopus.response;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
 *  Diese Klasse arbeitet als direkte Ausgabe Engine.
 *
 *  @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcRawResponseEngine implements TcResponseEngine {
    /** Der Logger */
    private static Log logger = LogFactory.getLog(TcRawResponseEngine.class);

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }

    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {
        String encoding = (String) theContent.getAsObject("responseParams.encoding");
        if (encoding == null || encoding.length() == 0)
            encoding = config.getDefaultEncoding();
        PrintWriter outWriter = null;
        try {
            outWriter = new PrintWriter(new OutputStreamWriter(tcResponse.getOutputStream(), encoding), true);
            logger.debug(Resources.getInstance().get("RAWRESPONSE_LOG_ENCODING", request.getRequestID(), encoding));
        } catch (UnsupportedEncodingException e) {
            logger.warn(Resources.getInstance().get("RAWRESPONSE_LOG_ENCODING_UNSUPPORTED", request.getRequestID(), encoding), e);
            outWriter = tcResponse.getWriter();
        }
        String outputFieldName = desc.getDescName();
        outWriter.print(theContent.getAsString(outputFieldName));
        outWriter.flush();
        outWriter.close();
    }
}
