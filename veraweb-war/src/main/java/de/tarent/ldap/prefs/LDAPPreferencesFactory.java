/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.ldap.prefs;

import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * @author kirchner
 * 
 * Eine PreferencesFactory-Implementierung f�r LDAP.
 */
public class LDAPPreferencesFactory implements PreferencesFactory {

	private static Properties	LDAPProperties	= new Properties();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.prefs.PreferencesFactory#systemRoot()
	 */
	public Preferences systemRoot() {
		return new LDAPSystemPreferences(null, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.prefs.PreferencesFactory#userRoot()
	 */
	public Preferences userRoot() {
		return new LDAPUserPreferences(null, "");
	}

	/**
	 * @return Returns the LDAPProperties.
	 */
	public static Properties getLDAPProperties() {
		return LDAPProperties;
	}

	/**
	 * @param properties
	 *            The LDAPProperties to set.
	 */
	public static void setLDAPProperties(Properties properties) {
		LDAPProperties = properties;
	}
}
