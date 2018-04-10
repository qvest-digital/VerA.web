package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.beans.XMLEncoder;

/**
 * Converts from an Java object to String, using the Java XMLEndoder API.
 */
public class XMLEncoderConverter extends AbstractConverter {

    Class sourceClass;

    public XMLEncoderConverter(Class sourceClass) {
        this.sourceClass = sourceClass;
    }

    public Class getTargetType() {
        return String.class;
    }

    public Class getSourceType() {
        return sourceClass;
    }

    public Object doConversion(Object sourceData) throws IllegalArgumentException {
        if (sourceData != null && !sourceClass.isAssignableFrom(sourceData.getClass()))
            throw new IllegalArgumentException("Source data is not instance of "+sourceClass+" ("+sourceData.getClass() +")");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(bos);
        encoder.writeObject(sourceData);
        encoder.close();

        try {
            return bos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) { // should never occur with UTF-8
            throw new RuntimeException(e);
        }
    }
}
