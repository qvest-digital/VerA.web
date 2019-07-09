package de.tarent.commons.datahandling.binding;
import de.tarent.commons.datahandling.entity.*;

import java.util.*;

/**
 * Simple Model implementation storing in a Map.
 */
public class MapModel extends AbstractModel {

    Map map;

    public MapModel() {
        this.map = new HashMap();
    }

    public MapModel(Map storageMap) {
        this.map = storageMap;
    }

    public Map getStorageMap() {
        return map;
    }

    public void setAttribute(String key, Object value) throws EntityException {
        map.put(key, value);
        fireDataChanged(new DataChangedEvent(this, key));
    }

    public Object getAttribute(String key) throws EntityException {
        if (key == null || "".equals(key)) {
            return map;
        }
        return map.get(key);
    }

    public String toString() {
        return map.toString();
    }

    public Map getMap() {
        return map;
    }
}
