package de.tarent.commons.datahandling.entity;
/**
 * This interface stands for an abstract unit that may have Attributes for reading.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public interface ReadableAttribute {

    /**
     * returns an attribute of the unit
     * The key may be hierarchically organized with a '.' as delimiter, depending on the underlaying implementation.
     *
     * @param key the key of the attribute
     * @return the value of th attribute
     */
    public Object getAttribute(String key) throws EntityException;
}
