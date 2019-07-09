package de.tarent.commons.utils;
/**
 * Interface for converter between data types
 */
public interface Converter {

    /**
     * Returns the Name of the converter
     */
    public String getConverterName();

    /**
     * Returns the target type of the converter
     */
    public Class getTargetType();

    /**
     * Returns the source type of the converter
     */
    public Class getSourceType();

    /**
     * Converts a data Item from the source to the target type.
     * If the supplied sourceData is null, a null should be returned.
     *
     * @param sourceData the convertable data. null is allowed here.
     * @throws IllegalArgumentException if the input data is not convertable by this converter
     */
    public Object convert(Object sourceData) throws IllegalArgumentException;
}
