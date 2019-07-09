package de.tarent.octopus.util;
import java.util.*;

/**
 * Map Implementierung, deren KeySet().iterator() die Keys
 * in der Reihenfolge zurück gibt, in der sie eingefügt wurden.
 */
public class ConsistentMap extends HashMap {
    /**
     * serialVersionUID = -7508966936670839920L
     */
    private static final long serialVersionUID = -7508966936670839920L;

    LinkedHashSet orderedKeySet = new LinkedHashSet();

    public ConsistentMap() {
        super();
    }

    public void clear() {
        super.clear();
        orderedKeySet.clear();
    }

    public Set keySet() {
        return orderedKeySet;
    }

    public Object put(Object key, Object value) {
        orderedKeySet.add(key);
        return super.put(key, value);
    }

    public void putAll(Map m) {
        orderedKeySet.addAll(m.keySet());
        super.putAll(m);
    }

    public Object remove(Object key) {
        orderedKeySet.remove(key);
        return super.remove(key);
    }
}
