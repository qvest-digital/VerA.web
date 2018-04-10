package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

public class StringToFloat extends AbstractConverter {
    public Class getTargetType() {
        return Float.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return new Float((String)sourceData);
    }
}
