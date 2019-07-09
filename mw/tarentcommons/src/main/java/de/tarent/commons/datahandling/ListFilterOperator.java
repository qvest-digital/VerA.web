package de.tarent.commons.datahandling;
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
