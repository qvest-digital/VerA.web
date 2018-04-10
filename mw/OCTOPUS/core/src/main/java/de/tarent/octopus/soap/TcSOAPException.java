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
