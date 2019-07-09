package de.tarent.commons.datahandling.binding;
/**
 * This abstract implementation of the {@link Binding}
 * interface eases the effort to implement a read-only
 * view connection to the model.
 *
 * <p>The only things you need to do is:
 * <ul>
 * <li>provide the model attribute key at instantiation time</li>
 * <li>implement {@link Binding#setViewData(Object)}</li>
 * </ul>
 *
 * @author Robert Schuster
 */
public abstract class AbstractReadOnlyBinding implements Binding {
    /**
     * In case subclasses want to access the attribute key they
     * do not need to use the {@link #getModelAttributeKey()} and
     * instead read this field instead.
     */
    protected final String modelAttributeKey;

    protected AbstractReadOnlyBinding(String modelAttributeKey) {
        assert (modelAttributeKey != null);

        this.modelAttributeKey = modelAttributeKey;
    }

    /**
     * As this is a read-only binding pushing a view
     * to the model is not needed.
     *
     * <p>The implementation returns <code>null</code>.</p>
     */
    public final Object getViewData() {
        return null;
    }

    /**
     * Returns the model attribute key that was provided
     * to the constructor.
     */
    public final String getModelAttributeKey() {
        return modelAttributeKey;
    }

    /**
     * As this is a read-only binding the value in the
     * view never changes.
     *
     * <p>The implementation returns <code>false</code>.</p>
     */
    public final boolean wasViewModified() {
        return false;
    }

    /**
     * As this is a read-only binding the value in the
     * view never changes and are never pushed back into
     * the model.
     *
     * <p>The implementation returns <code>false</code>.</p>
     */
    public final boolean onChangeWriteToModel() {
        return false;
    }

    /**
     * As this is a read-only binding the implementation
     * returns <code>true</code>.
     */
    public final boolean isReadOnly() {
        return true;
    }
}
