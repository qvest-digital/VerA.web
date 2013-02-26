/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.ldap;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resource f�r die Strings....
 * 
 * @author philipp
 */
public class Messages {

	private static final String BUNDLE_NAME = "de.tarent.ldap.resources.ldap"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 *	Konstuktor 
	 */
	private Messages() {

	}
	/**
	 * Methode, die die externen Strings holt
	 * @param key
	 * @return Message
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
