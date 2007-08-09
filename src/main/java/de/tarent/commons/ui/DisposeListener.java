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

package de.tarent.commons.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DisposeListener implements MouseListener {
	protected Component component;
	
	public DisposeListener(Component component) {
		this.component = component;
	}

    /**
     * Do nothing when user click the component
	 * in the default implementation.
     */
	public void mouseClicked(MouseEvent e) {
		// nothing in the default implementation
	}

	/**
	 * Do nothing when mouse enter the component
	 * in the default implementation.
	 */
	public void mouseEntered(MouseEvent e) {
		// nothing in the default implementation
	}

	/**
	 * Do nothing when mouse exit the component
	 * in the default implementation.
	 */
	public void mouseExited(MouseEvent e) {
		// nothing in the default implementation
	}

	/**
	 * Do nothing on mouse butten will be pressed
	 * in the default implementation.
	 */
	public void mousePressed(MouseEvent e) {
		// nothing in the default implementation
	}

	/**
	 * Do nothing on mouse button will be released
	 * in the default implementation.
	 */
	public void mouseReleased(MouseEvent e) {
		// nothing in the default implementation
	}

	/**
	 * Dispose the given component. 
	 */
	protected void dispose() {
		Container container = component.getParent();
		if (container != null) {
			container.remove(component);
		}
		if (component.isVisible()) {
			component.setVisible(false);
		}
		if (component instanceof Window) {
			((Window)component).dispose();
		}
	}
}
