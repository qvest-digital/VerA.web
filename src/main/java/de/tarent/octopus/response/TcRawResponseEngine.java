/* $Id: TcRawResponseEngine.java,v 1.2 2006/11/23 14:33:30 schmitz Exp $
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
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

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
