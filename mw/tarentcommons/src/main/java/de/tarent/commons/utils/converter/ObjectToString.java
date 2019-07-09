package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

public class ObjectToString extends AbstractConverter {
    public Class getTargetType() {
        return String.class;
    }

    public Class getSourceType() {
        return Object.class;
    }

    public Object doConversion(Object sourceData) {
        return sourceData.toString();
    }
}
