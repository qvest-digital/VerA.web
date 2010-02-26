/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */
package de.tarent.ldap.prefs;

import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * @author kirchner
 * 
 * Eine PreferencesFactory-Implementierung für LDAP.
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