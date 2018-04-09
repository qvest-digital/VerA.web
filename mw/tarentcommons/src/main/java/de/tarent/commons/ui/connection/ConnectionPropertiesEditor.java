package de.tarent.commons.ui.connection;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.ui.CommonDialogButtons;
import de.tarent.commons.ui.EscapeDialog;
import de.tarent.commons.ui.Messages;

/**
 *
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 *
 */

class ConnectionPropertiesEditor extends EscapeDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 2569284015031831219L;

	private boolean cancelled = true;

	private JTextField connectionLabel;

	private JTextField serverURL;

	private JTextField module;

	private ConnectionPropertiesViewer parent;

	private String editLabel;

	private ActionListener actionListener;

	String connectionLabelCopy, serverURLCopy, moduleCopy;

	ConnectionPropertiesEditor(ConnectionPropertiesViewer parent)
	{
		super(parent, true);

		this.parent = parent;

		init();
	}

	public boolean wasCancelled()
	{
		return cancelled;
	}

	private void init()
	{
		initComponents();

		pack();
		setLocationRelativeTo(getOwner());
	}

	void initEntry(String connectionLabel, String serverURL, String module)
	{
		if(connectionLabel != null && connectionLabel.trim().length() != 0)
			editLabel = connectionLabel;

		this.connectionLabel.setText(connectionLabel);
		this.serverURL.setText(serverURL);
		this.module.setText(module);
	}

	private void initComponents()
	{
		setTitle(Messages.getString("ConnectionPropertiesEditor_Title"));

		connectionLabel = new JTextField(30);
		serverURL = new JTextField();
		module = new JTextField();

		Container cp = getContentPane();
		FormLayout l = new FormLayout(
				"3dlu, pref, 3dlu, 150dlu:grow, 3dlu", // columns
				"3dlu, pref, 3dlu, pref, 3dlu, pref, 6dlu, pref, 3dlu"); // rows
		cp.setLayout(l);

		CellConstraints cc = new CellConstraints();

		cp.add(new JLabel(Messages.getString("ConnectionPropertiesEditor_ConnectionLabel")), cc.xy(2, 2));
		cp.add(connectionLabel, cc.xy(4,2));

		cp.add(new JLabel(Messages.getString("ConnectionPropertiesEditor_ServerURL")), cc.xy(2, 4));
		cp.add(serverURL, cc.xy(4, 4));

		cp.add(new JLabel(Messages.getString("ConnectionPropertiesEditor_Module")), cc.xy(2, 6));
		cp.add(module, cc.xy(4,6));

		cp.add(CommonDialogButtons.getSubmitCancelButtons(getActionListener(), getRootPane()), cc.xyw(2, 8, 3));
	}

	protected ActionListener getActionListener() {
		if(actionListener == null) {
			actionListener = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("submit")) {
						cancelled = false;

						connectionLabelCopy = connectionLabel.getText().trim();
						serverURLCopy = serverURL.getText().trim();
						moduleCopy = module.getText().trim();

						if (connectionLabelCopy.length() == 0)
						{
						  JOptionPane.showMessageDialog(
								  ConnectionPropertiesEditor.this,
								  Messages.getString("ConnectionPropertiesEditor_LabelInvalid_Message"));
						  return;
						}

						if (serverURLCopy.length() == 0)
						{
						  JOptionPane.showMessageDialog(
								  ConnectionPropertiesEditor.this,
								  Messages.getString("ConnectionPropertiesEditor_ServerURLInvalid_Message"));
						  return;
						}

						if (moduleCopy.length() == 0)
						{
						  JOptionPane.showMessageDialog(
								  ConnectionPropertiesEditor.this,
								  Messages.getString("ConnectionPropertiesEditor_ModuleInvalid_Message"));
						  return;
						}

						// if we are not in edit mode or the label has just been modified,
						// check if a connection with this label is already existent

						if(editLabel == null || !connectionLabelCopy.equals(editLabel))
						{

							if (parent.isLabelAlreadyExistent(connectionLabelCopy))
							{
								JOptionPane.showMessageDialog(
										ConnectionPropertiesEditor.this,
										Messages.getString("ConnectionPropertiesEditor_LabelAlreadyExistent_Message"));
								return;
							}
						}

		                ConnectionPropertiesEditor.this.setVisible(false);
					}
					else if(e.getActionCommand().equals("cancel")) {
						cancelled = true;

						ConnectionPropertiesEditor.this.setVisible(false);
					}
				}
			};
		}
		return actionListener;
	}

	public void setVisible(boolean b)
	{
		if (b)
		{
			// Set to true, the default state after closing (which may happen
			// by pressing ESC).
			cancelled = true;
		}

		super.setVisible(b);

		if (!b)
		{
			// Resets the textfields (for later reuse).
			initEntry("", "", "");
			editLabel = null;
		}
	}
}
