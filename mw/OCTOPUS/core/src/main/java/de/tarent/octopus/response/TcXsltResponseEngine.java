package de.tarent.octopus.response;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.util.Xml;
import lombok.extern.log4j.Log4j2;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Verarbeitung mit XSLT.
 *
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
@Log4j2
public class TcXsltResponseEngine implements TcResponseEngine {
    private static final String suffix = ".xsl";
    private static final String subdir = "xslt";

    /**
     * @see de.tarent.octopus.response.TcResponseEngine#sendResponse(TcConfig, TcResponse, TcContent, TcResponseDescription,
     * TcRequest)
     */
    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc,
      TcRequest request)
      throws ResponseProcessingException {
        String name = (String) theContent.getAsObject("responseParams.name");
        File templateFile = new File(new File(config.getTemplateRootPath(), subdir), desc.getDescName() + suffix);

        // Nach einem gleichnamigen Template in dem Default Modul suchen
        if (!templateFile.exists()) {

            TcCommonConfig cc = config.getCommonConfig();
            File defaultTemplateFile =
              new File(new File(cc.getTemplateRootPath(cc.getDefaultModuleName()), subdir), desc.getDescName() + suffix);
            if (defaultTemplateFile.exists()) {
                templateFile = defaultTemplateFile;
            } else {
                throw new ResponseProcessingException(
                  Resources.getInstance().get("XSLTRESPONSE_EXC_NO_TEMPLATE", templateFile, defaultTemplateFile));
            }
        }

        String xmlDef = (String) theContent.getAsObject(name);
        if (xmlDef == null) {
            xmlDef = Resources.getInstance().get("XSLTRESPONSE_XML_DEFAULT_SOURCE");
        }
        try {
            Transformer transformer = Xml.getXSLTTransformer(new StreamSource(templateFile));
            Iterator e = theContent.getKeys();
            while (e.hasNext()) {
                String key = (String) e.next();
                putXSLTParameter(transformer, key, theContent.get(key));
            }
            transformer.transform(new StreamSource(new StringReader(xmlDef)), new StreamResult(tcResponse.getWriter()));
        } catch (TransformerException e) {
            logger.warn(Resources.getInstance().get("XSLTRESPONSE_LOG_TRANSFORM_EXCEPTION", request.getRequestID(), templateFile),
              e);
        }
    }

    private void putXSLTParameter(Transformer transformer, String name, Object value) {
        if (name == null) {
            name = "";
        }
        if (value instanceof Map) {
            if (name.length() > 0) {
                name += '.';
            }
            Iterator mapIt = ((Map) value).entrySet().iterator();
            while (mapIt.hasNext()) {
                Map.Entry entry = (Map.Entry) mapIt.next();
                putXSLTParameter(transformer, name + entry.getKey(), entry.getValue());
            }
        } else if (value instanceof List) {
            if (name.length() > 0) {
                name += '.';
            }
            Iterator listIt = ((List) value).iterator();
            for (int i = 0; listIt.hasNext(); i++) {
                putXSLTParameter(transformer, name + i, listIt.next());
            }
        } else {
            transformer.setParameter(name, value);
        }
    }

    /**
     * @see de.tarent.octopus.response.TcResponseEngine#init(TcModuleConfig, TcCommonConfig)
     */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }
}
