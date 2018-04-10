package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;
import java.util.*;

public class DateToLong extends AbstractConverter {
    public Class getTargetType() {
        return Long.class;
    }

    public Class getSourceType() {
        return Date.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return new Long(((Date)sourceData).getTime());
    }
}
