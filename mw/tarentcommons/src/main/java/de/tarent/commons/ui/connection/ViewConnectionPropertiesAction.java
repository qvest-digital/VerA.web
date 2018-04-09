package de.tarent.commons.ui.connection;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.JFrame;

import de.tarent.commons.action.AbstractGUIAction;
import de.tarent.commons.config.ConfigManager;
import de.tarent.commons.richClient.ApplicationServices;

/**
 *
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 *
 */

public class ViewConnectionPropertiesAction extends AbstractGUIAction{

	/**
	 *
	 */
	private static final long serialVersionUID = 5547713729445994305L;

	private ConnectionPropertiesViewer viewer;

	private JFrame owner;

	/**
	 * This is the constructor used by the <code>ActionRegistry</code>
	 * framework solely.
	 *
	 */
	public ViewConnectionPropertiesAction()
	{
	}

	/**
	 * Creates a {@link ViewConnectionPropertiesAction} with an explicit
	 * owner for the {@link ConnectionPropertiesViewer} dialog it creates.
	 *
	 * <p>This allows usage of the action outside the framework of the
	 * <code>ActionRegistry</code>. This in turn is needed because
	 * configuring the connection properties should be possible at
	 * login time, where the <code>ActionRegistry</code> is not yet
	 * initialized.</code></p>
	 *
	 * @param owner
	 */
	public ViewConnectionPropertiesAction(JFrame owner)
	{
		this.owner = owner;
	}

	private ConnectionPropertiesViewer getViewer()
	{
		ApplicationServices as = ApplicationServices.getInstance();

		if (viewer == null)
		  {
			JFrame frame = owner;
			if (frame == null && as.getCommonDialogServices() != null)
				frame = as.getCommonDialogServices().getFrame();

		    viewer = new ConnectionPropertiesViewer(frame);

		    Collection fixed = ConfigManager.getEnvironment().getFixedConnectionDefinitions();
			Collection modifiable = ConfigManager.getEnvironment().getModifiableConnectionDefinitions();
			getViewer().initEntries(fixed, modifiable);
		  }
		return viewer;
	}

	public void actionPerformed(ActionEvent arg) {
		getViewer().setVisible(true);

		// At this point the dialog has been closed.

		// If no change was requested, then well, do nothing. :)
		if (getViewer().wasCancelled())
		  return;

		// Receive modified connection properties
		Collection modifiable = getViewer().getModifiableEntries();

		// Updates the in-memory values.
		ConfigManager.getEnvironment().setModifiableConnectionDefinitions(modifiable);

		// Writes the updated configuration documents.
		ConfigManager.getInstance().store();
	}

	/**
	 * Returns whether the action was cancelled (no update to
	 * the connection definitions took place) or <code>true</code>
	 * when the action has not been invoked at least once.
	 *
	 * <p>Note: Return value of this method makes sense <b>only</b>
	 * immediately after the invocation of
	 * {@link #actionPerformed(ActionEvent)}</p>.
	 *
	 * <p>The use of the method is to find out whether the action
	 * had an effect or not.</p>
	 *
	 * <p>TODO: Make this smarter so it can determine whether the
	 * user's inputs resemble a value change in the strictest sense.</p>
	 *
	 * @return
	 */
	public boolean wasCancelled()
	{
		return getViewer().wasCancelled();
	}

	/**
	 * Returns the label of the connection-definition the user is
	 * probably going to use
	 *
	 * @return the label of an connection-definition or null if none could be determined
	 */

	public String getPreferredConnection()
	{
		if (getViewer().getPreferredConnection() != null)
			return getViewer().getPreferredConnection();

		// if no connection is selected in the viewer return the lastly used connection
		return ConfigManager.getPreferences().node("connection.last").get("name", null);
	}

	/**
	 * Sets the connection the viewer will show when it becomes visible
	 *
	 * @param connectionLabel the label identifying the connection to show initially
	 */

	public void setPreferredConnection(String connectionLabel)
	{
		getViewer().setPreferredConnection(connectionLabel);
	}
}
