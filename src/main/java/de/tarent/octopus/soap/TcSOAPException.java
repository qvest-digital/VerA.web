/* $Id: TcSOAPException.java,v 1.2 2006/02/08 11:27:00 christoph Exp $
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
