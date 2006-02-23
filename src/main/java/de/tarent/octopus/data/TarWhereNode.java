/* $Id: TarWhereNode.java,v 1.2 2006/02/23 15:07:57 christoph Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.data;

import java.util.Arrays;

/**
 * Ein Knoten mit einem Token eines SQL-Ausdruckes.
 *
 * Wird mit einem String initialisiert. 
 * Dieser wird als Schlüsselwort erkannt, wenn er in den Arrays DOUBLE_OPERATORS oder SINGLE_OPERATORS enthalten ist.
 *
 * @see TarWhereClause
*/
public class TarWhereNode {
    /**
     * Operatoren, die sich auf zwei Operanden beziehen.
     * Muss sortiert sein, da mit einer Binary Search darin gesucht wird.    
     */
    protected static String[] DOUBLE_OPERATORS = new String[] { "=", "and", "in", "like", "or" };

    /**
     * Operatoren, die sich auf einen Operanden beziehen.
     * Muss sortiert sein, da mit einer Binary Search darin gesucht wird.    
     */
    protected static String[] SINGLE_OPERATORS = new String[] { "not" };

    private String value = null;

    private boolean isSingleOperator = false;
    private boolean isDoubleOperator = false;

    private TarWhereNode firstChild = null;
    private TarWhereNode secondChild = null;

    /**
     * Initialisierung mit einem String
     *
     * @param token Das Token
     */
    public TarWhereNode(String token) {
        this(token, false);
    }

    /**
     * Initialisierung mit einem String, "'" werden bei bedarf gequotet.
     *
     * @param token Das Token
     * @param quote Gibt an, ob "'" in dem Token durch "\'" ersetzt werden sollen und das Token in "'" eingebettet werden soll.
     */
    public TarWhereNode(String token, boolean quote) {
        String lowerCaseToken = token.toLowerCase();
        if (Arrays.binarySearch(DOUBLE_OPERATORS, lowerCaseToken) >= 0) {
            isDoubleOperator = true;
            value = lowerCaseToken;
        } else if (Arrays.binarySearch(SINGLE_OPERATORS, lowerCaseToken) >= 0) {
            isSingleOperator = true;
            value = lowerCaseToken;
        } else {
            value = token;
        }
    }

    /**
     * Gibt an, ob dieses Knoten ein Operator ist, also ob es in dem entsprechenden Array enthalten ist.
     */
    public boolean isOperator() {
        return isDoubleOperator || isSingleOperator;
    }

    /**
     * Gibt an, ob dieses Knoten ein Operator ist, 
     * der sich auf einen Operand bezieht, 
     * also ob es in dem entsprechenden Array enthalten ist.
     */
    public boolean isSingleOperator() {
        return isSingleOperator;
    }

    /**
     * Gibt an, ob dieses Knoten ein Operator ist, 
     * der sich auf zwei Operanden bezieht, 
     * also ob es in dem entsprechenden Array enthalten ist.
     */
    public boolean isDoubleOperator() {
        return isDoubleOperator;
    }

    /**
     * Setzt den linken Kindknoten.
     */
    public void setFirstChild(TarWhereNode child) {
        firstChild = child;
    }

    /**
     * Setzt den rechten Kindknoten.
     */
    public void setSecondChild(TarWhereNode child) {
        secondChild = child;
    }

    /**
     * Gibt den linken Kindknoten zurück, oder null, wenn es keinen gibt.
     */
    public TarWhereNode getFistChild() {
        return firstChild;
    }

    /**
     * Gibt den rechten Kindknoten zurück, oder null, wenn es keinen gibt.
     */
    public TarWhereNode getSecondChild() {
        return secondChild;
    }

    /**
     * Gibt den Inhalt des Knotens als Stringbuffer.
     */
    public StringBuffer getValue() {
        return new StringBuffer(value);
    }

    /**
     * Gibt eine menschenlesbare Repräsentation rekursiv mit Kindern zurück.
     */
    public String toString() {
        return " (" + value + " " + firstChild + " " + secondChild + ")";
    }
}
