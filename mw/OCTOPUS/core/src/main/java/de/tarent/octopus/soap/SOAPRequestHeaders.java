package de.tarent.octopus.soap;
import java.util.List;

import org.w3c.dom.Node;

import de.tarent.octopus.request.RequestHeaders;

/**
 * This class holds and provides access to soap request headers.
 * For more information please see the superior class {@link RequestHeaders}.
 *
 * @author Jens Neumaier, tarent GmbH
 * @see RequestHeaders
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
        Node domHeaderNode = (Node) this.getHeaderAsObject(headerName);
        return domHeaderNode == null ? null : domHeaderNode.getFirstChild().getNodeValue();
    }

    /**
     * Implementing access method to the common header information <i>ReplyTo-Address</i>.
     *
     * @see RequestHeaders#getReplyToAddress()
     */
    public String getReplyToAddress() {
        RequestHeaders replyToHeader = this.getSubHeaders("ReplyTo");
        if (replyToHeader != null) {
            return replyToHeader.getHeaderAsString("Address");
        }
        return null;
    }
}
