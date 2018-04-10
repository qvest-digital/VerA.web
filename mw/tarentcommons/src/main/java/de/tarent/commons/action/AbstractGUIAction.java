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
