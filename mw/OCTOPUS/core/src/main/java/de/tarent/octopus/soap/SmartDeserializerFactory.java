package de.tarent.octopus.soap;
import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializerFactory;

import javax.xml.rpc.encoding.Deserializer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simple Serializer Factory
 */
public class SmartDeserializerFactory implements DeserializerFactory {
	private static final long serialVersionUID = 836471028156815505L;

	ArrayList mechanisms;

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
