package de.tarent.commons.datahandling;

import java.util.List;

/**
 * Is an interface in order to provide the list representation of a filter.<p>
 *
 * @see de.tarent.commons.datahandling.ListFilterImpl
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public interface ListFilterProvider {

    /**
     * Returns the filter tree as list in Reverse Polish Notation (Umgekehrte Polnische Notation UPN)
     * Property names (attribute names) are wrapped by an ListFilterPropertyName-Wrapper. Values may be from any type.
     * The operators are from the type ListFilterOperator.
     */
    public List getFilterList();

}
