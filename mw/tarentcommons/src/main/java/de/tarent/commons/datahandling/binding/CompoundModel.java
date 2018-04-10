package de.tarent.commons.datahandling.binding;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
