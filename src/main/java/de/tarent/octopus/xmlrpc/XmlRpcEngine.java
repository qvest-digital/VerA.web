/* $Id: XmlRpcEngine.java,v 1.2 2006/11/23 14:33:30 schmitz Exp $
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

package de.tarent.octopus.xmlrpc;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcRequestProcessor;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.soap.TcSOAPException;

/**
 * Diese Klasse dient dem Auslesen von XmlRpc-Anfragen. 
 * 
 * @author mikel
 */
public class XmlRpcEngine {

    /** Logger für diese Klasse */
    private static Log logger = LogFactory.getLog(XmlRpcEngine.class);


    /**
     * Diese Methode analysiert eine XML-RPC-Anfrage.
     * 
     * @param inStream die XmlRPC-Anfrage
     * @param requestType der Anfragetyp
     * @param requestID die Anfrage-ID
     * @return ein generiertes Anfrage-Objekt
     * @throws TcSOAPException
     */
    public static TcRequest[] readXmlRpcRequests(InputStream inStream, int requestType, String requestID) throws TcSOAPException {
        logger.trace(XmlRpcEngine.class.getName() + " readXmlRpcRequests " + new Object[] {inStream, new Integer(requestType), requestID});

        XmlRpcRequest xmlRpcRequest = null;

        try {
            XmlRpcRequestProcessor requestProcessor = new XmlRpcRequestProcessor() {};
            xmlRpcRequest = requestProcessor.processRequest(inStream);
        } catch (Exception e) {
            throw new TcSOAPException(e);
        }
        
        Map params = new HashMap();
        List paramList = xmlRpcRequest.getParameters();
        if (paramList != null && paramList.size() > 0) {
            Iterator itParams = paramList.iterator();
            for(int i=0; itParams.hasNext(); i++)
                params.put(String.valueOf(i), itParams.next());
        }

        Pattern pattern = Pattern.compile("[{]([^}]*)[}](.*)");
        Matcher matcher = pattern.matcher(xmlRpcRequest.getMethodName());
        
        String method = null;
        String module = null;
        if (matcher.matches()) {
            module = matcher.group(1);
            method = matcher.group(2);
        } else
            method = xmlRpcRequest.getMethodName();
        
        TcRequest octRequest = new TcRequest(requestID);
        octRequest.setRequestType(requestType);
        octRequest.setRequestParameters(params);
        if (method != null && method.length() > 0)
            octRequest.setTask(method);
        if (module != null && module.length() > 0)
            octRequest.setModule(module);

        return new TcRequest[] {octRequest};
    }
}
