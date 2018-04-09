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

package de.tarent.commons.datahandling;

import java.lang.reflect.Method;

/**
 * Interface for a controler for pojos
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public interface GenericPojoManager {

    /**
     * This method is called when arbitrary methods are invoked on the generic pojo.
     *
     * @param pojo the called pojo
     * @param theGenericPojo the generic pojo implementation for the pojo
     * @param method the called method
     * @param args the suplied params the the call
     * @return the result for the call
     */
    public Object methodCalled(Object pojo, GenericPojo theGenericPojo, Method method, Object[] args);

}
