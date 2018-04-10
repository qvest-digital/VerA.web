package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

public class StringToBoolean extends AbstractConverter {
    public Class getTargetType() {
        return Boolean.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return new Boolean((String)sourceData);
    }
}
