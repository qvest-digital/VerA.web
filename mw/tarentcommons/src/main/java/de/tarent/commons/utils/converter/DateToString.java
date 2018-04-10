package de.tarent.commons.utils.converter;

import de.tarent.commons.utils.AbstractConverter;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateToString extends AbstractConverter {
    DateFormat df = new SimpleDateFormat();

    public Class getTargetType() {
        return String.class;
    }

    public Class getSourceType() {
        return Date.class;
    }

    public Object doConversion(Object sourceData) throws NumberFormatException {
        return df.format((Date)sourceData);
    }
}
