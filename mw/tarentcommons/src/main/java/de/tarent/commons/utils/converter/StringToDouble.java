package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

public class StringToDouble extends AbstractConverter {
    public Class getTargetType() {
        return Double.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return new Double((String)sourceData);
    }
}
