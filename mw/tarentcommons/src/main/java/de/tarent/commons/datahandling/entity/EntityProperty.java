package de.tarent.commons.datahandling.entity;
/**
 * An <code>EntityProperty</code> describes an {@link Entity}'s
 * property.
 *
 * <p>Subclasses can implement building sets of the properties,
 * localization, etc.</p>
 *
 * <p>Using <code>EntityProperty</code> instances over strings
 * in methods makes those places type-safe.</p>
 *
 * <p>Note for implementors:
 * <ul>
 * <li>instances of subclasses should not be publically creatable</li>
 * <li>subclasses should implement a mechanism that registers the instances
 * with a set or list</li>
 * <li>subclasses should add more specialized query methods</li>
 * </ul>
 * </p>
 */
public abstract class EntityProperty {

    protected final String key;

    /**
     * Creates a new <code>EntityProperty</code> with a
     * fixed name.
     *
     * @param key
     */
    protected EntityProperty(String key) {
        this.key = key;
    }

    /**
     * Returns the property's identifying key.
     *
     * <p>This override allows nicer usage of <code>EntityProperty</code>
     * instances in string concatenation scenarios.</p>
     */
    public final String toString() {
        return key;
    }

    /**
     * Returns the property's identifying key.
     */
    public final String getKey() {
        return key;
    }

    /**
     * Should be implemented to return the property's
     * user readable label translated into the respective
     * language.
     */
    public abstract String getLabel();
}
