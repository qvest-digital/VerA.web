package de.tarent.octopus.response;

/*-
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
import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
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
    private static Log logger = LogFactory.getLog(TcXsltResponseEngine.class);
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
            logger.warn(Resources.getInstance().get("XSLTRESPONSE_LOG_TRANSFORM_EXCEPTION", request.getRequestID(), templateFile), e);
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
