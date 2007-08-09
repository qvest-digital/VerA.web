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

import java.lang.reflect.Method;
import java.util.*;

import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import de.tarent.commons.utils.*;    
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

/**
 * This is a binding implementation for usage of normal swing components or other bean like classes as view.
 * To use this binding, the view must only have a matching get/set pair to the viewAttributeKey. If the 
 * view has only a set-method, then the BeanBinding defaults to readOnly binding.
 *
 * <p>There are two methods of data conversion for the get an set to the model: explicit DataConverter and an automatic data conversion.
 * <h3>explicit DataConverter</h3>
 * If the getViewDataConverter or the setViewDataConverter are set, they are used to convert the data before setting or after getting from the view.
 * <h3>automatic data conversion</h3>
 * If no setViewDataConverter is set, the data is automaticly converted before setting by the {@see de.tarent.commons.utils.Pojo} and {@see de.tarent.commons.utils.ConverterRegistry} api ot the target type of the pojo property.
 * If no getViewDataConverter is set, the data is automaticly converted after retrieving from the view, to the type of data in the first setViewData() operation. It is possible to force this datatype with the forceDataType attribute.
 * </p>
 *
 *
 * TODO: Support the modified flag e.g. by listenting to the right event
 */
public class BeanBinding implements Binding, DataSubject {

    static final Object[] emptyArgs = new Object[]{};
    
    Object viewComponent;
    String viewAttributeKey;
    Method setMethod;
    Method getMethod;
    String modelAttributeKey;
    boolean onChangeWriteToModel = true;
    boolean readOnly = false;
    List dataChangedListener;
    
    /**
     * The datatype to which the data is converted after retrieving from the view, if no converter is set.
     * If this value is not set, it will be set to the supplied datatype in the first after the firt setViewData() call.
     */
    Class forceDataType;

    /**
     * Converter for conversion of the data befor getting it to the view.
     * If no such converter is supplied, an auto converting mechanism is used.
     */
    Converter getViewDataConverter;

    /**
     * Converter for conversion of the data befor setting it to the view
     * If no such converter is supplied, an auto converting mechanism is used.
     */
    Converter setViewDataConverter;
    
    
    public BeanBinding(Object viewComponent, String viewAttributeKey, String modelAttributeKey) {
        this.viewComponent = viewComponent;
        this.viewAttributeKey = viewAttributeKey;
        this.modelAttributeKey = modelAttributeKey;

        getMethod = Pojo.getGetMethod(viewComponent, viewAttributeKey, true);  
        setMethod = Pojo.getSetMethod(viewComponent, viewAttributeKey, true);  
        if (setMethod == null)
            throw new IllegalArgumentException("No method setter method found for binding '"+this+"' in class '"+viewComponent.getClass()+"'");
        if (getMethod == null)
            readOnly = true;
        
        registerListener();        
    }

    /**
     * TODO: Extend the listener for target components
     */
    protected void registerListener() {
        if (viewComponent instanceof JTextField)
            ((JTextField)viewComponent).getDocument().addDocumentListener(new DocumentListenerAdapter(this));
        else if (viewComponent instanceof JTextArea)
            ((JTextArea)viewComponent).getDocument().addDocumentListener(new DocumentListenerAdapter(this));
        else if (viewComponent instanceof JCheckBox)
            ((JCheckBox)viewComponent).addActionListener(new ActionListenerAdapter(this));
        else if (viewComponent instanceof JRadioButton)
            ((JRadioButton)viewComponent).addChangeListener(new ChangeListenerAdapter(this));
        else if (viewComponent instanceof JComboBox && (("selectedItem".equalsIgnoreCase(viewAttributeKey) || "selectedIndex".equalsIgnoreCase(viewAttributeKey)) )) {
            ((JComboBox)viewComponent).addActionListener(new ActionListenerAdapter(this));
        }
    }
    
    public void setViewData(Object data) {
        if (data != null && forceDataType == null)
            forceDataType = data.getClass();        
        if (setViewDataConverter != null)
            data = setViewDataConverter.convert(data);            
        Pojo.set(viewComponent, setMethod, data);
    }
    
    public Object getViewData() {
        if (!isReadOnly()) {
            Object result = Pojo.get(viewComponent, getMethod);
            if (getViewDataConverter != null)
                return getViewDataConverter.convert(result);
            if (forceDataType != null)
                return ConverterRegistry.convert(result, forceDataType);
            return result;
        }
        throw new RuntimeException("getViewData on readonly Binding called "+toString());
    }
    
    public String getModelAttributeKey() {
        return modelAttributeKey;
    }

    public String getViewAttributeKey() {
        return viewAttributeKey;
    }
    
