package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;
import java.util.*;

public class LongToDate extends AbstractConverter {
    public Class getTargetType() {
        return Date.class;
    }

    public Class getSourceType() {
        return Long.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        Date d = new Date();
        d.setTime(((Long)sourceData).longValue());
        return d;
    }
}
