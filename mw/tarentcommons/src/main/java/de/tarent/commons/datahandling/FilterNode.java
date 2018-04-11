package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
