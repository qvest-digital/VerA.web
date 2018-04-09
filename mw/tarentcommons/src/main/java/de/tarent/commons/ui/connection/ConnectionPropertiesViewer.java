package de.tarent.commons.ui.connection;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.tarent.commons.config.ConnectionDefinition;
import de.tarent.commons.config.ConnectionDefinition.Key;
import de.tarent.commons.ui.CommonDialogButtons;
import de.tarent.commons.ui.EscapeDialog;
import de.tarent.commons.ui.Messages;

/**
 * 
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 *
 */

public class ConnectionPropertiesViewer extends EscapeDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5827220017771955633L;

	private ConnectionPropertiesViewerModel model;
	
	ActionListener actionListener;
	
	JList entries;
	
	JTextField connectionLabel;
	
	JTextField serverURL;
	
	JTextField module;
	
	JButton newButton, deleteButton, editButton;
	
	boolean cancelled;
	
	ConnectionPropertiesEditor editor;

	public ConnectionPropertiesViewer(JDialog parent)
	{
		super(parent, true);
		
		init();
	}

	public ConnectionPropertiesViewer(JFrame parent)
	{
		super(parent, true);
		
		init();
	}
	
	public boolean wasCancelled()
	{
		return cancelled;
	}
	
	public String getPreferredConnection()
	{
		if(entries != null && entries.getSelectedIndex() != -1)
			return ((ConnectionProperties)entries.getSelectedValue()).label;
		
		return null;
	}
	
	public void initEntries(Collection/*<ConnectionDefinition>*/ fixed, Collection/*<ConnectionDefinition>*/ modifiable)
	{
		model.clear();

		Iterator ite = fixed.iterator();
		while (ite.hasNext())
		{
			ConnectionDefinition cd = (ConnectionDefinition) ite.next();
			model.add(new ConnectionProperties(
					cd.get(Key.LABEL),
					cd.get(Key.SERVER_URL),
					cd.get(Key.OCTOPUS_MODULE),
					false));
		}

		ite = modifiable.iterator();
		while (ite.hasNext())
		{
			ConnectionDefinition cd = (ConnectionDefinition) ite.next();
			
			model.add(new ConnectionProperties(
					cd.get(Key.LABEL),
					cd.get(Key.SERVER_URL),
					cd.get(Key.OCTOPUS_MODULE),
					true));
		}
	}
	
	public Collection/*<ConnectionDefinition>*/ getModifiableEntries()
	{
		Enumeration e = model.connectionPropertiesList.elements();
		LinkedList ll = new LinkedList();
		while (e.hasMoreElements())
		{
			ConnectionProperties cp = (ConnectionProperties) e.nextElement();
			if (cp.modifiable)
			  ll.add(new ConnectionDefinition(cp.label, cp.serverURL, cp.moduleName));
		}
		
		return ll;
	}
	
	private void init()
	{
		editor = new ConnectionPropertiesEditor(this);
		model = new ConnectionPropertiesViewerModel(new Runnable() { public void run(){ update(); } });
		initComponents();
		
		pack();
		setLocationRelativeTo(getOwner());
	}
	
	private void initComponents()
	{
		setTitle(Messages.getString("ConnectionPropertiesViewer_Title"));
		entries = new JList(model.getListModel());
		// Fixes the lists' width to display 35 characters and solves a
		// layout problem (= list got bigger after it was filled and
		// components on the right were not displayed completely). So
		// be carefull if you fiddle with this.
		entries.setPrototypeCellValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		entries.setCellRenderer(model.getRenderer());
		
		entries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entries.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent arg) {
				if (!arg.getValueIsAdjusting())
				  model.setSelected(entries.getSelectedIndex());
			}
		});
		
		connectionLabel = new JTextField();
		connectionLabel.setEditable(false);
		
		serverURL = new JTextField();
		serverURL.setEditable(false);
		
		module = new JTextField(30);
		module.setEditable(false);
		
		newButton = new JButton(Messages.getString("ConnectionPropertiesViewer_New"));
		newButton.setToolTipText(Messages.getString("ConnectionPropertiesViewer_New_ToolTip"));
		newButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				editor.setVisible(true);
				
				if (editor.wasCancelled())
				  return;
				
				ConnectionProperties cp = new ConnectionProperties(editor.connectionLabelCopy,
						editor.serverURLCopy, editor.moduleCopy, true);
				
				model.add(cp);
			}
		});
		
		editButton = new JButton(Messages.getString("ConnectionPropertiesViewer_Edit"));
		editButton.setToolTipText(Messages.getString("ConnectionPropertiesViewer_Edit_ToolTip"));
		editButton.setEnabled(false);
		editButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				ConnectionProperties cp = (ConnectionProperties) entries.getSelectedValue();
				editor.initEntry(cp.label, cp.serverURL, cp.moduleName);
				
				// will block until the user closes the editor-dialog
				editor.setVisible(true);
				
				if (editor.wasCancelled())
				  return;
				
				cp.label = editor.connectionLabelCopy;
				cp.serverURL = editor.serverURLCopy;
				cp.moduleName = editor.moduleCopy;
				
				// The list model knows no way to refresh itself when the properties
				// of an entry have updated. So we remove the entry and insert it at
				// the same position to cause a proper update.
				int index = entries.getSelectedIndex();
				model.delete(index);
				model.insert(cp, index);
				
				// Causes the updated values of cp being written into the
				// model and then the textfields.
				entries.setSelectedIndex(index);
			}
		});
		
		deleteButton = new JButton(Messages.getString("ConnectionPropertiesViewer_Delete"));
		deleteButton.setToolTipText(Messages.getString("ConnectionPropertiesViewer_Delete_ToolTip"));
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				model.delete(entries.getSelectedIndex());
			}
		});
		
		FormLayout l = new FormLayout("3dlu, pref:grow, 3dlu, pref, 3dlu, 120dlu:grow, 3dlu", // columns
		"3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu"); // rows

		Container cp = getContentPane();
		cp.setLayout(l);
		
		CellConstraints cc = new CellConstraints();
		cp.add(new JScrollPane(entries), cc.xywh(2, 2, 1, 7));

		cp.add(new JLabel(Messages.getString("ConnectionPropertiesViewer_ConnectionLabel")), cc.xy(4, 2));
		cp.add(connectionLabel, cc.xy(6, 2));

		cp.add(new JLabel(Messages.getString("ConnectionPropertiesViewer_ServerURL")), cc.xy(4, 4));
		cp.add(serverURL, cc.xy(6, 4));

		cp.add(new JLabel(Messages.getString("ConnectionPropertiesViewer_Module")), cc.xy(4, 6));
		cp.add(module, cc.xy(6, 6));
		
		PanelBuilder pb = new PanelBuilder(new FormLayout("pref, 3dlu:grow, pref, 3dlu:grow, pref", "pref"));
		pb.add(newButton, cc.xy(1, 1));
		pb.add(editButton, cc.xy(3, 1));
		pb.add(deleteButton, cc.xy(5, 1));
		
		cp.add(pb.getPanel(), cc.xyw(4, 8, 3));
		
		cp.add(CommonDialogButtons.getSubmitCancelButtons(getActionListener(), getRootPane()), cc.xyw(2, 10, 5));
	}
	
	protected ActionListener getActionListener() {
		if(actionListener == null) {
			actionListener = new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("submit")) {
						cancelled = false;
						ConnectionPropertiesViewer.this.setVisible(false);
					}
					else if(e.getActionCommand().equals("cancel")) {
						cancelled = true;
						ConnectionPropertiesViewer.this.setVisible(false);
					}
				}
			};
		}
		return actionListener;
	}
	
	private void update()
	{
		connectionLabel.setText(model.label);
		serverURL.setText(model.serverURL);
		module.setText(model.module);
		
		editButton.setEnabled(model.modifiable);
		deleteButton.setEnabled(model.modifiable);
	}
	
	public void setVisible(boolean b)
	{
		super.setVisible(b);

		if (!b)
		{
			connectionLabel.setText("");
			serverURL.setText("");
			module.setText("");
		}
	}
	
	/**
	 * 
	 * Determines whether the given label is already used for another connection-definition
	 * 
	 * @param connectionLabel the label to check
	 * @return true if the label is already existent
	 */

	public boolean isLabelAlreadyExistent(String connectionLabel)
	{
		for(int i=0; i < entries.getModel().getSize(); i++)
		{
			ConnectionProperties props = (ConnectionProperties)entries.getModel().getElementAt(i);
			if(props.label.equals(connectionLabel))
				return true;
		}
		return false;
	}
	
	/**
	 * Sets the connection the viewer will show when it becomes visible
	 * 
	 * @param connectionLabel the label identifying the connection to show initially
	 */

	public void setPreferredConnection(String connectionLabel)
	{
		// need to clear selection, otherwise setSelectedValue above
		// does not refill the property-fields when selection did not change
		entries.clearSelection();
		
		for(int i=0; i < entries.getModel().getSize(); i++)
		{
			ConnectionProperties props = (ConnectionProperties)entries.getModel().getElementAt(i);
			if(props.label.equals(connectionLabel))
			{
				entries.setSelectedValue(props, true);
				break;
			}
		}
	}
}
