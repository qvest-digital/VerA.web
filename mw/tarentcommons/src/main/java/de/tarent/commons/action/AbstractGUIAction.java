package de.tarent.commons.action;

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
