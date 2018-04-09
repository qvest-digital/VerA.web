/**
 *
 */
package de.tarent.commons.ui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.config.ConfigManager;
import de.tarent.commons.config.ConnectionDefinition;
import de.tarent.commons.config.Environment.Key;
import de.tarent.commons.ui.connection.ViewConnectionPropertiesAction;

/**
 *
 * <p>Provides a login screen that is displayed right after the
 * start and which allows the user to select the database connection
 * as well providing her login credentials.</p>
 *
 * <p>Other classes can register <code>ActionListeners</code> via
 * the <code>addActionListener</code> method, if they are interested
 * in events like "User clicked on Login-Button" or "User clicked on Cancel-Button"
 *
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 *
 */
public class LoginDialog extends JFrame implements DeferringLoginProvider
{
	/**
	 *
	 */
	private static final long serialVersionUID = 657372095408198428L;
	protected JPanel mainPanel;
	protected JButton loginButton;
	protected JButton cancelButton;
	protected JButton infoButton;
	protected JButton connectionPropertiesButton;
	protected JTextField userNameField;
	protected JPasswordField passwordField;
	protected JCheckBox continueSessionBox;
	protected JComboBox connectionBox;
	protected JProgressBar progressBar;
	protected JPanel buttonPanel;
	protected JPanel progressBarPanel;
	protected JLabel continueSessionLabel;
	protected JLabel connectionLabel;
	protected JLabel userNameLabel;
	protected JLabel passwordLabel;

	protected ActionListener actionListener;

	protected List actionListeners;
	protected int actionIDCounter;

	protected Collection connectionDefinitions;

	protected static Logger logger = Logger.getLogger(LoginDialog.class.getName());

	protected boolean currentlyLoggingIn = false;

	public LoginDialog()
	{
		this(null, null, null);
	}

	public LoginDialog(String initialUser, String initialConnection, Image icon) {
		this(initialUser, initialConnection, icon, null);
	}

	public LoginDialog(String pInitialUser, String initialConnection, Image icon, String appTitle)
	{
		super(Messages.getString("LoginDialog_Title") + (appTitle == null ? "" : (" - " + appTitle)));

		if(icon != null)
			setIconImage(icon);

		connectionDefinitions = ConfigManager.getEnvironment().getConnectionDefinitions();
		setSelectedConnection(initialConnection);

		if(pInitialUser != null)
			getUserNameField().setText(pInitialUser);

		getContentPane().add(getMainPanel());
		getRootPane().setDefaultButton(getLoginButton());

		addWindowListener(new LoginDialogWindowListener());

		pack();
		setLocationRelativeTo(null);
	}

	protected List getActionListeners()
	{
		if(actionListeners == null)
			actionListeners = new ArrayList();

		return actionListeners;
	}

	public void addActionListener(ActionListener pListener)
	{
		getActionListeners().add(pListener);
	}

	public void removeActionListener(ActionListener pListener)
	{
		getActionListeners().remove(pListener);
	}

	public void fireActionEvent(final String pActionCommand)
	{
		final Iterator it = getActionListeners().iterator();

		while(it.hasNext())
			((ActionListener)it.next()).actionPerformed(new ActionEvent(this, actionIDCounter++, pActionCommand));
	}

	public void fireLoginRequested()
	{
		fireActionEvent("login");
	}

	public void fireCancelRequested()
	{
		fireActionEvent("cancel");
	}

	public void fireQuitRequested()
	{
		fireActionEvent("quit");
	}

	public void fireSelectedConnectionChanged()
	{
		fireActionEvent("connection_changed");
	}

