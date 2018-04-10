package de.tarent.octopus.soap;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.soap.SOAPException;

import org.apache.axis.Message;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;

/**
 * Bereitstellung und Kapselung von SOAP Funktionalität
 * für die Erstellung einer Antwort auf einen RPC Aufruf.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class RPCResponse {
    protected RPCElement rpc;
    protected TcSOAPEngine soapEngine;

    public static final String METHOD_RESPONSE_SUFFIX = "Response";

    public RPCResponse(TcSOAPEngine soapEngine, String namespace, String callingMethodName) {
        this.soapEngine = soapEngine;
        rpc = new RPCElement(namespace, callingMethodName + METHOD_RESPONSE_SUFFIX, null);
    }

    public void addParam(String name, Object value) {
        rpc.addParam(new RPCParam(name, value));
    }

    /**
     * Method writeTo.
     * @param outputStream
     */
    public void writeTo(OutputStream outputStream) throws IOException, SOAPException {
        SOAPEnvelope env = new SOAPEnvelope();
        env.addBodyElement(rpc);

        Message outMessage = new Message(env);
        outMessage.setMessageContext(soapEngine.createMessageContext());
        outMessage.writeTo(outputStream);
    }
}
