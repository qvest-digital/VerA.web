package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

/**
 * Any number to integer
 */
public class NumberToInteger extends AbstractConverter {

    public Class getTargetType() {
        return Integer.class;
    }

    public Class getSourceType() {
        return Number.class;
    }

    public Object doConversion(Object sourceData) {
        return new Integer(((Number) sourceData).intValue());
    }
}
