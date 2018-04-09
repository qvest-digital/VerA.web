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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * <p>This class implements the {@link MouseWheelListener}-Interface.</p>
 *
 * <p>It is intended to be an extension to GUI-Components,
 * for using the mouse-wheel to navigate fast and convenient.</p>
 *
 * <p>Inspired by the KDE Konqueror (<a href="http://www.konqueror.org">konqueror.org</a>),
 * but can be found in several Applications and Desktop Environments nowadays.</p>
 *
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */

public abstract class AbstractMouseWheelNavigator implements MouseWheelListener
{
	/**
	 * if we jump from the last index to the first and the other way around
	 */
	private boolean jumpOverBoundaries;

	/**
	 * Same as AbstractMouseWheelNavigator(true)
	 *
	 */

	protected AbstractMouseWheelNavigator()
	{
		this(true);
	}

	/**
	 * A constructor allowing you to determine if we jump over the boundaries (like in KDE) or not (like in GNOME)
	 *
	 * @param pJumpOverBoundaries if we jump from the last index to the first and the other way around
	 */

	protected AbstractMouseWheelNavigator(boolean pJumpOverBoundaries)
	{
		jumpOverBoundaries = pJumpOverBoundaries;
	}

	public void mouseWheelMoved(MouseWheelEvent pEvent)
	{
		// Ensure that the component to be navigated is enabled
		// For example a ComboBox should not be scrollable if its deactived.
		if(!isComponentEnabled()) return;

		// Ensure at least the current component is enabled (otherwise we could get into an infinite loop)
		if(!isValidIndex(getCurrentIndex())) return;

		// get the wheel-rotation
		int notches = pEvent.getWheelRotation();

		// determine if we are moving up or down
		boolean movingUp = (notches >=0);

		// first, simply add the rotation to the current index
		int resultingIndex = getCurrentIndex()+notches;

		// then check if this is between 0 and the maximum index
		resultingIndex = ensureBoundaries(resultingIndex);

		// and finally check if the resulting component is valid (enabled)
		while(!isValidIndex(resultingIndex))
		{
			if(movingUp) resultingIndex++;
			else resultingIndex--;

			// we have to check again, if the desired index is between 0 and the maximum index
			resultingIndex = ensureBoundaries(resultingIndex);
		}

		// after all, set the current selected index
		setIndex(resultingIndex);
	}

	/**
	 * Correctly set index when and end of the tabs has been reached
	 *
	 * @param pResultingIndex the unprooved index
	 * @param pMaxIndex the maximum index of the tabbed-pane
	 * @return the prooven index between 0 and the maximum index
	 */

	protected int ensureBoundaries(int pResultingIndex)
	{
		// check if we have a negative index
		while(pResultingIndex < 0)
		{
			// if we are allowed to jump from the start to the end, do so
			if(jumpOverBoundaries) pResultingIndex = getMaxIndex()+pResultingIndex+1;
			// otherwise, stay at the first index
			else pResultingIndex = 0;
		}

		// check if the index is over maximum index
		while(pResultingIndex > getMaxIndex())
		{
			// if we are allowed to jump from the end to the start, do so
			if(jumpOverBoundaries) pResultingIndex = pResultingIndex-getMaxIndex()-1;
			// otherwise, we stay at the last index
			else pResultingIndex = getMaxIndex();
		}

		// return an index between 0 and the maximum index
		return pResultingIndex;
	}

	/**
	 * The maximum index which the component can have.
	 * @return maximum index
	 */
	protected abstract int getMaxIndex();

	/**
	 * The currently selected index
	 * @return currently selected index
	 */
	protected abstract int getCurrentIndex();

	/**
	 * Sets the selected index
	 * @param pIndex the index to select
	 */
	protected abstract void setIndex(int pIndex);

	/**
	 * If the given index can be selected (e.g. if the component behind is set to "enabled")
	 * @param pIndex the index to check
	 * @return true if the index is selectable
	 */
	protected abstract boolean isValidIndex(int pIndex);

	/**
	 * Check if the underlying component is enabled
	 * @return true if the component is enabled and should be scrollable
	 */
	protected abstract boolean isComponentEnabled();
}
