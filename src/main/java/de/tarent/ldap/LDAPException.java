/* $Id: LDAPException.java,v 1.2 2005/08/17 09:58:57 kirchner Exp $
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

/**
 * Kapselt Exceptions aus dem LDAP
 * 
 * @author philipp
 */
public class LDAPException extends Exception {
	private static final long serialVersionUID = -13354750513745321L;

	/**
	 * 
	 */
	public LDAPException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public LDAPException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LDAPException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public LDAPException(Throwable arg0) {
		super(arg0);
	}

}
