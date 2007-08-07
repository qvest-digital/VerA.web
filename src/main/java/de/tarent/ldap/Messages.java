/* $Id: Messages.java,v 1.2 2004/04/30 09:22:56 philipp Exp $
 * 
 * Created on 02.05.2003 by philipp
 * 
 * tarent-contact, Plattform-Independent Webservice-Based Contact Management
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Philipp Kirchner.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.ldap;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resource für die Strings....
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
