/*
 * $Id: LDAPPreferencesFactory.java,v 1.6 2004/05/24 16:52:05 philipp Exp $
 * 
 * Created on 12.05.2004 by kirchner
 * 
 * tarent-contact, Plattform-Independent Webservice-Based Contact Management
 * Copyright (C) 2004 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright interest in the program
 * 'tarent-octopus' (which makes passes at compilers) written by Philipp
 * Kirchner. signature of Elmar Geese, 1 June 2002 Elmar Geese, CEO tarent GmbH
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