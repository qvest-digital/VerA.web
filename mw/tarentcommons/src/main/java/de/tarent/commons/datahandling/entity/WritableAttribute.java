package de.tarent.commons.datahandling.entity;

/**
 * This interface stands for an abstract unit that may have Attributes for writing.
 *
 * @author Sebastian Mancke, tarent GmbH
 *
 */
public interface WritableAttribute  {

	/**
     * Sets the attribute of the entity.
     * The key may be hierarchically organized with a '.' as delimiter, depending on the underlaying implementation.
     *
     * @param key The key of the attribute
     * @param value The value of the attribute
     */
    public void setAttribute(String key, Object value) throws EntityException;
}
