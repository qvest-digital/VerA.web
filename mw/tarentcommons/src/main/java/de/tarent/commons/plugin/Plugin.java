package de.tarent.commons.plugin;

import java.util.List;

/**
 * This interface needs to be implemented by any class
 * that shall be able to register itsself at the PluginRegistry class.
 * @author Steffi Tinder, tarent GmbH
 *
 */
public interface Plugin {

	public String getID();

	/**
	 * called by the container for initialization
	 */
	public void init();

	/**
	 * Returns an implementation of the plugin with the supplied interface
	 */
	public Object getImplementationFor(Class type);

	/**
	 *
	 * @return Returns a List of supported Interfaces
	 */
	public List getSupportedTypes();

	/**
	 * Returns, if the supplied interface is provided by this plugin
	 */
	public boolean isTypeSupported(Class type);

	/**
	 * Returns the name of a plugin to display to the user
	 */
	public String getDisplayName();
}
