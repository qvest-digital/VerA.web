package de.tarent.ldap;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
	 * @param arg0 FIXME
	 */
	public LDAPException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0 FIXME
	 * @param arg1 FIXME
	 */
	public LDAPException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0 FIXME
	 */
	public LDAPException(Throwable arg0) {
		super(arg0);
	}

}
