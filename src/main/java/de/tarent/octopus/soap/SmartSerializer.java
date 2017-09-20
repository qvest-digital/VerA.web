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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import de.tarent.octopus.logging.LogFactory;

public class SmartSerializer implements Serializer {

    /** serialVersionUID */
	private static final long serialVersionUID = 7766326226378057582L;

	private static final Log logger = LogFactory.getLog(SmartSerializer.class);

    public void serialize(QName proposedName, Attributes attributes, Object value, SerializationContext context) 
        throws IOException {

        context.startElement(proposedName, null);
        try {
            if (value instanceof List) {
                for (Iterator iter = ((List)value).iterator(); iter.hasNext();) {
                    Object item = iter.next();
                    if (item == null) {
                        context.serialize(new QName("", "item"), null, item);
                    } else {
                        QName childElementName = new QName("", context.getQNameForClass(item.getClass()).getLocalPart());
                        context.serialize(childElementName, null, item);
                    }
                }
            } else if (value instanceof Map) {
                for (Iterator iter = ((Map)value).entrySet().iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry)iter.next();
                    QName childElementName = new QName("", ""+entry.getKey());
                    context.serialize(childElementName, null, entry.getValue());
                }
            } else {
                context.writeString(context.getEncoder().encode(""+value));
            }
        } catch (Exception e) {
            logger.error("Error while serialization", e);
        } finally {
            context.endElement();
        }
    }
    
    public Element writeSchema(Class javaType, Types types) throws Exception {
        throw new RuntimeException("writeSchema not supported");
    }

    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }
}