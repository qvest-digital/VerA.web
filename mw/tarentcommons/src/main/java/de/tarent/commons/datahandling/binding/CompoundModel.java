package de.tarent.commons.datahandling.binding;
import java.util.HashMap;

import de.tarent.commons.datahandling.entity.EntityException;
import de.tarent.commons.datahandling.entity.EntityList;
import de.tarent.commons.utils.Pojo;

/**
 * This class implements a generic compound model that can store hierarchically
 * organized model structures.
 *
 * <p>The compoundModel can be used to store different Models in the same data structures,
 * each can be accessed through a string key while a "." marks the diffeent hierarchical units</p>
 * <p> The easiest way to use a CompoundModel ist to extend it and to add problem specific getters, stters and other methods</p>
 *
 * @author Steffi Tinder, tarent GmbH
 */
public class CompoundModel extends AbstractModel implements DataChangedListener {

    /**
     * Models by theire path (prefix)
     */
    private HashMap subModels;

    /**
     * Stores the prefix of a submodel by the model
     */
    private HashMap subModelPaths;

    public CompoundModel() {
        subModels = new HashMap();
        subModelPaths = new HashMap();
    }

    /**
     * retrieves an Attribute with regard to a possible hierarchically organized model structure
     */
    public Object getAttribute(String key) throws EntityException {
        if (subModels.containsKey(key)) {
            return subModels.get(key);
        }
        if (key.indexOf('.') == -1) {
            //no dot contained in key
            return subModels.get(key);
        } else {
            int firstdot = key.indexOf('.');
            String first = key.substring(0, firstdot);
            key = key.substring(firstdot + 1);
            Object submodel = subModels.get(first);
            if (submodel == null) {
                return null;
            }
            if (submodel instanceof Model) {
                return ((Model) submodel).getAttribute(key);
            }
            return Pojo.get(submodel, key);
        }
    }

    /**
     * sets an Attribute with regard to a possible hierarchically organized model structure
     */
    public void setAttribute(String key, Object value) throws EntityException {
        removeDataChangedListenerIfNecessary(key);
        if (key.indexOf('.') == -1) {
            registerObject(key, value);
            fireDataChanged(new DataChangedEvent(this, key));
        } else {
            int firstdot = key.indexOf('.');
            String first = key.substring(0, firstdot);
            key = key.substring(firstdot + 1);
            if (subModels.containsKey(first)) {
                Object subModel = subModels.get(first);
                if (subModel == null) {
                    throw new RuntimeException("no submodel for key: '" + first + "' found");
                }
                if (subModel instanceof Model) {
                    ((Model) subModel).setAttribute(key, value);
                } else {
                    Pojo.set(subModel, key, value);
                }
            }
        }
    }

    public void dataChanged(DataChangedEvent e) {
        Object submodelPrefix = subModelPaths.get(e.getSource());
        if (submodelPrefix != null) {
            String path = submodelPrefix + "." + e.getAttributePath();
            fireDataChanged(new DataChangedEvent(this, path));
        }
        // else: do not push the event
    }

    //----- getters & setters ....

    public Model getModel(String key) {
        return (Model) subModels.get(key);
    }

    public HashMap getModels() {
        return subModels;
    }

    public void registerModel(String key, Model model) {
        removeDataChangedListenerIfNecessary(key);
        model.addDataChangedListener(this);
        subModels.put(key, model);
        subModelPaths.put(model, key);
    }

    public void deregisterModel(String key) {
        removeDataChangedListenerIfNecessary(key);
        Object model = subModels.remove(key);
        subModelPaths.remove(model);
    }

    public EntityList getEntityList(String key) {
        return (EntityList) subModels.get(key);
    }

    public void registerEntityList(String key, EntityList list) {
        if (list instanceof DataSubject) {
            ((DataSubject) list).addDataChangedListener(this);
        }
        removeDataChangedListenerIfNecessary(key);
        subModels.put(key, list);
        subModelPaths.put(list, key);
    }

    public void deregisterEntityList(String key) {
        removeDataChangedListenerIfNecessary(key);
        Object model = subModels.remove(key);
        subModelPaths.remove(model);
    }

    public Object getObject(String key) {
        return subModels.get(key);
    }

    public void registerObject(String key, Object obj) {
        if (obj instanceof DataSubject) {
            ((DataSubject) obj).addDataChangedListener(this);
        }
        removeDataChangedListenerIfNecessary(key);
        subModels.put(key, obj);
        subModelPaths.put(obj, key);
    }

    public void deregisterObject(String key) {
        removeDataChangedListenerIfNecessary(key);
        Object model = subModels.remove(key);
        subModelPaths.remove(model);
    }

    private void removeDataChangedListenerIfNecessary(String key) {
        if ((subModels.containsKey(key)) && (subModels.get(key) instanceof DataSubject)) {
            ((DataSubject) subModels.get(key)).removeDataChangedListener(this);
        }
    }
}
