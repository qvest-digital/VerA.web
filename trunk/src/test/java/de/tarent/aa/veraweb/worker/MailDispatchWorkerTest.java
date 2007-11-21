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

	protected void setUp() throws Exception {
		super.setUp();
		worker = new MailDispatchWorker();
		
		Logger.getLogger("de.tarent").addAppender(new NullAppender());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		worker = null;
	}

	protected Person getPerson() {
		Person person = new Person();
		for (Iterator it = person.keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
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
