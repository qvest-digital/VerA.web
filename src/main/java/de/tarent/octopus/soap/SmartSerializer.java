package de.tarent.octopus.soap;

import java.util.logging.Logger;
import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import java.util.*;
import java.util.logging.Level;

public class SmartSerializer implements Serializer {

    private static final Logger logger = Logger.getLogger(SmartSerializer.class.getName());

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
            logger.log(Level.SEVERE, "Error while serialization", e);
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