package de.tarent.commons.datahandling.entity;
import java.util.HashMap;

/**
 * Simple implementation for a LookupContext based on a Map
 */
public class LookupContextImpl implements LookupContext {

    HashMap map = new HashMap();

    public boolean doesEntityExist(Object entityID, String entityType) {
        return map.containsKey(concat(entityID, entityType));
    }

    public Object getEntity(Object entityID, String entityType) {
        return map.get(concat(entityID, entityType));
    }

    public void registerEntity(Object entityID, String entityType, Object entity) {
        map.put(concat(entityID, entityType), entity);
    }

    protected String concat(Object entityID, String entityType) {
        return entityType + ":" + entityID;
    }
}
