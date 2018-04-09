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

/**
 * Binding is an abstraction for the connection between a model and a view.
 * The binding stores, which attribute of the model is mapped to the view.
 * The binding provides methods to store an retrieve the data from the view.
 *
 * <p>Depending on the implementation, the binding may convert the data to
 * other formats proper to the view.</p>
 */
public interface Binding {

    /** Sets the data to the view according to the binding. */
    public void setViewData(Object data);

    /** Returns the data from the view according to the binding. */
    public Object getViewData();

    /**
     * Returns the bindings attribute key in the model.
     * This attribute key is a "data-path-describtor" for the data on which the view is bound by this binding.
     * Depending on the model, the attribute key may be hierarchically organized.
     *
     * @return the attribute key which is <code>!= null</code>
     */
    public String getModelAttributeKey();

    /**
     * Reports, if the data in the view was modified since it was set from the model.
     * The implementation of this method is optional. If the feature is not supported
     * by the underlaying implementation, wasViewModified should always return <code>true</code>.
     */
    public boolean wasViewModified();

    /**
     * Flag to indicate, that modifications in the view should always be directly
     * written to the model by a BindingManager. If this flag has the state <code>onChangeWriteToModel == false</code>,
     * a write back should only take place on explicit request.
     */
    public boolean onChangeWriteToModel();

    /**
     * Flag to indicate if a Binding should only be used to transfer data from the model to the view
     */
    public boolean isReadOnly();
}
