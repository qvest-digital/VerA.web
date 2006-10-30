package de.tarent.octopus.soap;

import javax.xml.rpc.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import java.util.*;
import org.apache.axis.Constants;

public class SmartSerializerFactory implements SerializerFactory {

    List mechanisms;

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