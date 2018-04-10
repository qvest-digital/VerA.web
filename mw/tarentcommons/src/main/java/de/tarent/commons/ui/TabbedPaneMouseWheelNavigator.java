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

import javax.swing.JTabbedPane;

/**
 *
 * <p>The TabbedPaneMouseWheelNavigator is intended to be an extension to {@link JTabbedPane}s,
 * for using the mouse-wheel to navigate fast and convenient through the tabs of a {@link JTabbedPane}.</p>
 *
 * <p>Inspired by the KDE Konqueror (<a href="http://www.konqueror.org">konqueror.org</a>),
 * but can be found in several Applications and Desktop Environments nowadays.</p>
 *
 * TODO ensure that tabs are only scrollable if the mouse is over the tab-headers (these things with the tab-title on it) and not on the whole panel of a tab
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */

public class TabbedPaneMouseWheelNavigator extends AbstractMouseWheelNavigator
{
	private JTabbedPane tabbedPane;

	/**
	 * <p>Constructs a ComboBoxMouseWheelNavigator for the given JTabbedPane.</p>
	 * <p>Same as TabbedPaneMouseWheelNavigator(pTabbedPane, true)</p>
	 *
	 * @param pTabbedPane
	 */

	public TabbedPaneMouseWheelNavigator(JTabbedPane pTabbedPane)
	{
		super();
		tabbedPane = pTabbedPane;
	}

	/**
	 * Constructs a TabbedPaneMouseWheelNavigator for the given JTabbedPane. Allows you to determine whether we jump over the boundaries.
	 * @param pTabbedPane the JTabbedPane to navigate on
	 * @param pJumpOverBoundaries whether we jump from the last tab to the first and the other way around
	 */

	public TabbedPaneMouseWheelNavigator(JTabbedPane pTabbedPane, boolean pJumpOverBoundaries)
	{
		super(pJumpOverBoundaries);
		tabbedPane = pTabbedPane;
	}

	protected int getCurrentIndex()
	{
		if(tabbedPane != null) return tabbedPane.getSelectedIndex();
		return 0;
	}

	protected int getMaxIndex()
	{
		if(tabbedPane != null) return tabbedPane.getTabCount()-1;
		return 0;
	}

	protected boolean isValidIndex(int pIndex)
	{
		return (tabbedPane != null && tabbedPane.isEnabledAt(pIndex));
	}

	protected void setIndex(int pIndex)
	{
		if(tabbedPane != null) tabbedPane.setSelectedIndex(pIndex);
	}

	protected boolean isComponentEnabled()
	{
		return (tabbedPane != null && tabbedPane.isEnabled());
	}
}
