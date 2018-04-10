package de.tarent.commons.datahandling.entity;

import de.tarent.commons.datahandling.entity.AttributeSource;

/**
 * Interface for object creation factories
 */
public interface EntityFactory {

    /**
     * Returns a new instance which is not initialized
     */
    public Object getEntity();

    /**
     * Returns a new initialized instance
     */
    public Object getEntity(AttributeSource as, LookupContext lc);

    /**
     * Fills the supplied entity with the data from the attribute source
     */
    public void fillEntity(Object entity, AttributeSource as, LookupContext lc);

    /**
     * Stores the entity attributes to the ParamSet.
     */
    public void writeTo(ParamSet target, Object entity);
}
