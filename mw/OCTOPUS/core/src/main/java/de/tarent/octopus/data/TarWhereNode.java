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
