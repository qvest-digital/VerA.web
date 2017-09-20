package de.tarent.octopus.soap;

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
import java.io.OutputStream;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;

import de.tarent.octopus.security.TcSecurityException;

public class TcSOAPException extends Exception {
    /**
	 * serialVersionUID = -4535324668548567738L
	 */
	private static final long serialVersionUID = -4535324668548567738L;

	protected AxisFault axisFault;

    public TcSOAPException(String message) {
        super(message);
        axisFault = new AxisFault(message);
        axisFault.clearFaultDetails();
    }

    public TcSOAPException(Exception e) {
        if (e instanceof AxisFault)
            axisFault = (AxisFault) e;
        else if (e instanceof TcSecurityException) {
            TcSecurityException se = (TcSecurityException)e;
            axisFault = new AxisFault(se.getMessage(), se);
            axisFault.setFaultString(se.getMessage());
            if(se.getDetailMessage()!=null){
            	axisFault.addFaultDetailString(se.getDetailMessage());
            }
            axisFault.setFaultCode(se.getSoapFaultCode());
        } else
            axisFault = AxisFault.makeFault(e);
    }

    public void writeTo(OutputStream out) throws java.io.IOException, javax.xml.soap.SOAPException {
        Message outMessage = new Message(axisFault);
        outMessage.writeTo(out);
    }
}
