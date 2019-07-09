package de.tarent.commons.datahandling.entity;
import java.util.List;

/**
 * This interface stands for an abstract unit that may have some attributes
 * for reading and have the capability to list the attributes.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public interface AttributeSource extends ReadableAttribute {
    /**
     * Return the value of the given attribute name.
     *
     * @param attributeName
     */
    public Object getAttribute(String attributeName);

    /**
     * Return the type of the given attribute name.
     *
     * @param attributeName
     */
    public Class getAttributeType(String attributeName);

    /**
     * Returns a list the attribute names
     *
     * @return list of Strings
     */
    public List getAttributeNames();
}
