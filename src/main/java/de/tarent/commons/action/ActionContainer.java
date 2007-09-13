/* $Id: ActionContainer.java,v 1.1 2007/08/17 11:20:23 fkoester Exp $
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

import javax.swing.Action;

/**
 * Interface for container of actions.
 */
public interface ActionContainer {

    /**
     * Returns an identifier for this container. 
     */
    public String getContainerUniqueName();

    /** 
     * Adds an action to this Container.
     * 
     * @param action - The concrete instance may be one of TarentGUIAction
     * @param menuPath - It describes where in the container this action should be attached.
     * @throws ActionContainerException if registering of a given action failed  
     */
    public void attachGUIAction(Action action, String menuPath) throws ActionContainerException;

    /**
     * Removes the action from this container.
     * The container may implement this feature, but it's not required.
     * @param action - The concrete instance may be one of TarentGUIAction
     */
    public void removeGUIAction(Action action);
    
    /**
     * Initialize actions for this ActionContainer if the Container has been added to ActionRegistry after action-definition already exists
     *
     */
    public void initActions() throws ActionContainerException ;
}