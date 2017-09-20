package de.tarent.octopus.data;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
