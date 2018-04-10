package de.tarent.commons.datahandling.entity;

import java.util.*;

/**
 * Represents an API to set paraemters in an parameterized structure.
 */
public interface ParamSet extends WritableAttribute {

    /**
     * Clears all parameters of the structure.
     */
    public void clearAttributes();

    /**
     * Sets the attribute <code>attributeName</code> of this structure to the supplied value.
     */
    public void setAttribute(String attributeName, Object attributeValue);

    /**
     * Sets the attributes of this structure to the supplied attributes in the map.
     */
    public void setAttributes(AttributeSource attributeSource);

    /**
     * Returns the String List of the AttributeNames
     */
    public List getAttributeNames();

}
