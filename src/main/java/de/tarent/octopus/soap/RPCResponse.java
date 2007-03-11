/* $Id: RPCResponse.java,v 1.2 2007/03/11 14:04:34 christoph Exp $
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
