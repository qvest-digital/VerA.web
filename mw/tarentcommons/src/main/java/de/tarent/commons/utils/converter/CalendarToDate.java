package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

import java.util.*;

public class CalendarToDate extends AbstractConverter {
    public Class getTargetType() {
        return Date.class;
    }

    public Class getSourceType() {
        return Calendar.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return ((Calendar) sourceData).getTime();
    }
}
