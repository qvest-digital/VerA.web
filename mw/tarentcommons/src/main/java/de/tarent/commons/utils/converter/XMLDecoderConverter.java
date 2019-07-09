package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

import java.io.ByteArrayInputStream;
import java.beans.XMLDecoder;

/**
 * Converts from String to an Java object, using the Java XMLEndoder API.
 */
public class XMLDecoderConverter extends AbstractConverter {

    Class targetClass;

    public XMLDecoderConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    public Class getTargetType() {
        return targetClass;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws IllegalArgumentException {
        if (sourceData == null || "".equals(sourceData)) {
            return null;
        }

        if (!(sourceData instanceof String)) {
            throw new IllegalArgumentException("Source data is not instance of String (" + sourceData.getClass() + ")");
        }

        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(((String) sourceData).getBytes()));
        Object result = decoder.readObject();
        decoder.close();

        if (result != null && !targetClass.isAssignableFrom(result.getClass())) {
            throw new IllegalArgumentException("Decoded type is not instance of " + targetClass + " (" + result.getClass() + ")");
        }

        return result;
    }
}