    public boolean wasViewModified() {
        return true;
    }
    
    public void setOnChangeWriteToModel(boolean newOnChangeWriteToModel) {
        this.onChangeWriteToModel = newOnChangeWriteToModel;
    }

    public boolean onChangeWriteToModel() {
        return onChangeWriteToModel;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
    
    public void setReadOnly(boolean newReadOnly) {
        this.readOnly = newReadOnly;
    }

    protected void fireDataChanged(DataChangedEvent e) {
        if (dataChangedListener == null)
            return;
        for (Iterator iter = dataChangedListener.iterator(); iter.hasNext();) {
            DataChangedListener listener = (DataChangedListener)iter.next();
            listener.dataChanged(e);
        }
    }    
    
    public void addDataChangedListener(DataChangedListener listener) {
        if (dataChangedListener == null)
            dataChangedListener = new ArrayList(2);
        dataChangedListener.add(listener);
    }    
    
    /**
     * Removes a DataChangedListener
     * @param listener The registered listener
     */
    public void removeDataChangedListener(DataChangedListener listener) {
        if (dataChangedListener != null) {
            dataChangedListener.remove(listener);
        }
    }

    /**
     * Sets the datatype to which the data is converted after retrieving from the view, if no converter is set.
     * If this value is not set, it will be set to the supplied datatype in the first after the firt setViewData() call.
     */
    public void setForceDataType(Class type) {
        forceDataType = type;
    }

    /**
     * Returns the datatype to which the data is converted after retrieving from the view, if no converter is set.
     * If this value is not set, it will be set to the supplied datatype in the first after the firt setViewData() call.
     */
    public Class getForceDataType() {
        return forceDataType;
    }

    /**
     * Returns the converter for conversion of the data befor setting it to the view.
     * If no such converter is supplied, an auto converting mechanism is used.
     */
    public Converter getSetViewDataConverter() {
        return setViewDataConverter;
    }

    /**
     * Sets the converter for conversion of the data befor setting it to the view.
     * If no such converter is supplied, an auto converting mechanism is used.
     */
    public void setSetViewDataConverter(Converter newSetViewDataConverter) {
        this.setViewDataConverter = newSetViewDataConverter;
    }
    
    /**
     * Returns the converter for conversion of the data befor getting it to the view.
     * If no such converter is supplied, an auto converting mechanism is used.
     */
    public Converter getGetViewDataConverter() {
        return getViewDataConverter;
    }

    /**
     * Sets the converter for conversion of the data befor getting it to the view.
     * If no such converter is supplied, an auto converting mechanism is used.
     */
    public void setGetViewDataConverter(Converter newGetViewDataConverter) {
        this.getViewDataConverter = newGetViewDataConverter;
    }
    

    public String toString() {
        String writeTo = onChangeWriteToModel ? "=" : "-";
        String notRo = readOnly ? "" : ">";
        
        return "("
            +viewComponent.getClass().getName()
            +":" 
            +viewAttributeKey
            +" <"+writeTo+notRo+ " model:" 
            +modelAttributeKey
            +")";
    }

    protected class DocumentListenerAdapter implements DocumentListener {
        
        BeanBinding parentBinding;
        public DocumentListenerAdapter(BeanBinding parentBinding) {
            this.parentBinding = parentBinding;
        }
        
        void fire() {
            parentBinding.fireDataChanged(new DataChangedEvent(parentBinding, parentBinding.getModelAttributeKey()));
        }
        
        public void changedUpdate(DocumentEvent e) {
            fire();
        }
        
        public void insertUpdate(DocumentEvent e) {
            fire();
        }

        public void removeUpdate(DocumentEvent e) {
            fire();            
        }
    }

    protected class ChangeListenerAdapter implements ChangeListener {
        
        BeanBinding parentBinding;
        
        public ChangeListenerAdapter(BeanBinding parentBinding) {
            this.parentBinding = parentBinding;
        }
        
        void fire() {
            parentBinding.fireDataChanged(new DataChangedEvent(parentBinding, parentBinding.getModelAttributeKey()));
        }
        
        public void stateChanged(ChangeEvent e) {
            fire();
        }
    }

    protected class ActionListenerAdapter implements ActionListener {
        
        BeanBinding parentBinding;
        
        public ActionListenerAdapter(BeanBinding parentBinding) {
            this.parentBinding = parentBinding;
        }
        
        void fire() {
            parentBinding.fireDataChanged(new DataChangedEvent(parentBinding, parentBinding.getModelAttributeKey()));
        }
        
        public void actionPerformed(ActionEvent e) {
            fire();
        }
    }
}