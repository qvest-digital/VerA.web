package de.tarent.octopus.xmlrpc;

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
