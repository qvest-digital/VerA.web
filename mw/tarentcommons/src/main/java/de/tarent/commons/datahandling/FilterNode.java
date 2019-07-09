package de.tarent.commons.datahandling;
import java.util.*;

/**
 * This is a class for representing one node in an filter expression.
 * This class supports methods to transform this FilterNode to a postfix notated list.
 */
public class FilterNode {

    ListFilterOperator operator;
    ListFilterPropertyName attribute;
    Object value;
    boolean negated = false;

    public FilterNode(String theAttribute, ListFilterOperator theOperator, Object theValue, boolean isNegated) {
        this.attribute = new ListFilterPropertyName(theAttribute);
        this.value = theValue;
        this.operator = theOperator;
        this.negated = isNegated;
    }

    public static FilterNode eq(String theAttribute, Object theValue) {
        return new FilterNode(theAttribute, ListFilterOperator.EQ, theValue, false);
    }

    public static FilterNode neq(String theAttribute, Object theValue) {
        return new FilterNode(theAttribute, ListFilterOperator.NE, theValue, true);
    }

    /**
     * Appends this FilterNode to the supplied filter expression in the List.
     *
     * @param filterList a filter list in postfix notation
     */
    public void appendToList(List filterList) {
        filterList.add(attribute);
        filterList.add(value);
        filterList.add(operator);
        if (negated) {
            filterList.add(ListFilterOperator.NOT);
        }
    }

    /**
     * Appends this FilterNode to the supplied filter expression in the List using an AND operator.
     *
     * @param filterList a filter list in postfix notation
     */
    public void appendToListAND(List filterList) {
        boolean wasEmpty = filterList.size() == 0;
        appendToList(filterList);
        if (!wasEmpty) {
            filterList.add(ListFilterOperator.AND);
        }
    }

    /**
     * Appends this FilterNode to the supplied filter expression in the List using an AND operator.
     *
     * @param filterList a filter list in postfix notation
     */
    public void appendToListOR(List filterList) {
        boolean wasEmpty = filterList.size() == 0;
        appendToList(filterList);
        if (!wasEmpty) {
            filterList.add(ListFilterOperator.OR);
        }
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean newNegated) {
        this.negated = newNegated;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }

    public ListFilterPropertyName getAttribute() {
        return attribute;
    }

    public void setAttribute(ListFilterPropertyName newAttribute) {
        this.attribute = newAttribute;
    }

    public ListFilterOperator getOperator() {
        return operator;
    }

    public void setOperator(ListFilterOperator newOperator) {
        this.operator = newOperator;
    }
}
