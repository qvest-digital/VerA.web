package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

/**
 * Any number to integer
 */
public class NumberToLong extends AbstractConverter {

    public Class getTargetType() {
        return Long.class;
    }

    public Class getSourceType() {
        return Number.class;
    }

    public Object doConversion(Object sourceData) {
        return new Long(((Number)sourceData).longValue());
    }
}
