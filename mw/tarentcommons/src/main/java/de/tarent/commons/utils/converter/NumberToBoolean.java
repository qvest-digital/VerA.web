package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;

/**
 * Converts 1 to true,
 * everything else to false.
 */
public class NumberToBoolean extends AbstractConverter {

    public Class getTargetType() {
        return Boolean.class;
    }

    public Class getSourceType() {
        return Number.class;
    }

    public Object doConversion(Object sourceData) {
        if (((Number)sourceData).intValue() == 1)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
}
