package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
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

import java.util.List;
import java.util.Arrays;

/**
 * Is a wrapper class for a filter operator.<p>
 *
 * It will be used by {@link de.tarent.commons.datahandling.ListFilterProvider}
 * in order to distinguish and handle the filter elements inside the list, that represents a filter.
 * <p>
 *
 * @see de.tarent.commons.datahandling.ListFilterProvider#getFilterList()
 * <p>
 */
public class ListFilterOperator {

    protected static final List VALID_OPERATOR_SYMBOLS =
      Arrays.asList(new String[] { "=", "!=", "<", ">", "IN", "LIKE", "ILIKE", "IS NULL", "AND", "OR", "NOT" });

    //TODO: remove if no dependences and use objects instead.
    public static final String OPERATOR_EQ = "=";
    public static final String OPERATOR_NE = "!=";
    public static final String OPERATOR_LT = "<";
    public static final String OPERATOR_GT = ">";
    public static final String OPERATOR_LIKE = "LIKE";
    public static final String OPERATOR_ILIKE = "ILIKE";
    public static final String OPERATOR_IS_NULL = "IS NULL";

    public static final String OPERATOR_AND = "AND";
    public static final String OPERATOR_OR = "OR";
    public static final String OPERATOR_NOT = "NOT";
    // registered operators
    public static final ListFilterOperator EQ = new ListFilterOperator("=");
    public static final ListFilterOperator NE = new ListFilterOperator("!=");
    public static final ListFilterOperator LT = new ListFilterOperator("<");
    public static final ListFilterOperator GT = new ListFilterOperator(">");
    public static final ListFilterOperator IN = new ListFilterOperator("IN");
    public static final ListFilterOperator LIKE = new ListFilterOperator("LIKE");
    public static final ListFilterOperator ILIKE = new ListFilterOperator("ILIKE");
    public static final ListFilterOperator IS_NULL = new ListFilterOperator("IS NULL");

    public static final ListFilterOperator AND = new ListFilterOperator("AND");
    public static final ListFilterOperator OR = new ListFilterOperator("OR");
    public static final ListFilterOperator NOT = new ListFilterOperator("NOT");

    private String operator;

    /**
     * Constructor with an operator string.
     * Only the preefined types are allowed. It is not possible to make a real enum of this, because apache-axis needs the
     * String Contructor.
     */
    public ListFilterOperator(String anOperator) {
        if (!VALID_OPERATOR_SYMBOLS.contains(anOperator)) {
            throw new IllegalArgumentException(
              anOperator + "is not a valid operator. Ony the predefined operators are allowed: " + VALID_OPERATOR_SYMBOLS);
        }
        operator = anOperator;
    }

    /**
     * Returns a count of operands required for a given operator.
     *
     * @return '1' if NOT or IS_NULL operator and '2' else.
     */
    public int getConsumingArsg() {
        if (NOT.operator.equals(operator)
          || IS_NULL.operator.equals(operator)) {
            return 1;
        }
        return 2;
    }

    /**
     * Checks if this operator is a connection operator.
     *
     * @return 'true' if is one of the following operators: AND, OR, NOT.
     */
    public boolean isConnectionOperator() {
        return (AND.operator.equals(operator)
          || OR.operator.equals(operator)
          || NOT.operator.equals(operator));
    }

    public boolean equals(Object o) {
        return (o instanceof ListFilterOperator) && o.toString().equals(operator);
    }

    /**
     * Returns a string representation of an encapsulated operator.
     */
    public String toString() {
        return operator;
    }
}
