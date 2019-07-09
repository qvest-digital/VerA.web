package de.tarent.commons.datahandling;
/**
 * Interface for a storage class for pojo properties
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public interface GenericPojoStorage {
    /**
     * Template Method for setting a propertie.
     *
     * @param key   the key of the propertie
     * @param value the value of the propertie
     * @return the old value of the propertie
     */
    public abstract Object put(Object key, Object value);

    /**
     * Template Method for accessing a propertie.
     *
     * @param key the key of the propertie
     * @return the value of the propertie
     */
    public abstract Object get(Object key);
}
