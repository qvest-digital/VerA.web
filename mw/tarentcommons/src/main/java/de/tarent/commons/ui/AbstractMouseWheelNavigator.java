package de.tarent.commons.ui;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * <p>This class implements the {@link MouseWheelListener}-Interface.</p>
 *
 * <p>It is intended to be an extension to GUI-Components,
 * for using the mouse-wheel to navigate fast and convenient.</p>
 *
 * <p>Inspired by the KDE Konqueror (<a href="http://www.konqueror.org">konqueror.org</a>),
 * but can be found in several Applications and Desktop Environments nowadays.</p>
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 */

public abstract class AbstractMouseWheelNavigator implements MouseWheelListener {
    /**
     * if we jump from the last index to the first and the other way around
     */
    private boolean jumpOverBoundaries;

    /**
     * Same as AbstractMouseWheelNavigator(true)
     */

    protected AbstractMouseWheelNavigator() {
        this(true);
    }

    /**
     * A constructor allowing you to determine if we jump over the boundaries (like in KDE) or not (like in GNOME)
     *
     * @param pJumpOverBoundaries if we jump from the last index to the first and the other way around
     */

    protected AbstractMouseWheelNavigator(boolean pJumpOverBoundaries) {
        jumpOverBoundaries = pJumpOverBoundaries;
    }

    public void mouseWheelMoved(MouseWheelEvent pEvent) {
        // Ensure that the component to be navigated is enabled
        // For example a ComboBox should not be scrollable if its deactived.
        if (!isComponentEnabled()) {
            return;
        }

        // Ensure at least the current component is enabled (otherwise we could get into an infinite loop)
        if (!isValidIndex(getCurrentIndex())) {
            return;
        }

        // get the wheel-rotation
        int notches = pEvent.getWheelRotation();

        // determine if we are moving up or down
        boolean movingUp = (notches >= 0);

        // first, simply add the rotation to the current index
        int resultingIndex = getCurrentIndex() + notches;

        // then check if this is between 0 and the maximum index
        resultingIndex = ensureBoundaries(resultingIndex);

        // and finally check if the resulting component is valid (enabled)
        while (!isValidIndex(resultingIndex)) {
            if (movingUp) {
                resultingIndex++;
            } else {
                resultingIndex--;
            }

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
     * @param pMaxIndex       the maximum index of the tabbed-pane
     * @return the prooven index between 0 and the maximum index
     */

    protected int ensureBoundaries(int pResultingIndex) {
        // check if we have a negative index
        while (pResultingIndex < 0) {
            // if we are allowed to jump from the start to the end, do so
            if (jumpOverBoundaries) {
                pResultingIndex = getMaxIndex() + pResultingIndex + 1;
            }
            // otherwise, stay at the first index
            else {
                pResultingIndex = 0;
            }
        }

        // check if the index is over maximum index
        while (pResultingIndex > getMaxIndex()) {
            // if we are allowed to jump from the end to the start, do so
            if (jumpOverBoundaries) {
                pResultingIndex = pResultingIndex - getMaxIndex() - 1;
            }
            // otherwise, we stay at the last index
            else {
                pResultingIndex = getMaxIndex();
            }
        }

        // return an index between 0 and the maximum index
        return pResultingIndex;
    }

    /**
     * The maximum index which the component can have.
     *
     * @return maximum index
     */
    protected abstract int getMaxIndex();

    /**
     * The currently selected index
     *
     * @return currently selected index
     */
    protected abstract int getCurrentIndex();

    /**
     * Sets the selected index
     *
     * @param pIndex the index to select
     */
    protected abstract void setIndex(int pIndex);

    /**
     * If the given index can be selected (e.g. if the component behind is set to "enabled")
     *
     * @param pIndex the index to check
     * @return true if the index is selectable
     */
    protected abstract boolean isValidIndex(int pIndex);

    /**
     * Check if the underlying component is enabled
     *
     * @return true if the component is enabled and should be scrollable
     */
    protected abstract boolean isComponentEnabled();
}
