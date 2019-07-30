package de.tarent.commons.datahandling.binding;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