	public JPanel getMainPanel()
	{
		if(mainPanel == null)
		{
			/* The dialog layout is described in the "de.tarent.contact.gui.StartScreen.odg" document which
			 * can be found in src/site/apt.
			 */
			FormLayout layout = new FormLayout(
					"pref, 3dlu, pref:grow",  // Columns.
					"pref, 3dlu, pref, 10dlu, pref, 3dlu, pref, pref, 10dlu:grow, pref" // Rows.
			);

			PanelBuilder builder = new PanelBuilder(layout);

			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.add(getConnectionLabel(), cc.xy(1, 1));
			builder.add(getConnectionBox(), cc.xy(3, 1));

			// hide option to reconnect if session-use is disabled in environment-configuration
			if(ConfigManager.getEnvironment() != null && ConfigManager.getEnvironment().getAsBoolean(Key.USE_OCTOPUS_SESSION_COOKIE)) {
				builder.add(getContinueSessionLabel(), cc.xy(1, 3));
				builder.add(getContinueSessionBox(), cc.xy(3, 3));
			}

			builder.add(getUserNameLabel(), cc.xy(1, 5));
			builder.add(getUserNameField(), cc.xy(3, 5));

			builder.add(getPasswordLabel(), cc.xy(1, 7));
			builder.add(getPasswordField(), cc.xy(3, 7));

			builder.add(getProgressBarPanel(), cc.xyw(1, 8, 3));

			builder.add(getButtonPanel(), cc.xyw(1, 10, 3));

			mainPanel = builder.getPanel();
		}

		return mainPanel;
	}

