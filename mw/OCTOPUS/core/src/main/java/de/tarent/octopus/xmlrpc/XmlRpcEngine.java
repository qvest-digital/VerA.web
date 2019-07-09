package de.tarent.octopus.xmlrpc;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.soap.TcSOAPException;
import lombok.extern.log4j.Log4j2;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.XmlRpcRequestProcessor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse dient dem Auslesen von XmlRpc-Anfragen.
 *
 * @author mikel
 */
@Log4j2
public class XmlRpcEngine {
    /**
     * Diese Methode analysiert eine XML-RPC-Anfrage.
     *
     * @param inStream    die XmlRPC-Anfrage
     * @param requestType der Anfragetyp
     * @param requestID   die Anfrage-ID
     * @return ein generiertes Anfrage-Objekt
     * @throws TcSOAPException
     */
    public static TcRequest[] readXmlRpcRequests(InputStream inStream, int requestType, String requestID) throws TcSOAPException {
        logger.trace(XmlRpcEngine.class.getName() + " readXmlRpcRequests " +
          new Object[] { inStream, new Integer(requestType), requestID });

        XmlRpcRequest xmlRpcRequest = null;

        try {
            XmlRpcRequestProcessor requestProcessor = new XmlRpcRequestProcessor() {
            };
            xmlRpcRequest = requestProcessor.processRequest(inStream);
        } catch (Exception e) {
            throw new TcSOAPException(e);
        }

        Map params = new HashMap();
        List paramList = xmlRpcRequest.getParameters();
        if (paramList != null && paramList.size() > 0) {
            Iterator itParams = paramList.iterator();
            for (int i = 0; itParams.hasNext(); i++) {
                params.put(String.valueOf(i), itParams.next());
            }
        }

        Pattern pattern = Pattern.compile("[{]([^}]*)[}](.*)");
        Matcher matcher = pattern.matcher(xmlRpcRequest.getMethodName());

        String method = null;
        String module = null;
        if (matcher.matches()) {
            module = matcher.group(1);
            method = matcher.group(2);
        } else {
            method = xmlRpcRequest.getMethodName();
        }

        TcRequest octRequest = new TcRequest(requestID);
        octRequest.setRequestType(requestType);
        octRequest.setRequestParameters(params);
        if (method != null && method.length() > 0) {
            octRequest.setTask(method);
        }
        if (module != null && module.length() > 0) {
            octRequest.setModule(module);
        }

        return new TcRequest[] { octRequest };
    }
}
