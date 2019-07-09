package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

/**
 * Converts true to 1,
 * false to 0.
 */
public class BooleanToInteger extends AbstractConverter {

    public Class getTargetType() {
        return Integer.class;
    }

    public Class getSourceType() {
        return Boolean.class;
    }

    public Object doConversion(Object sourceData) {
        if (((Boolean) sourceData).booleanValue()) {
            return new Integer(1);
        }
        return new Integer(0);
    }
}