	protected JLabel createPlainLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		return label;
	}

	protected JPanel getButtonPanel()
	{
		if(buttonPanel == null)
		{
			FormLayout layout = new FormLayout(
					"80dlu, 3dlu, pref, 6dlu:grow, right:pref",  // Columns.
					"pref" // Rows.
			);

			PanelBuilder builder = new PanelBuilder(layout);

			CellConstraints cc = new CellConstraints();

			builder.add(getLoginButton(), cc.xy(1, 1));
			builder.add(getConnectionPropertiesButton(), cc.xy(3, 1));
			builder.add(getCancelButton(), cc.xy(5, 1));

			buttonPanel = builder.getPanel();
		}
		return buttonPanel;
	}

	protected ActionListener getActionListener()
	{
		if(actionListener == null)
			actionListener = new LoginDialogActionListener();

		return actionListener;
	}

	protected JPanel getProgressBarPanel()
	{
		if(progressBarPanel == null)
		{
			FormLayout layout = new FormLayout(
					"fill:pref:grow",  // Columns.
					"10dlu, pref" // Rows.
			);

			PanelBuilder builder = new PanelBuilder(layout);

			CellConstraints cc = new CellConstraints();

			builder.add(getProgressBar(), cc.xy(1, 2));

			progressBarPanel = builder.getPanel();
			progressBarPanel.setVisible(false);
		}
		return progressBarPanel;
	}

	protected JProgressBar getProgressBar()
	{
		if(progressBar == null)
		{
			progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setString("");
		}
		return progressBar;
	}

	protected JButton getLoginButton()
	{
		if(loginButton == null)
		{
			loginButton = new JButton(Messages.getString("LoginDialog_Button_Ok"));
			loginButton.addActionListener(getActionListener());
			loginButton.setMnemonic('l');

			KeyStroke stroke = KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK);
			getRootPane().registerKeyboardAction( new ActionListener() {

				public void actionPerformed(ActionEvent e)
				{
					if(getContinueSessionBox().isEnabled())
						getContinueSessionBox().setSelected(true);

					getLoginButton().doClick();
				}

			}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
		}
		return loginButton;
	}

	protected JButton getCancelButton()
	{
		if(cancelButton == null)
		{
			cancelButton = new JButton(Messages.getString("LoginDialog_Button_Quit"));
			cancelButton.setToolTipText(Messages.getString("LoginDialog_Button_Quit_ToolTip"));
			cancelButton.addActionListener(getActionListener());
			cancelButton.setMnemonic('c');
		}
		return cancelButton;
	}

	protected JButton getConnectionPropertiesButton()
	{
		if(connectionPropertiesButton == null)
		{
			final ViewConnectionPropertiesAction action = new ViewConnectionPropertiesAction(this);
			connectionPropertiesButton = new JButton(Messages.getString("LoginDialog_Button_ConnectionProperties"));
			connectionPropertiesButton.setToolTipText(Messages.getString("LoginDialog_Button_ConnectionProperties_ToolTip"));
			connectionPropertiesButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					// initialize viewer (invoked by action) with the currently selected connection-definition
                    ConnectionDefinition selectedConnection = (ConnectionDefinition) getConnectionBox().getSelectedItem();

                    if(selectedConnection != null) //if at least one connection defined
                        action.setPreferredConnection(selectedConnection.get(ConnectionDefinition.Key.LABEL));

					action.actionPerformed(ae);
					if (action.wasCancelled())
					  return;

					// Updates the connection box' content after knowing that
					// the connection definitions must have changed.
					connectionDefinitions = ConfigManager.getEnvironment().getConnectionDefinitions();

					getConnectionBox().setModel(new DefaultComboBoxModel(
							connectionDefinitions.toArray()));

					setSelectedConnection(action.getPreferredConnection());

					// need to adapt the size of the dialog to eventually changed
					// width of the connections-box
					LoginDialog.this.pack();

					// The dialog should also be centered again
					LoginDialog.this.setLocationRelativeTo(null);
				}
			});

			connectionPropertiesButton.setMnemonic('e');
		}
		return connectionPropertiesButton;
	}

	protected JPasswordField getPasswordField()
	{
		if(passwordField == null)
			passwordField = new JPasswordField();

		return passwordField;
	}

	protected JLabel getPasswordLabel()
	{
		if(passwordLabel == null)
		{
			passwordLabel = new JLabel(Messages.getString("LoginDialog_Password"));
			passwordLabel.setLabelFor(getPasswordField());
			passwordLabel.setDisplayedMnemonic('p');
		}
		return passwordLabel;
	}

	protected JTextField getUserNameField()
	{
		if(userNameField == null)
			userNameField = new JTextField();

		return userNameField;
	}

	protected JLabel getUserNameLabel()
	{
		if(userNameLabel == null)
		{
			userNameLabel = new JLabel(Messages.getString("LoginDialog_Username"));
			userNameLabel.setLabelFor(getUserNameField());
			userNameLabel.setDisplayedMnemonic('u');
		}
		return userNameLabel;
	}

	protected JCheckBox getContinueSessionBox()
	{
		if(continueSessionBox == null)
		{
			continueSessionBox = new JCheckBox();
			continueSessionBox.setToolTipText(Messages.getString("LoginDialog_Continue_ToolTip"));
			continueSessionBox.setEnabled(false);
			continueSessionBox.setHorizontalTextPosition(SwingConstants.LEFT);
			continueSessionBox.addActionListener(getActionListener());
		}
		return continueSessionBox;
	}

	protected JLabel getContinueSessionLabel()
	{
		if(continueSessionLabel == null)
		{
			continueSessionLabel = createPlainLabel(Messages.getString("LoginDialog_Continue"));
			continueSessionLabel.setLabelFor(getContinueSessionBox());
			continueSessionLabel.setEnabled(false);
			// TODO does not apply to english I18n
			continueSessionLabel.setDisplayedMnemonic('w');
		}
		return continueSessionLabel;
	}

	protected JComboBox getConnectionBox()
	{
		if(connectionBox == null)
		{
			connectionBox = new JComboBox(connectionDefinitions.toArray());
			connectionBox.addMouseWheelListener(new ComboBoxMouseWheelNavigator(connectionBox));
			connectionBox.addActionListener(getActionListener());
		}
		return connectionBox;
	}

	protected JLabel getConnectionLabel()
	{
		if(connectionLabel == null)
		{
			connectionLabel = new JLabel(Messages.getString("LoginDialog_Connection"));
			connectionLabel.setLabelFor(getConnectionBox());
			// TODO does not apply to english I18n
			connectionLabel.setDisplayedMnemonic('v');
		}
		return connectionLabel;
	}

	public void setStatusText(String pText)
	{
		getProgressBar().setString(pText);
	}

	public void setStatus(int status)
	{
		getProgressBar().setValue(status);
	}

	public void setDialogVisible(boolean pVisible)
	{
		if(pVisible)
			initFocus();

		setVisible(pVisible);

		if(!pVisible)
			dispose();
	}

	protected void initFocus()
	{
		if(getUserNameField().getText().length() > 0)
		{
			getPasswordField().requestFocus();
			getPasswordField().selectAll();
		}
		else
		{
			getUserNameField().requestFocus();
			getUserNameField().selectAll();
		}
	}

	public ConnectionDefinition getConnectionDefinition()
	{
		return (ConnectionDefinition)getConnectionBox().getSelectedItem();
	}

	public String getPassword()
	{
		return new String(getPasswordField().getPassword());
	}

	public String getUserName()
	{
		return getUserNameField().getText();
	}

	public boolean shouldContinueSession()
	{
		return getContinueSessionBox().isSelected();
	}

	protected class LoginDialogActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent pEvent)
		{
			if(pEvent.getSource().equals(getCancelButton()))
			{
				if(currentlyLoggingIn)
					fireCancelRequested();
				else
					fireQuitRequested();
			}
			else if(pEvent.getSource().equals(getLoginButton()))
			{
				if (connectionDefinitions.isEmpty()) {
					JOptionPane.showMessageDialog(LoginDialog.this,
							Messages.getString("LoginDialog_NoConnections_Message"),
							Messages.getString("LoginDialog_NoConnections_Title"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				setControlsEnabled(false);
				getProgressBarPanel().setVisible(true);
				getCancelButton().setText(Messages.getString("LoginDialog_Button_Cancel"));
				getCancelButton().setToolTipText(Messages.getString("LoginDialog_Button_Cancel_ToolTip"));
				LoginDialog.this.pack();
				currentlyLoggingIn = true;
				fireLoginRequested();
			}
			else if(pEvent.getSource().equals(getConnectionBox()))
			{
				// disable continue-session-checkbox
				// maybe enabled by Starter after check again
				getContinueSessionBox().setSelected(false);
				getContinueSessionBox().setEnabled(false);
				getContinueSessionLabel().setEnabled(false);

				// eventually need to enable username and password-field
				getUserNameField().setEnabled(true);
				getUserNameLabel().setEnabled(true);
				getPasswordField().setEnabled(true);
				getPasswordLabel().setEnabled(true);

				fireSelectedConnectionChanged();
			}
			else if(pEvent.getSource().equals(getContinueSessionBox()))
			{
				getUserNameField().setEnabled(!getContinueSessionBox().isSelected());
				getUserNameLabel().setEnabled(!getContinueSessionBox().isSelected());
				getPasswordField().setEnabled(!getContinueSessionBox().isSelected());
				getPasswordLabel().setEnabled(!getContinueSessionBox().isSelected());
			}
		}
	}

	public void reset()
	{
		getProgressBar().setValue(100);
		getProgressBar().setString("");

		int seconds = ConfigManager.getEnvironment().getAsInt(Key.SLEEP_SECONDS, 3);
		new SleepScreen(this, Messages.getString("SleepScreen_Title"), Messages.getString("SleepScreen_Message"), seconds);

		getCancelButton().setText(Messages.getString("LoginDialog_Button_Quit"));
		getCancelButton().setToolTipText(Messages.getString("LoginDialog_Button_Quit_ToolTip"));
		getPasswordField().setText("");
		setControlsEnabled(true);
		getContinueSessionBox().setSelected(false);
		getContinueSessionBox().setEnabled(false);
		getProgressBar().setValue(0);
		getProgressBar().setString("");
		getProgressBarPanel().setVisible(false);
		pack();
		initFocus();
		currentlyLoggingIn = false;
		fireSelectedConnectionChanged();
	}

	protected void setSelectedConnection(String pInitialConnection)
	{
		for(int i=0; i < getConnectionBox().getItemCount(); i++)
		{
			if(getConnectionBox().getItemAt(i).toString().equals(pInitialConnection))
			{
				getConnectionBox().setSelectedIndex(i);
				break;
			}
		}
	}

	public void setSessionContinuable(boolean pContinuable)
	{
		if(!currentlyLoggingIn)
		{
			getContinueSessionBox().setEnabled(pContinuable);
			getContinueSessionLabel().setEnabled(pContinuable);
		}
	}

	protected void setControlsEnabled(boolean pEnabled)
	{
		getLoginButton().setEnabled(pEnabled);
		getConnectionPropertiesButton().setEnabled(pEnabled);
		getConnectionBox().setEnabled(pEnabled);
		getConnectionLabel().setEnabled(pEnabled);
		getContinueSessionBox().setEnabled(pEnabled);
		getContinueSessionLabel().setEnabled(pEnabled);
		getUserNameField().setEnabled(pEnabled);
		getUserNameLabel().setEnabled(pEnabled);
		getPasswordField().setEnabled(pEnabled);
		getPasswordLabel().setEnabled(pEnabled);
	}

	protected class LoginDialogWindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			fireCancelRequested();
			super.windowClosing(e);
		}
	}
}
