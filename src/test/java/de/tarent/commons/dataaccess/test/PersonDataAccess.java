/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
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
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.dataaccess.test;

import javax.management.AttributeValueExp;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.StringValueExp;

import de.tarent.commons.dataaccess.DataAccess;

public class PersonDataAccess {
	private DataAccess dataAccess = new DataAccess();

	public Person getPersonById(String uid) {
		QueryExp expr = Query.eq(
				new AttributeValueExp("uid"),
				new StringValueExp(uid));
		
		return (Person) dataAccess.getEntry(Person.class, expr);
	}

	public Person getPersonByName(String lastname, String firstname) {
		QueryExp expr = Query.and(
				Query.eq(
						new AttributeValueExp("lastname"),
						new StringValueExp(lastname)),
				Query.eq(
						new AttributeValueExp("firstname"),
						new StringValueExp(firstname)));
		
		return (Person) dataAccess.getEntry(Person.class, expr);
	}
}
