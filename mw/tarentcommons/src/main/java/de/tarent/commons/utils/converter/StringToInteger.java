package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

public class StringToInteger extends AbstractConverter {
    public Class getTargetType() {
        return Integer.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return new Integer((String) sourceData);
    }
}
