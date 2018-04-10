package de.tarent.octopus.response;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.content.TcContent;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.resource.Resources;
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

	    TcSOAPException soapException;
	    if (config.getModuleConfig().getParam(TcEnv.KEY_RESPONSE_ERROR_LEVEL).equals(TcEnv.VALUE_RESPONSE_ERROR_LEVEL_DEVELOPMENT))
		soapException = new TcSOAPException(e);
	    else
		soapException = new TcSOAPException(Resources.getInstance().get("ERROR_MESSAGE_GENERAL_ERROR"));

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
