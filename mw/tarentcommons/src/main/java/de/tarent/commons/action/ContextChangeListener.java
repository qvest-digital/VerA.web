package de.tarent.commons.action;

/**
 * A context change listener should be registered in
 * {@link ActionRegistry} in order to be notified about context changes.
 * <p>
 * At the moment there is only one <code>ContextChangeListener</code>:
 * {@link de.tarent.contact.gui.MainFrameExtStyle}.
 * <p>
 *
 * @see de.tarent.contact.gui.action.ActionRegistry#addContextChangeListener(ContextChangeListener)
 *
 * @author Aleksej Palij (a.palij@tarent.de), tarent GmbH Bonn
 */
public interface ContextChangeListener {

    /**
     * Should handle context activation/deactivation.
     * It will be invoked by the {@link de.tarent.contact.gui.action.ActionRegistry}
     * after every context change.
     *
     * @param context that currently changed
     * @param isEnabled is 'true' if has been enabled
     */
    public void contextChanged( String context, boolean isEnabled );

}
