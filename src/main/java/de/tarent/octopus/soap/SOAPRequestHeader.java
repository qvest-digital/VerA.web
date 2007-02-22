package de.tarent.octopus.soap;

import java.util.Iterator;

import javax.xml.soap.SOAPHeaderElement;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tarent.octopus.request.RequestHeader;

public class SOAPRequestHeader implements RequestHeader {
	
	//
    // * Header information type constants
    //
	public static final String HEADER_TYPE_REPLY_TO_ADDRESS = "replyToAddress";
	
	public SOAPHeaderElement firstHeaderElement;
	
	public SOAPRequestHeader(SOAPHeaderElement firstHeaderElement) {
		this.firstHeaderElement = firstHeaderElement;
	}
	
	/**
	 * @see RequestHeader#getObject(String)
	 */
	public Object getObject(String headerType) {
		if (headerType.equals(HEADER_TYPE_REPLY_TO_ADDRESS))
			return getReplyToAddress();
		return null;
	}

	/**
	 * @see RequestHeader#getString(String)
	 */
	public String getString(String headerType) {
		if (headerType.equals(HEADER_TYPE_REPLY_TO_ADDRESS))
			return getReplyToAddress();
		return null;
	}
	
	public SOAPHeaderElement getFirstHeaderElement() {
		return this.firstHeaderElement;
	}
	
	public String getReplyToAddress() {
	    Iterator iter = firstHeaderElement.getParentElement().getChildElements();
        while (iter.hasNext())
        {
        	Node node = (Node) iter.next();
        	if (node.getLocalName().equals("ReplyTo"))
        	{
	            // found ReplyTo field
	            NodeList childs = node.getChildNodes();
	            for (int i=0; i<childs.getLength(); i++)
	            {
	                MessageElement thisChild = (MessageElement) childs.item(i);
	                if (thisChild.getLocalName().equals("Address"))
	                {
	                    // found address element
	                    String addressValue = thisChild.getValue();
	                    return addressValue;
	                }
	                
	            }
        	}
        }
        return null;
	}
}
