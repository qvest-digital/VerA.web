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
