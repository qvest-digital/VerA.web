/* $Id: AbstractGUIAction.java,v 1.2 2007/08/17 13:26:05 fkoester Exp $
 *
 * tarent-contact, Plattform-Independent Webservice-Based Contactmanagement
 * Copyright (C) 2002 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-contact'
 * (which makes passes at compilers) written
 * by Sebastian Mancke.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.commons.action;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

/**
 * Einfache Implementierung einer GUI Action.
 *
 * @see TarentGUIAction
 */
public abstract class AbstractGUIAction extends AbstractAction implements TarentGUIAction {

    protected HashMap	containersMap	= new HashMap();

    protected boolean isSelected = false;

	public AbstractGUIAction() {
		super();
	}

	public AbstractGUIAction(String uniqueName, String name) {
		super(name);
		putValue(PROP_KEY_UNIQUE_NAME, uniqueName);
		putValue(PROP_KEY_ACTION_TYPE, TYPE_TRIGGER);
	}

	/**
	 * Template Methode zum Überschreiben in Unterklassen. Diese Methode wird von der ActionRegistry nach dem Erzeugen auf gerufen.
	 */
	protected void init() {
	}

	/**
	 * Liefert einen global eindeutigen Namen, über den die Action angesprochen werden kann.
	 */
	public String getUniqueName() {
		return "" + getValue(PROP_KEY_UNIQUE_NAME);
	}

	/**
	 * Gibt an, ob die Action in dem entsprechenden Container (Menü, Toolbar, ..) angezeigt werden soll.
	 *
	 */
	public boolean isAssignedToContainer(String containerUID) {
		return containersMap.containsKey(containerUID);
	}

	/**
	 * Ordnet einen Container zu, in dem die Action angezeigt werden soll.
	 */
	public void addContainerAssignment(String containerUID, String containerMenuPath) {
		containersMap.put(containerUID, containerMenuPath);
	}

	/**
	 * Liefert den Menübereich, unter dem die Action angezeigt werden soll. Derzeit sind die Konstanten unter MenuBar erlaubt.
	 */
	public String getMenuPath(String containerUID) {
		return (String) containersMap.get(containerUID);
	}

    /**
     * Sets the selected state of all components that have been registered with this
     * action and support that state (= JToggleButton subclasses).
     *
     * @param selected
     */
    public void setSelected( boolean selected ) {
	isSelected = selected;
	Iterator ite = MenuHelper.getSynchronizationComponents( this );
	while ( ite.hasNext() )
	    ( (JToggleButton) ite.next() ).setSelected( selected );

    }

    public boolean isSelected() {
	return isSelected;
    }

    /** Returns an unique name of an action. */
    public String toString() {
	return getUniqueName();
    }

}
