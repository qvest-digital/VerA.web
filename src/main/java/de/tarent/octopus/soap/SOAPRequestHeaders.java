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
import java.util.List;
import org.w3c.dom.Node;

import de.tarent.octopus.request.RequestHeaders;

/**
 * This class holds and provides access to soap request headers.
 * For more information please see the superior class {@link RequestHeaders}.
 * 
 * @see RequestHeaders
 * @author Jens Neumaier, tarent GmbH
 */
public class SOAPRequestHeaders extends RequestHeaders {
	
	/**
	 * Default constructor
	 * 
	 * @see RequestHeaders#RequestHeaders(List)
	 */
	public SOAPRequestHeaders(List headerList) {
		super(headerList);
	}
	
	/**
	 * Overwrites the access to a header's string representation.
	 * 
	 * @see RequestHeaders#getHeaderAsString(String)
	 */
	public String getHeaderAsString(String headerName) {
		Node domHeaderNode = (Node)this.getHeaderAsObject(headerName);
		return domHeaderNode == null ? null : domHeaderNode.getFirstChild().getNodeValue();
	}
	
	/**
	 * Implementing access method to the common header information <i>ReplyTo-Address</i>.
	 * 
	 * @see RequestHeaders#getReplyToAddress()
	 */
	public String getReplyToAddress() {
	    RequestHeaders replyToHeader = this.getSubHeaders("ReplyTo");
	    if (replyToHeader != null)
	    	return replyToHeader.getHeaderAsString("Address");
	    return null;
	}
}
