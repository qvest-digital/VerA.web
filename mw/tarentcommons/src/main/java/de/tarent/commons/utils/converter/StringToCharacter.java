package de.tarent.commons.utils.converter;
import de.tarent.commons.utils.AbstractConverter;

public class StringToCharacter extends AbstractConverter {

    public Class getTargetType() {
        return Character.class;
    }

    public Class getSourceType() {
        return String.class;
    }

    public Object doConversion(Object sourceData) throws IllegalArgumentException {
        if (sourceData == null || ((String) sourceData).length() != 1) {
            throw new IllegalArgumentException("Conversion String => Character not possible for '" + sourceData + "'");
        }
        return new Character(((String) sourceData).charAt(0));
    }
}
