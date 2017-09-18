package de.tarent.octopus.response;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
