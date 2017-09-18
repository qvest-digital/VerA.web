package de.tarent.octopus.soap;

/*
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
