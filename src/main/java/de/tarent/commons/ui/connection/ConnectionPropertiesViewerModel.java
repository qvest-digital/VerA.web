package de.tarent.commons.ui.connection;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 * 
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 *
 */

class ConnectionPropertiesViewerModel {
	
	String label, serverURL, module;
	boolean modifiable;
	
	DefaultListModel connectionPropertiesList = new DefaultListModel();
	
	Runnable updater;
	
	ConnectionPropertiesViewerModel(Runnable updater)
	{
		this.updater = updater;
		label = serverURL = module = "";
		modifiable = false;
	}
	
	void delete(int index)
	{
	  connectionPropertiesList.remove(index);
	}

	void insert(ConnectionProperties cp, int index)
	{
	  connectionPropertiesList.insertElementAt(cp, index);
	}
	
	void add(ConnectionProperties cp)
	{
	  connectionPropertiesList.addElement(cp);
	}
	
	void clear()
	{
	  connectionPropertiesList.clear();
	}
	
	void setSelected(int index)
	{
	  if (index == -1)
	  {
		// After deletion the selected index jumps to -1 which we
		// handle as "no selection".
		label = serverURL = module = "";
		modifiable = false;
	  }
	  else
	  {
		ConnectionProperties cp = (ConnectionProperties) connectionPropertiesList.getElementAt(index);
	    label = cp.label;
	    serverURL = cp.serverURL;
	    module = cp.moduleName;
	    modifiable = cp.modifiable;
	  }
	  
	  updater.run();
	}
	
	ListModel getListModel()
	{
		return connectionPropertiesList;
	}
	
	Renderer getRenderer()
	{
		return new Renderer();
	}
	
	class Renderer extends DefaultListCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 157978706853131134L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			// Prototype is a string and should not be rendered special.
			if (value instanceof String)
			  return this;
			
			ConnectionProperties cp = (ConnectionProperties) value;
			
			setText(cp.label);
			setToolTipText(cp.serverURL);
			if (!cp.modifiable)
			  setFont(getFont().deriveFont(Font.ITALIC));
			
			return this;
		}
	}
}
