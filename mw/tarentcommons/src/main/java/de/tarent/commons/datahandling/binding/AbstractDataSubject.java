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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Interface for a subject in an observer pattern.
 */
public class AbstractDataSubject implements DataSubject {

    protected List dataChangedListener;

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
}
