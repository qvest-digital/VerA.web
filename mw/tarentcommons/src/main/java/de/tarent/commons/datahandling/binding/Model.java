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
