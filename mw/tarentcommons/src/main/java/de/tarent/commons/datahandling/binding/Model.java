package de.tarent.commons.datahandling.binding;

import de.tarent.commons.datahandling.entity.*;

/**
 * Interface for a generic model. The method signatures are compatible with the Entity interface.
 * Therefore it is easy to implement an Entity which is a model.
 *
 * <p>The attribute key for a model may be hierarchically organized, depending on the model impementation,
 * using a '.' operator for divinding the steps.</p>
 *
 * <p>Examples for attribute keys:
 * <ul>
 *   <li> <code>name</code> describing a name Attribute</li>
 *   <li> <code>address.street</code> describing the field street in the name structure of the model</li>
 * </ul>
 * </p>
 *
 * TODO: method to test if the data is available
 */
public interface Model extends ReadableAttribute, WritableAttribute, DataSubject {

}
