package de.tarent.commons.datahandling;

import java.util.Map;

/**
 * Simple Implementation of a GenericPojoStorage as a wrapper over a Map.
 * With this class it is possible to use an existing Map as a storage without copying it to a new data structure.
 */
public class MapWrapperPojoStorage
    implements GenericPojoStorage {

    Map dataContainer;

    public MapWrapperPojoStorage(Map dataContainer) {
        this.dataContainer = dataContainer;
    }

    public Object put(Object key, Object value) {
        return dataContainer.put(key, value);
    }

    public Object get(Object key) {
        return dataContainer.get(key);
    }

    public Map getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(final Map newDataContainer) {
        this.dataContainer = newDataContainer;
    }
}
