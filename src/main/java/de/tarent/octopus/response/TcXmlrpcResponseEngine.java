/*
 * $Id: TcXmlrpcResponseEngine.java,v 1.2 2006/11/23 14:33:30 schmitz Exp $
 * 
 * Created on 02.06.2004
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
 * by Michael Klink.
 * 
 * signature of Elmar Geese, 1 June 2002
 * 
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.response;

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
     * @see de.tarent.octopus.response.TcResponseEngine#sendResponse(de.tarent.octopus.config.TcConfig, de.tarent.octopus.request.TcResponse, de.tarent.octopus.content.TcContent, de.tarent.octopus.response.TcResponseDescription, java.lang.String)
     */
    public void sendResponse(TcConfig config, TcResponse tcResponse, TcContent theContent, TcResponseDescription desc,
            TcRequest request) throws ResponseProcessingException {
        String contentType = theContent.getAsString("responseParams.ContentType");
        if (contentType == null)
            tcResponse.setContentType("text/xml");


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
        for (Iterator iter = outputFields.keySet().iterator(); iter.hasNext();) {
            // Field Name wird hier ignoriert. Warum?
            // Gibt es bei XmlRPC keine Benennung der return Parameter?
            String fieldNameOutput = (String)iter.next();
            String fieldNameContent = (String)outputFields.get(fieldNameOutput);
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
                logger.error("Es konnte auch keine XML-RPC Fehlermeldung ausgegeben werden. Schmeiße jetzt einfach eine Exception.",
                    e2);
                throw new ResponseProcessingException("Es ist Fehler bei der Formatierung der Ausgabe ausgetreten.", e);
            }
        }
    }
    /* (non-Javadoc)
     * @see de.tarent.octopus.response.TcResponseEngine#init(de.tarent.octopus.config.TcModuleConfig, de.tarent.octopus.config.TcCommonConfig)
     */
    public void init(TcModuleConfig moduleConfig, TcCommonConfig commonConfig) {
        // TODO Auto-generated method stub
    }
    

}
