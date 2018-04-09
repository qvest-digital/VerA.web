/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.datahandling;

import java.util.*;




/**
 * This is a class for representing one node in an filter expression.
 * This class supports methods to transform this FilterNode to a postfix notated list.
 *
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
     * @param filterList a filter list in postfix notation
     */
    public void appendToList(List filterList) {
        filterList.add(attribute);
        filterList.add(value);
        filterList.add(operator);
        if (negated)
            filterList.add(ListFilterOperator.NOT);
    }

    /**
     * Appends this FilterNode to the supplied filter expression in the List using an AND operator.
     * @param filterList a filter list in postfix notation
     */
    public void appendToListAND(List filterList) {
        boolean wasEmpty = filterList.size() == 0;
        appendToList(filterList);        
        if (! wasEmpty)
            filterList.add(ListFilterOperator.AND);
    }

    /**
     * Appends this FilterNode to the supplied filter expression in the List using an AND operator.
     * @param filterList a filter list in postfix notation
     */
    public void appendToListOR(List filterList) {
        boolean wasEmpty = filterList.size() == 0;
        appendToList(filterList);        
        if (! wasEmpty)
            filterList.add(ListFilterOperator.OR);
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
        this.operator = newOperator ;
    }

}