package de.tarent.commons.datahandling;
import java.util.List;

/**
 * Is an interface in order to provide the list representation of a filter.<p>
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 * @see de.tarent.commons.datahandling.ListFilterImpl
 */
public interface ListFilterProvider {

    /**
     * Returns the filter tree as list in Reverse Polish Notation (Umgekehrte Polnische Notation UPN)
     * Property names (attribute names) are wrapped by an ListFilterPropertyName-Wrapper. Values may be from any type.
     * The operators are from the type ListFilterOperator.
     */
    public List getFilterList();
}
