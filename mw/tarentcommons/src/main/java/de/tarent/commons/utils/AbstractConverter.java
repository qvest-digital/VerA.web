package de.tarent.commons.utils;
/**
 * Abstract default implementation of the converter interface.
 */
public abstract class AbstractConverter implements Converter {

    /**
     * Returns the Name of the converter
     */
    public String getConverterName() {
        String cname = getClass().getName();
        return cname.substring(cname.lastIndexOf(".") + 1);
    }

    /**
     * Returns the target type of the converter
     */
    public Class getTargetType() {
        return Object.class;
    }

    /**
     * Returns the source type of the converter
     */
    public Class getSourceType() {
        return Object.class;
    }

    /**
     * Converts a data Item from the source to the target type.
     * This method does the handling of errors and null and uses doConversion() for the real conversion.
     *
     * @throws IllegalArgumentException if the input data is not convertable by this converter
     */
    public Object convert(Object sourceData) throws IllegalArgumentException {
        if (sourceData == null) {
            return null;
        }
        try {
            return doConversion(sourceData);
        } catch (Exception e) {
            // TODO Sebastian Mancke: IAE have only a simple string constructor in java 1.4, please verify this changes. Also
            // externalize strings?

            RuntimeException re = new IllegalArgumentException("Error while conversion '" + sourceData + "'.");
            re.initCause(e);
            throw re;
        }
    }

    /**
     * Template method for the real conversion.
     */
    public abstract Object doConversion(Object sourceData) throws Exception;
}
