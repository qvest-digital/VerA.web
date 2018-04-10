package de.tarent.octopus.soap;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Simple Serializer for List and Map Structures
 */
public class SmartDeserializer extends DeserializerImpl {
	private static final long serialVersionUID = 7492736949191767385L;

	Serializable containerObject = null;

	int childIndex;

	public void setChildValue(Object value, Object hint) throws SAXException {
		if (isMapType()) {
			((Map)containerObject).put(hint, value);
		} else {
			if (containerObject instanceof List) {
				int pos = ((Integer)hint).intValue();
				List containerList = (List)containerObject;
				while (containerList.size() <= pos)
					containerList.add(null);
				containerList.set(pos, value);
			} else
				throw new SAXException("Unsupported type for SmartDeserializer: " +
				    containerObject.getClass().getName());
		}
	}

	/**
	 * This method is invoked after startElement when the element requires
	 * deserialization (i.e. the element is not an href and the value is not nil.)
	 * DeserializerImpl provides default behavior, which simply
	 * involves obtaining a correct Deserializer and plugging its handler.
	 *
	 * @param namespace  is the namespace of the element
	 * @param localName  is the name of the element
	 * @param prefix     is the prefix of the element
	 * @param attributes are the attributes on the element...used to get the type
	 * @param context    is the DeserializationContext
	 */
	public void onStartElement(String namespace, String localName,
	    String prefix, Attributes attributes,
	    DeserializationContext context)
	throws SAXException {
		QName type = getDefaultType();
		if (type == null)
			type = context.getTypeFromAttributes(namespace, localName, attributes);

		Class clazz = context.getTypeMapping().getClassForQName(type);
		if (clazz == null)
			throw new SAXException("no java type configured for '" + type + "'");
		if (!Serializable.class.isAssignableFrom(clazz))
			throw new SAXException("configured java type '" +
			    clazz.getName() + "' for '" + type + "' not serialisable");
		try {
			value = containerObject = (Serializable)clazz.newInstance();
		} catch (InstantiationException ie) {
			throw new SAXException(ie);
		} catch (IllegalAccessException iae) {
			throw new SAXException(iae);
		}
		//context.getDeserializerForType(type);
	}

	/**
	 * onStartChild is called on each child element.
	 * The default behavior supplied by DeserializationImpl is to do nothing.
	 * A specific deserializer may perform other tasks.  For example a
	 * BeanDeserializer will construct a deserializer for the indicated
	 * property and return it.
	 *
	 * @param namespace  is the namespace of the child element
	 * @param localName  is the local name of the child element
	 * @param prefix     is the prefix used on the name of the child element
	 * @param attributes are the attributes of the child element
	 * @param context    is the deserialization context.
	 *
	 * @return is a Deserializer to use to deserialize a child (must be
	 *     a derived class of SOAPHandler) or null if no deserialization should
	 *     be performed.
	 */
	public SOAPHandler onStartChild(String namespace, String localName,
	    String prefix, Attributes attributes,
	    DeserializationContext context)
	throws SAXException {
		QName itemType = context.getTypeFromAttributes(namespace, localName, attributes);

		if (itemType == null)
			throw new SAXException("cant get item type for: " + namespace + ":" + localName);

		Deserializer dSer = context.getDeserializerForType(itemType);

		Object hint = null;
		if (isMapType())
			hint = localName;
		else
			hint = new Integer(childIndex++);

		dSer.registerValueTarget(new DeserializerTarget(this, hint));

		// The framework handles knowing when the value is complete, as
		// long as we tell it about each child we're waiting on...
		addChildDeserializer(dSer);

		// it seems save to cast here
		// since this is done in axis to
		return (SOAPHandler)dSer;
	}

	protected boolean isMapType() {
		return (containerObject instanceof Map);
	}
}
