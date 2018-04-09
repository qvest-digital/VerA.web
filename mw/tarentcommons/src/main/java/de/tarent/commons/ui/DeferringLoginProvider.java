/**
 * 
 */
package de.tarent.commons.ui;

import java.awt.event.ActionListener;

/**
 * 
 * An interface for classes providing login-credentials based on user-input (like Login-Dialogs)
 * 
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public interface DeferringLoginProvider extends LoginProvider
{
	/**
	 * If the class implementing this interface has something like a text-status-information
	 * use this method to show some text to the user
	 * 
	 * @param pText the text to be shown
	 */
	public void setStatusText(String pText);
	
	
	public void setStatus(int status);
	
	/**
	 * <p>Adds an <code>ActionListener</code> to this LoginProvider</p>
	 * 
	 * <p>The registered ActionListeners might receive one of the following <code>ActionEvents</code>:
	 * <ul>
	 * <li><b>"login"</b> if the user requested a login <em>or</em></li>
	 * <li><b>"cancel"</b> if the user requested to cancel the login-proces</li>
	 * </ul>
	 * </p>
	 * 
	 * @param pActionListener the <code>ActionListener</code> to register
	 */
	public void addActionListener(ActionListener pActionListener);
	
	/**
	 * Removes an <code>ActionListener</code> from this LoginProvider
	 * 
	 * @param pActionListener the <code>ActionListener</code> to remove
	 */
	public void removeActionListener(ActionListener pActionListener);
	
	/**
	 * If the user-interface should be visible
	 * 
	 * @param pVisible true makes the user-interface visible
	 */
	public void setDialogVisible(boolean pVisible);
	
	public void reset();
	
	public void setSessionContinuable(boolean pContinuable);
}
