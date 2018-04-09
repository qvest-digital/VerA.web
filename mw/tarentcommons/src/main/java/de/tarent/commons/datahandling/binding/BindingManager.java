/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.datahandling.binding;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This is a controller class for synchronizing a set of views with a model.
 *
 * <p>This impementation was done with two different threads in mind: A background 
 * thread for data loading within the model and an AWT/Swing thread for the views.
 * Actions started in consequence of model events are executed in the AWT thread by 
 * {@see java.awt.EventQueue.invokeLater()}.
 * </p>
 *
 * The controller supports different policies for updating.
 * <ul>
 *   <li>Update the model, on changes of the view</li>
 *   <li>Update the model, request</li>
 *   <li>Update the view once</li>
 *   <li>Update the view always</li>
 * </ul>
 *
 * TODO: We need a good error handling mechanism for this class, becase throwing of exceptions does not make sense on code called by gui events.
 */
public class BindingManager implements DataChangedListener {


    private static final Log logger = LogFactory.getLog(BindingManager.class);


    /** The target model for all bindings */
    Model model;

    /** List of Binding elements */
    List bindings = new ArrayList();

    /** Targets for current operations to avoid event ping pong.
        If the target list is empty, no update is in process */
    ArrayList updateTargets = new ArrayList();
    
    /** 
     * Adds a Binding to the Manager.
     * Imidiately a read to the view will be done.
     * <p>If the supplied Binding implementation implements the Model interface, 
     * the BindingManager registers as DataChangedListener on the binding.</p>
     * 
     */
    public void addBinding(Binding binding) {
        if (logger.isDebugEnabled())
            logger.debug("Add binding "+binding);
        bindings.add(binding);        
        if (binding instanceof DataSubject)
            ((DataSubject)binding).addDataChangedListener(this);
        read(binding);
    }

    /** Reads the binded data from the model to the view */       
    protected void read(Binding binding) {
        if (updateTargets.contains(binding))
            return;
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

    /** Writes the binded data from the view to the model */       
    protected void write(Binding binding) {
        if (binding.isReadOnly())
            return;
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

    /** initialize the view with all data from the model */
    public void readAll() {
        for (Iterator iter = bindings.iterator(); iter.hasNext();) {
            read((Binding)iter.next());
        }
    }

    /** write all modified data of the views back to the model */
    public void writeAll() {
        for (Iterator iter = bindings.iterator(); iter.hasNext();) {
            Binding binding = (Binding)iter.next();
            if (binding.wasViewModified())
                write(binding);
        }
    }
    
    public void dataChanged(final DataChangedEvent e) {
        Object source = e.getSource();
        if (updateTargets.contains(source))
            return;
        
        if (source.equals(model)) {
            // this may be a hack!
            if (! java.awt.EventQueue.isDispatchThread()) {
                // event from the model in an background thread 
                // do view updates in AWT thread
                EventQueue.invokeLater(new Runnable() {
                        public void run() {                    
                            String path = e.getAttributePath();
                            for (Iterator iter = bindings.iterator(); iter.hasNext();) {
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
                                    || (path.startsWith(bindingModelAttributeKey) && path.charAt(bindingModelAttributeKey.length()) == '.')
                                    || (bindingModelAttributeKey.startsWith(path) && (bindingModelAttributeKey.charAt(path.length()) == '.' || path.endsWith("."))))
                                    read(binding);
                            }
                        }
                    });
            } else {
                // the event comes from the model,
                // but was triggered by the swing thread, so we
                // do updates directly to avoid ping-pong events
                String path = e.getAttributePath();
                for (Iterator iter = bindings.iterator(); iter.hasNext();) {
                    Binding binding = (Binding)iter.next();
                    if (path == null || path.startsWith(binding.getModelAttributeKey()) || binding.getModelAttributeKey().startsWith(path))
                        read(binding);
                }
            }
        } 
        else if (source instanceof Binding) {
            Binding binding = (Binding)source;
            // event from the view 
            // do model updates in AWT thread
            if (binding.onChangeWriteToModel())
                write(binding);
        }
    }
    
    
    /** Return the target model for all bindings */
    public Model getModel() {
        return model;
    }
    
    /** Sets the target model for all bindings.  */
    public void setModel(final Model newModel) {

        // If a previous model was set, we deregister from the old model.
        if (model != null)
            model.removeDataChangedListener(this);
        this.model = newModel;
        model.addDataChangedListener(this);
    }
  
}