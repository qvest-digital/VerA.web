package de.tarent.commons.ui.connection;

/**
 * 
 * @author Robert Schuster (r.schuster@tarent.de) tarent GmbH Bonn
 *
 */

class ConnectionProperties {
	
	String label;
	String serverURL;
	String moduleName;
	
	boolean modifiable;
	
	ConnectionProperties(String label,
			String serverURL, String moduleName, boolean modifiable)
	{
		this.label = label;
		this.serverURL = serverURL;
		this.moduleName = moduleName;
		this.modifiable = modifiable;
	}

}
