package de.tarent.commons.action;

/**
 * Interface to activate all actions to the specified context.
 */
public interface ActionActivationListener {
    
    /** Activates actions only to the specified context.*/
    public void setCurrentActivationContext(String context);
}
