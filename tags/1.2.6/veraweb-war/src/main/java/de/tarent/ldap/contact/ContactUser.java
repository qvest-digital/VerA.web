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
package de.tarent.ldap.contact;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * @author kirchner
 */
public class ContactUser {

	private String	vorname		= null;

	private String	nachname	= null;

	private String	name		= null;

	private String	email		= null;

	private Boolean	admin		= new Boolean(false);

	/**
	 * @param user
	 *            Attribute des Users
	 */
	public ContactUser(Attributes user) {
		vorname = get_Attribute(user, "gn", "givenname");
		nachname = get_Attribute(user, "sn", "surname");
		name = get_Attribute(user, "cn", "commonname");
		email = get_Attribute(user, "mail");
		String admin_temp = get_Attribute(user, "adminflag");
		if ("TRUE".equals(admin_temp)) {
			admin = new Boolean(true);
		}
	}

	/**
	 * @param user
	 *            Attribute des Users
	 * @param name1
	 *            Name des Attributes
	 * @param name2
	 *            Name des Attributes
	 * @return Wert des Attributs
	 */
	private String get_Attribute(Attributes user, String name1, String name2) {
		String result = "";
		Attribute attr = user.get(name1);
		if (attr == null) {
			attr = user.get(name2);
		}
		if (attr != null) {
			try {
				result = (String) attr.get(0);
			} catch (NamingException e) {
				result = "";
			}
		}
		return result;
	}

	/**
	 * @param user
	 *            Attribute des Users
	 * @param name
	 *            Name des Attributes
	 * @return Wert des Attributs
	 */
	private String get_Attribute(Attributes user, String name) {
		String result = "";
		Attribute attr = user.get(name);
		if (attr != null) {
			try {
				result = (String) attr.get(0);
			} catch (NamingException e) {
				result = "";
			}
		}
		return result;
	}

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return Returns the nachname.
	 */
	public String getNachname() {
		return nachname;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the vorname.
	 */
	public String getVorname() {
		return vorname;
	}
	
	@Override
    public String toString(){
		return "Vorname: "+vorname+", Nachname: "+nachname+", Name: "+name+", EMail: "+email+", admin: "+admin.toString();
	}
	/**
	 * @return Returns the admin.
	 */
	public Boolean getAdmin() {
		return admin;
	}
}