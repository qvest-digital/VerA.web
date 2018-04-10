package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

public class StringToLong extends AbstractConverter {
    public Class getTargetType() {
        return Long.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return new Long((String)sourceData);
    }
}
