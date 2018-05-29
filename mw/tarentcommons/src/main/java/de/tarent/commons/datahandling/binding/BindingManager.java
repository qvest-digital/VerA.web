package de.tarent.commons.datahandling.binding;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a controller class for synchronizing a set of views with a model.
 *
 * This impementation was done with two different threads in mind: A background
 * thread for data loading within the model and an AWT/Swing thread for the views.
 * Actions started in consequence of model events are executed in the AWT thread by
 * {@link java.awt.EventQueue#invokeLater(Runnable)}
 *
 * The controller supports different policies for updating.
 * <ul>
 * <li>Update the model, on changes of the view</li>
 * <li>Update the model, request</li>
 * <li>Update the view once</li>
 * <li>Update the view always</li>
 * </ul>
 *
 * TODO: We need a good error handling mechanism for this class, becase throwing of exceptions does not make sense on code
 * called by gui events.
 */
public class BindingManager implements DataChangedListener {
    private static final Log logger = LogFactory.getLog(BindingManager.class);

    /**
     * The target model for all bindings
     */
    Model model;

    /**
     * List of Binding elements
     */
    List bindings = new ArrayList();

    /**
     * Targets for current operations to avoid event ping pong.
     * If the target list is empty, no update is in process
     */
    ArrayList updateTargets = new ArrayList();

    /**
     * Adds a Binding to the Manager.
     * Imidiately a read to the view will be done.
     * <p>If the supplied Binding implementation implements the Model interface,
     * the BindingManager registers as DataChangedListener on the binding.</p>
     */
    public void addBinding(Binding binding) {
        if (logger.isDebugEnabled()) {
            logger.debug("Add binding " + binding);
        }
        bindings.add(binding);
        if (binding instanceof DataSubject) {
            ((DataSubject) binding).addDataChangedListener(this);
        }
        read(binding);
    }

    /**
     * Reads the binded data from the model to the view
     */
    protected void read(Binding binding) {
        if (updateTargets.contains(binding)) {
            return;
        }
        updateTargets.add(binding);
        try {
            binding.setViewData(model.getAttribute(binding.getModelAttributeKey()));
        } catch (IllegalArgumentException e) {
            logger.warn("Error on writing binded data", e);
        } catch (Exception e) {
            logger.error("Error on reading binded data", e);
        } finally {
            updateTargets.remove(binding);
        }
    }

    /**
     * Writes the binded data from the view to the model
     */
    protected void write(Binding binding) {
        if (binding.isReadOnly()) {
            return;
        }
        updateTargets.add(binding);
        try {
            model.setAttribute(binding.getModelAttributeKey(), binding.getViewData());
        } catch (IllegalArgumentException e) {
            logger.warn("Error on writing binded data", e);
        } catch (Exception e) {
            logger.error("Error on writing binded data", e);
        } finally {
            updateTargets.remove(binding);
        }
    }

    /**
     * initialize the view with all data from the model
     */
    public void readAll() {
        for (Iterator iter = bindings.iterator(); iter.hasNext(); ) {
            read((Binding) iter.next());
        }
    }

    /**
     * write all modified data of the views back to the model
     */
    public void writeAll() {
        for (Iterator iter = bindings.iterator(); iter.hasNext(); ) {
            Binding binding = (Binding) iter.next();
            if (binding.wasViewModified()) {
                write(binding);
            }
        }
    }

    public void dataChanged(final DataChangedEvent e) {
        Object source = e.getSource();
        if (updateTargets.contains(source)) {
            return;
        }

        if (source.equals(model)) {
            // this may be a hack!
            if (!java.awt.EventQueue.isDispatchThread()) {
                // event from the model in an background thread
                // do view updates in AWT thread
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        String path = e.getAttributePath();
                        for (Iterator iter = bindings.iterator(); iter.hasNext(); ) {
                            Binding binding = (Binding) iter.next();
                            String bindingModelAttributeKey = binding.getModelAttributeKey();

                            // The view update is done if the following conditions hold:
                            // - the datachangedevent's model attribute key is identical to the one
                            //   in the current binding
                            // - the datachangedevent's model attribute key is a parent of the one
                            //   in the current binding (e.g. "main.employees.count" changes, thus
                            //   "main.employees" is updated as well).
                            //   This means that a change of a subkey provokes a change in the parent
                            // - the current binding model attribute key is a parent of the one in
                            //   the datachangedevent. This means a change to the parent provokes
                            //   changes in the children (update to "main.employees" updates "main.employees.count"
                            //   & "main.employees.whatever", too)
                            if (path == null
                              || path.equals(bindingModelAttributeKey)
                              || (path.startsWith(bindingModelAttributeKey) &&
                              path.charAt(bindingModelAttributeKey.length()) == '.')
                              || (bindingModelAttributeKey.startsWith(path) &&
                              (bindingModelAttributeKey.charAt(path.length()) == '.' || path.endsWith(".")))) {
                                read(binding);
                            }
                        }
                    }
                });
            } else {
                // the event comes from the model,
                // but was triggered by the swing thread, so we
                // do updates directly to avoid ping-pong events
                String path = e.getAttributePath();
                for (Iterator iter = bindings.iterator(); iter.hasNext(); ) {
                    Binding binding = (Binding) iter.next();
                    if (path == null || path.startsWith(binding.getModelAttributeKey()) ||
                      binding.getModelAttributeKey().startsWith(path)) {
                        read(binding);
                    }
                }
            }
        } else if (source instanceof Binding) {
            Binding binding = (Binding) source;
            // event from the view
            // do model updates in AWT thread
            if (binding.onChangeWriteToModel()) {
                write(binding);
            }
        }
    }

    /**
     * Return the target model for all bindings
     */
    public Model getModel() {
        return model;
    }

    /**
     * Sets the target model for all bindings.
     */
    public void setModel(final Model newModel) {

        // If a previous model was set, we deregister from the old model.
        if (model != null) {
            model.removeDataChangedListener(this);
        }
        this.model = newModel;
        model.addDataChangedListener(this);
    }
}
