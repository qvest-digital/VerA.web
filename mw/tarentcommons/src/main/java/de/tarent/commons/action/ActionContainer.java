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
