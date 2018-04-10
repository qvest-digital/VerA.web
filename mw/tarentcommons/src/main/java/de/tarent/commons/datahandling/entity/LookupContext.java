package de.tarent.commons.datahandling.entity;

public interface LookupContext {

    public boolean doesEntityExist(Object entityID, String entityType);

    public Object getEntity(Object entityID, String entityType);

    public void registerEntity(Object entityID, String entityType, Object entity);

}
