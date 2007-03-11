
package de.tarent.octopus.soap;

import java.util.*;
import org.apache.axis.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.Deserializer;
import org.apache.axis.Constants;

/** 
 * Simple Serializer Factory
 */
public class SmartDeserializerFactory implements DeserializerFactory {

    /** serialVersionUID */
	private static final long serialVersionUID = -5887479778012020482L;
	List mechanisms;
    
    public SmartDeserializerFactory() {
        mechanisms = new ArrayList(1);
        mechanisms.add(Constants.AXIS_SAX);
    }
    
    public Deserializer getDeserializerAs(java.lang.String mechanismType) {
        return new SmartDeserializer();
    }

    public Iterator getSupportedMechanismTypes() {
        return mechanisms.iterator();
    }
}
