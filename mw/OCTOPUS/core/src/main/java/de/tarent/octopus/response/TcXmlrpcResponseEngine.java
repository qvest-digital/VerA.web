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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.xmlrpc.XmlRpcBuffer;

/**
 * Diese Klasse dient der Ausgabe von XML-RPC-Rückgaben.
 *
 * TODO: Test der XmlRpc-Fähigkeiten
 *
 * @author mikel
 */
public class TcXmlrpcResponseEngine implements TcRPCResponseEngine, TcResponseEngine {
    //
    // Membervariablen
    //
    private static Log logger = LogFactory.getLog(TcXmlrpcResponseEngine.class);

    /* (non-Javadoc)
     * @see de.tarent.octopus.response.TcResponseEngine#sendResponse(de.tarent.octopus.config.TcConfig, de.tarent.octopus
     * .request.TcResponse, de.tarent.octopus.content.TcContent, de.tarent.octopus.response.TcResponseDescription, java.lang
     * .String)
     */
    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc,
            TcRequest request) throws ResponseProcessingException {
        String contentType = theContent.getAsString("responseParams.ContentType");
        if (contentType == null) {
            tcResponse.setContentType("text/xml");
        }

        // Geänder um auch ein Mapping der Parameternamen zu ermöglichen.
        //         Object outputFieldsObject = theContent.getAsObject(RPC_RESPONSE_OUTPUT_FIELDS);
        //         Object result = null;
        //         if (outputFieldsObject instanceof List) {
        //             List outputFields = (List) outputFieldsObject;
        //             List resultList = new ArrayList(outputFields.size());
        //             Iterator itFields = outputFields.iterator();
        //             while (itFields.hasNext()) {
        //                 Object field = theContent.getAsObject(itFields.next().toString());
        //                 resultList.add(field != null ? field : "");
        //             }
        //             result = resultList;
        //         } else if (outputFieldsObject != null)
        //             result = theContent.getAsObject(outputFieldsObject.toString());

        Map outputFields = TcResponseCreator.refineOutputFields(theContent.getAsObject(RPC_RESPONSE_OUTPUT_FIELDS));
        List resultList = new ArrayList(outputFields.size());
        for (Iterator iter = outputFields.keySet().iterator(); iter.hasNext(); ) {
            // Field Name wird hier ignoriert. Warum?
            // Gibt es bei XmlRPC keine Benennung der return Parameter?
            String fieldNameOutput = (String) iter.next();
            String fieldNameContent = (String) outputFields.get(fieldNameOutput);
            Object fieldData = theContent.getAsObject(fieldNameContent);
            resultList.add(fieldData != null ? fieldData : "");
        }

        // TODO Auto-generated method stub

        try {
            logger.debug("Gebe XML-RPC-Message aus. Antwort auf Methode:" + tcResponse.getTaskName());
            XmlRpcBuffer buffer = new XmlRpcBuffer();
            buffer.appendResponse(resultList);
            ByteBuffer byteBuffer = buffer.toByteBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            tcResponse.getOutputStream().write(bytes);
        } catch (Exception e) {
            logger.error("Versuche, eine XML-RPC-Fault auszugeben.",
                    e);
            try {
                XmlRpcBuffer buffer = new XmlRpcBuffer();
                buffer.appendError(0, "Fehler beim Schreiben der SOAP-Antwort: " + e.getMessage());
                ByteBuffer byteBuffer = buffer.toByteBuffer();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                tcResponse.getOutputStream().write(bytes);
            } catch (Exception e2) {
                logger.error(
                        "Es konnte auch keine XML-RPC Fehlermeldung ausgegeben werden. Schmeiße jetzt einfach eine Exception.",
                        e2);
                throw new ResponseProcessingException("Es ist Fehler bei der Formatierung der Ausgabe ausgetreten.", e);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.response.TcResponseEngine#init(de.tarent.octopus.config.TcModuleConfig, de.tarent.octopus.config
     * .TcCommonConfig)
     */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
        // TODO Auto-generated method stub
    }
}
