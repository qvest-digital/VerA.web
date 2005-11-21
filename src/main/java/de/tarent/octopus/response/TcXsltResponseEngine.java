/* $Id: TcXsltResponseEngine.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.util.Xml;

/** 
 * Verarbeitung mit XSLT.
 * 
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcXsltResponseEngine implements TcResponseEngine {
    /** Der Logger */
    private static Logger logger = Logger.getLogger(TcXsltResponseEngine.class.getName());
    private static final String suffix = ".xsl";
    private static final String subdir = "xslt";

    /**
     * @see de.tarent.octopus.response.TcResponseEngine#sendResponse(TcConfig, TcResponse, TcContent, TcResponseDescription, TcRequest)
     */
    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {
        String name = (String) theContent.getAsObject("responseParams.name");
        File templateFile = new File(new File(config.getTemplateRootPath(), subdir), desc.getDescName() + suffix);

        // Nach einem gleichnamigen Template in dem Default Modul suchen
        if (!templateFile.exists()) {

            TcCommonConfig cc = config.getCommonConfig();
            File defaultTemplateFile =
                new File(new File(cc.getTemplateRootPath(cc.getDefaultModuleName()), subdir), desc.getDescName() + suffix);
            if (defaultTemplateFile.exists())
                templateFile = defaultTemplateFile;
            else
                throw new ResponseProcessingException(Resources.getInstance().get("XSLTRESPONSE_EXC_NO_TEMPLATE", templateFile, defaultTemplateFile));
        }

        String xmlDef = (String) theContent.getAsObject(name);
        if (xmlDef == null)
            xmlDef = Resources.getInstance().get("XSLTRESPONSE_XML_DEFAULT_SOURCE");
        try {
            Transformer transformer = Xml.getXSLTTransformer(new StreamSource(templateFile));
            Iterator e = theContent.getKeys();
            while (e.hasNext()) {
                String key = (String) e.next();
                putXSLTParameter(transformer, key, theContent.get(key));
            }
            transformer.transform(new StreamSource(new StringReader(xmlDef)), new StreamResult(tcResponse.getWriter()));
        } catch (TransformerException e) {
            logger.log(Level.WARNING, Resources.getInstance().get("XSLTRESPONSE_LOG_TRANSFORM_EXCEPTION", request.getRequestID(), templateFile), e);
        }
    }

    private void putXSLTParameter(Transformer transformer, String name, Object value) {
        if (name == null)
            name = "";
        if (value instanceof Map) {
            if (name.length() > 0)
                name += '.';
            Iterator mapIt = ((Map)value).entrySet().iterator();
            while (mapIt.hasNext()) {
                Map.Entry entry = (Map.Entry) mapIt.next();
                putXSLTParameter(transformer, name + entry.getKey(), entry.getValue());
            }
        } else if (value instanceof List) {
            if (name.length() > 0)
                name += '.';
            Iterator listIt = ((List)value).iterator();
            for (int i = 0; listIt.hasNext(); i++)
                putXSLTParameter(transformer, name + i, listIt.next());
        } else
            transformer.setParameter(name, value);
    }

    /**
     * @see de.tarent.octopus.response.TcResponseEngine#init(TcModuleConfig, TcCommonConfig)
     */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }
}
