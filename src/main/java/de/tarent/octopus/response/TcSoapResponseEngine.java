/* $Id: TcSoapResponseEngine.java,v 1.3 2006/11/23 14:33:30 schmitz Exp $
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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.soap.RPCResponse;
import de.tarent.octopus.soap.TcSOAPEngine;
import de.tarent.octopus.soap.TcSOAPException;

/** 
 *  Diese Klasse gibt die ausgewählten Felder eines TcContent Objekte als SOAP Nachricht zurück.
 *
 *  @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcSoapResponseEngine implements TcRPCResponseEngine, TcResponseEngine {
    /** Der Logger */
    private static Log logger = LogFactory.getLog(TcCommonConfig.class);

    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
    }

    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc, TcRequest request)
        throws ResponseProcessingException {

        TcSOAPEngine engine = tcResponse.getSoapEngine();

        // Wenn ein ContentType angegeben wurde, 
        // wurde dieser bereits im TcRequestCreator gesetzt.
        // Ansonsten wird text/xml für SOAP gesetzt.
        String contentType = theContent.getAsString("responseParams.ContentType");
        if (contentType == null)
            tcResponse.setContentType("text/xml");

        String namespace = theContent.getAsString("responseParams.Namespace");
        if (namespace == null)
            namespace = engine.getNamespaceByModuleName(tcResponse.getModuleName());
        
        String callingMethodName = tcResponse.getTaskName();
        RPCResponse rpcResponse = new RPCResponse(engine, namespace, callingMethodName);

        try {
            Map outputFields = TcResponseCreator.refineOutputFields(theContent.getAsObject(RPC_RESPONSE_OUTPUT_FIELDS));
            for (Iterator iter = outputFields.keySet().iterator(); iter.hasNext();) {
                String fieldNameOutput = (String)iter.next();
                String fieldNameContent = (String)outputFields.get(fieldNameOutput);
                rpcResponse.addParam(fieldNameOutput, theContent.getAsObject(fieldNameContent));
            } 
        
            // Geänder um auch ein Mapping der Parameternamen zu ermöglichen.
            //         Vector outputFields = TcResponseCreator.refineOutputFields(theContent.getAsObject(RPC_RESPONSE_OUTPUT_FIELDS));
            //         for (int i = 0; i < outputFields.size(); i++) {
            //             String fieldName = (String) outputFields.get(i);
            //             rpcResponse.addParam(fieldName, theContent.getAsObject(fieldName));
            //         }
            
            logger.debug("Gebe SOAP Message aus. Antwort auf Methode:" + callingMethodName);
            rpcResponse.writeTo(tcResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("Versuche, eine SOAP-Fault auszugeben.",
                e);
            TcSOAPException soapException = new TcSOAPException(e);
            try {
                soapException.writeTo(tcResponse.getOutputStream());
            } catch (Exception e2) {
                logger.error("Es konnte auch keine SOAP Fehlermeldung ausgegeben werden. Schmeiße jetzt einfach eine Exception.",
                    e2);
                throw new ResponseProcessingException("Es ist Fehler bei der Formatierung der Ausgabe ausgetreten.", e);
            }
        }
    }
    
//     public static List refineOutputFields(Object fieldsObject) {
//         if (fieldsObject instanceof List)
//             return (List) fieldsObject;
//         else {
//             List fields = new ArrayList();
//             if (fieldsObject != null && fieldsObject.toString().length() != 0)
//                 fields.add(fieldsObject);
//             return fields;
//         }
//     }
}
