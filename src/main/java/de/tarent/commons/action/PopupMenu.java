/* $Id: PopupMenu.java,v 1.1 2007/09/08 22:18:52 fkoester Exp $
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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;


/**
 * Implementation of a JPopupMenu as an ActionContainer.
 * 
 * Actions may be appended by an menuPath:
 * <pre>
 * menuPath :== MenuName ("/" MenuName)* (":" Position)?
 * MenuName :== &lt;Name&gt;
 * Position :== [0-9]+
 * </pre>
 * 
 * The Menus will be created as needed. A missing position or a position of -1 means appending at the end.
 * The Position of the Actions is not forced. A later appended Action may relocate other actions. 
 *
 * @see de.tarent.contact.gui.action.ActionRegistry
 */
public class PopupMenu extends JPopupMenu implements ActionContainer {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3371448570419300838L;

	private static final Logger logger = Logger.getLogger( PopupMenu.class.getName() );

    protected String uniqueName;
    protected Map menus = new HashMap();
    protected ResourceBundle resourceBundle;
    protected static final String DEFAULT_MENU = "Extras";
    private static final String PRIORITY_KEY = "menuBar.items.priority.key";

    public PopupMenu( String uniqueName, ResourceBundle resourceBundle ) {
        this.uniqueName = uniqueName;
        this.resourceBundle = resourceBundle;
    }

    /**
     * Returns container's unique name.
     */
    public String getContainerUniqueName() {
        return uniqueName;
    }

    /** 
     * Adds the given action to a menu bar at assigned position.
     * 
     * @param action The action to attach
     * @param menuPath The path and rules to attach, as described above
     */
    public void attachGUIAction( Action action, String menuPath ) {
        JMenuItem item;
        String actionType = (String) action.getValue( AbstractGUIAction.PROP_KEY_ACTION_TYPE );

        if ( AbstractGUIAction.TYPE_CHECK.equals( actionType ) ) {
            item = new JCheckBoxMenuItem( action );
            // Adds the checkbox to the list of elements whose selection state
            // can be set through AbstractGUIAction.setSelected(boolean)
            MenuHelper.addSynchronizationComponent( action, (JCheckBoxMenuItem) item );

        }
        else if ( AbstractGUIAction.TYPE_CHOISE.equals( actionType ) ) {
            item = new JRadioButtonMenuItem( action );

        }
        else if ( AbstractGUIAction.TYPE_SEPARATOR.equals( actionType ) ) {
            addSeparator( menuPath );
            return;//READY

        }
        else {
            item = new JMenuItem( action );
        }
        // set component name (for later ecxeption handling)
        item.setName( (String) action.getValue( AbstractGUIAction.NAME ) );
        addItem( item, menuPath, MenuHelper.isWithFrontSeparator( action ), MenuHelper.isWithBackSeparator( action ) );
    }

    private void addSeparator( String menuPath ) {
        addSeparator();
    }

    public void removeGUIAction( Action action ) {
        logger.warning( "optional, not implemented method yet: removeGUIAction" );
    }
    
    public void initActions() {
    	Iterator mbIt = ActionRegistry.getInstance().getActions(getContainerUniqueName()).iterator();
		
		while(mbIt.hasNext())
		{
			TarentGUIAction action = (TarentGUIAction)mbIt.next();
			attachGUIAction(action, action.getMenuPath(getContainerUniqueName()));
		}
    }

    /**
     * @param item The item to attach
     * @param menuPath The path and rules to attach, as described above
     * @param withBackSeparator
     */
    public void addItem( JMenuItem item, String menuPath, boolean withFrontSeparator, boolean withBackSeparator ) {
        int priority = MenuHelper.getAssignedPriority( removeMainMenuPriority(menuPath) );

        item.getAction().putValue( PRIORITY_KEY, new Integer( priority ) );

        int pos = MenuHelper.getInsertPosition( this.getComponents(), PRIORITY_KEY, new Integer( priority ) );
        if ( withFrontSeparator )
            add( new JSeparator(), pos++ );

        logger.fine( "[menuBar]: " + item.getAction() + ": pos=" + String.valueOf( pos ) + " path=" + menuPath );
        add( item, pos++ );

        if ( withBackSeparator )
            add( new JSeparator(), pos );
    }
    
    private String removeMainMenuPriority(String menuPath)
    {
    	String[] menuPathParts = null;
    	if(menuPath != null)
    		menuPathParts = menuPath.split(":");
    	else
    		return null;
    	
    	// check if the menuPath starts with a digit
    	if(menuPathParts != null && menuPathParts.length > 0 && Character.isDigit(menuPathParts[0].charAt(0)))
    	{
    		// this menuPath contains a priority-value for the main-menu. remove it
    		return menuPath.substring(menuPath.indexOf(':')+1);
    	}
    	
    	// does not contain a priority-value for the main-menu. return without change
    	return menuPath;
    }
}