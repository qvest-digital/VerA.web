package de.tarent.octopus.soap;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializerFactory;

import javax.xml.rpc.encoding.Serializer;
import java.util.ArrayList;
import java.util.Iterator;

public class SmartSerializerFactory implements SerializerFactory {
    private static final long serialVersionUID = -8549318271987956766L;

    ArrayList mechanisms;

    public SmartSerializerFactory() {
        mechanisms = new ArrayList(1);
        mechanisms.add(Constants.AXIS_SAX);
    }

    public Serializer getSerializerAs(String mechanismType) {
        return new SmartSerializer();
    }

    public Iterator getSupportedMechanismTypes() {
        return mechanisms.iterator();
    }
}
