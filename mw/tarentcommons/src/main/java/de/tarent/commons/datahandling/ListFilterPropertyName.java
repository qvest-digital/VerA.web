package de.tarent.commons.datahandling;
/**
 * Is a wrapper class for attribute names (of any business object).<p>
 * It will be used by {@link de.tarent.commons.datahandling.ListFilterProvider}
 * in order to distinguish and handle the filter elements inside the list, that represents a filter.
 * <p>
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 * @see de.tarent.commons.datahandling.ListFilterProvider#getFilterList()
 * <p>
 */
public class ListFilterPropertyName {

    private String propertyName;

    /**
     * Creates an instance.<p>
     *
     * @param newPropertyName of an instance
     */
    public ListFilterPropertyName(String newPropertyName) {
        this.propertyName = newPropertyName;
    }

    /**
     * Returns an encapsulated property name.
     */
    public String toString() {
        return propertyName;
    }
}
