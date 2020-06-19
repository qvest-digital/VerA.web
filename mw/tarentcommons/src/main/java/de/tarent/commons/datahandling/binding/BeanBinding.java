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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.commons.utils.Converter;
import de.tarent.commons.utils.ConverterRegistry;
import de.tarent.commons.utils.Pojo;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a binding implementation for usage of normal swing components or other bean like classes as view.
 * To use this binding, the view must only have a matching get/set pair to the viewAttributeKey. If the
 * view has only a set-method, then the BeanBinding defaults to readOnly binding.
 *
 * There are two methods of data conversion for the get an set to the model: explicit DataConverter and an automatic data
 * conversion.
 * <h3>explicit DataConverter</h3>
 * If the getViewDataConverter or the setViewDataConverter are set, they are used to convert the data before setting or after
 * getting from the view.
 * <h3>automatic data conversion</h3>
 * If no setViewDataConverter is set, the data is automaticly converted before setting by the
 * {@link de.tarent.commons.utils.Pojo} and {@link de.tarent.commons.utils.ConverterRegistry} api ot the target type of the pojo
 * property.
 * If no getViewDataConverter is set, the data is automaticly converted after retrieving from the view, to the type of data in
 * the first setViewData() operation. It is possible to force this datatype with the forceDataType attribute.
 *
 * TODO: Support the modified flag e.g. by listenting to the right event
 */
public class BeanBinding implements Binding, DataSubject {
    static final Object[] emptyArgs = new Object[] {};

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
        if (setMethod == null) {
            throw new IllegalArgumentException(
              "No method setter method found for binding '" + this + "' in class '" + viewComponent.getClass() + "'");
        }
        if (getMethod == null) {
            readOnly = true;
        }

        registerListener();
    }

    /**
     * TODO: Extend the listener for target components
     */
    protected void registerListener() {
        if (viewComponent instanceof JTextField) {
            ((JTextField) viewComponent).getDocument().addDocumentListener(new DocumentListenerAdapter(this));
        } else if (viewComponent instanceof JTextArea) {
            ((JTextArea) viewComponent).getDocument().addDocumentListener(new DocumentListenerAdapter(this));
        } else if (viewComponent instanceof JCheckBox) {
            ((JCheckBox) viewComponent).addActionListener(new ActionListenerAdapter(this));
        } else if (viewComponent instanceof JRadioButton) {
            ((JRadioButton) viewComponent).addChangeListener(new ChangeListenerAdapter(this));
        } else if (viewComponent instanceof JComboBox &&
          (("selectedItem".equalsIgnoreCase(viewAttributeKey) || "selectedIndex".equalsIgnoreCase(viewAttributeKey)))) {
            ((JComboBox) viewComponent).addActionListener(new ActionListenerAdapter(this));
        }
    }

    public void setViewData(Object data) {
        if (data != null && forceDataType == null) {
            forceDataType = data.getClass();
        }
        if (setViewDataConverter != null) {
            data = setViewDataConverter.convert(data);
        }
        Pojo.set(viewComponent, setMethod, data);
    }

    public Object getViewData() {
        if (!isReadOnly()) {
            Object result = Pojo.get(viewComponent, getMethod);
            if (getViewDataConverter != null) {
                return getViewDataConverter.convert(result);
            }
            if (forceDataType != null) {
                return ConverterRegistry.convert(result, forceDataType);
            }
            return result;
        }
        throw new RuntimeException("getViewData on readonly Binding called " + toString());
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
        if (dataChangedListener == null) {
            return;
        }
        for (Iterator iter = dataChangedListener.iterator(); iter.hasNext(); ) {
            DataChangedListener listener = (DataChangedListener) iter.next();
            listener.dataChanged(e);
        }
    }

    public void addDataChangedListener(DataChangedListener listener) {
        if (dataChangedListener == null) {
            dataChangedListener = new ArrayList(2);
        }
        dataChangedListener.add(listener);
    }

    /**
     * Removes a DataChangedListener
     *
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
          + viewComponent.getClass().getName()
          + ":"
          + viewAttributeKey
          + " <" + writeTo + notRo + " model:"
          + modelAttributeKey
          + ")";
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
