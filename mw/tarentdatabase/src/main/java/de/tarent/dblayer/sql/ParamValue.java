package de.tarent.dblayer.sql;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * Named parameter for insert into statements.
 */
public class ParamValue extends SetDbContextImpl implements Cloneable {
    String name;
    Object value;
    boolean set = false;
    boolean optional = false;

    public ParamValue(String parameterName) {
        this.name = parameterName;
    }

    public ParamValue(String parameterName, boolean optional) {
        this.name = parameterName;
        this.optional = optional;
    }

    public ParamValue(String parameterName, Object parameterValue) {
        this.name = parameterName;
        setValue(parameterValue);
    }

    /**
     * Returns the parameters value, if set. The value even may be <code>null</code>.
     * If the value was not set, this method throws an IllegalArgumentException.
     *
     * @throws IllegalArgumentException if no value was set
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the parameters value. The value even may be <code>null</code>.
     */
    public void setValue(Object newValue) {
        this.value = newValue;
        set = true;
    }

    /**
     * Returns true, if the value was set, false otherwise
     */
    public final boolean isSet() {
        return set;
    }

    /**
     * Returns, if the ParamValue should be treated as an optional Parameter
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Clears the Parameter value
     */
    public void clear() {
        value = null;
        set = false;
    }

    /**
     * @return the parameters name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the formated value, if this parameter was set.
     * Otherwise the SQL param symbol '?' will be returned for usage in an prepared statement.
     * This method relies on an previous set DBContext. It no context was set, it will fall
     * back to the {#see {@link SQL#format(DBContext, Object)} default behavior.
     */
    public String toString() {
        if (!isSet()) {
            return "?";
        }

        return SQL.format(getDBContext(), getValue());
    }

    /**
     * Returns an independent clone of this statement.
     * ATTENTION: The value element of the expression will no be copied
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            ParamValue theClone = (ParamValue) super.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
