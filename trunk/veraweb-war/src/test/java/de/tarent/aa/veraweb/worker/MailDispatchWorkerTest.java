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

package de.tarent.aa.veraweb.worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonDoctypeFacade;

public class MailDispatchWorkerTest extends TestCase {
	MailDispatchWorker worker;

	@Override
    protected void setUp() throws Exception {
		super.setUp();
		worker = new MailDispatchWorker();
		
		Logger.getLogger("de.tarent").addAppender(new NullAppender());
	}

	@Override
    protected void tearDown() throws Exception {
		super.tearDown();
		worker = null;
	}

	protected Person getPerson() {
		Person person = new Person();
		for (Iterator it = person.keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			if ( key.contains( "name" ) )
				person.put(key, key);
		}
		return person;
	}

	protected PersonDoctypeFacade getPersonDoctypeFacade() {
		List doctypeA = new ArrayList();
		List doctypeB = new ArrayList();
		PersonDoctypeFacade facade = new PersonDoctypeFacade(getPerson(), doctypeA, doctypeB);
		facade.setFacade(null, null, true);
		return facade;
	}

	protected void assertMailText(String in, String expected) throws Exception {
		String text = worker.getMailText(in, getPersonDoctypeFacade());
		assertEquals(expected, text);
	}

	public void testEmptyMailText() throws Exception {
		assertMailText(null, "");
		assertMailText("", "");
	}

	public void testPlainMailText() throws Exception {
		assertMailText("Hallo", "Hallo");
		assertMailText("Hallo\nHallo", "Hallo\nHallo");
	}

	public void testSimpleMailText() throws Exception {
		assertMailText("<firstname>", "firstname_a_e1");
		assertMailText("ABC <firstname>", "ABC firstname_a_e1");
		assertMailText("<firstname> XYZ", "firstname_a_e1 XYZ");
		
		assertMailText("<firstname> <lastname>", "firstname_a_e1 lastname_a_e1");
		assertMailText("A<firstname> <lastname>Z", "Afirstname_a_e1 lastname_a_e1Z");
		assertMailText("A<firstname> <lastname>", "Afirstname_a_e1 lastname_a_e1");
		assertMailText("<firstname> <lastname>Z", "firstname_a_e1 lastname_a_e1Z");
	}

	public void testCrimpedMailText() throws Exception {
		assertMailText("<firstname><lastname>", "firstname_a_e1lastname_a_e1");
	}
}
