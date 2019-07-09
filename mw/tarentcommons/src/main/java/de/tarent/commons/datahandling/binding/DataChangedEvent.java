package de.tarent.commons.datahandling.binding;
import java.util.EventObject;

/**
 * Event class for events describing a change in the data of a model, such as an entity.
 *
 * <p>This event has an attributePath property, which describes which data in the underlaying model has changed,
 * the value of the attribute may be organized hierarchically, with the '.' operator. Than a change in an superior
 * data item means also a change in all underlaying items. If the attribute property is <code>null</code>
 * or the empty string, all data has changed.</p>
 *
 * <p>Examples for attribute descriptions:
 * <ul>
 * <li> <code>name</code> describing a name Attribute</li>
 * <li> <code>address.street</code> describing the field street in the name structure of the model</li>
 * </ul>
 * </p>
 */
public class DataChangedEvent extends EventObject {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 9017031537257068083L;
    String attributePath;

    public DataChangedEvent(Object source, String attributePath) {
        super(source);
        this.attributePath = attributePath;
    }

    public String getAttributePath() {
        return attributePath;
    }
}
