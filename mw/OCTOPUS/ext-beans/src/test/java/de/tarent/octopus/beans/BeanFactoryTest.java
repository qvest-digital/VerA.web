/*
 * tarent-octopus beans extension,
 * tarent-octopus beans extension
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
 * interest in the program 'tarent-octopus beans extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: BeanFactoryTest.java,v 1.2 2007/06/11 13:24:36 christoph Exp $
 */
package de.tarent.octopus.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * This class test the functionality from the {@link BeanFactory}.
 *
 * @author Christoph Jerolimov
 */
public class BeanFactoryTest extends TestCase {
	/**
	 * Simple test of the {@link BeanFactory#transform(Object, Class)} method,
	 * verifiy same correct date inputs.
	 *
	 * @throws Exception
	 */
	public void testGetDateSimpleCorrect() throws Exception {
		assertDate("01.01.2000", "1.1.00");
		assertDate("01.01.2000", "1.1.2000");
	}

	/**
	 * Simple test of the {@link BeanFactory#transform(Object, Class)} method,
	 * verifiy same incorrect date inputs.
	 *
	 * @throws Exception
	 */
	public void testGetDateSimpleInvalid() throws Exception {
		try {
			assertDate("32.01.2000", "32.1.00");
			assertTrue(false);
		} catch (BeanException e) {
			assertTrue(true);
		}
		try {
			assertDate("32.01.2000", "32.1.2000");
			assertTrue(false);
		} catch (BeanException e) {
			assertTrue(true);
		}
	}

	/**
	 * Test the {@link BeanFactory#transform(Object, Class)} method with
	 * two digit year dates, in german.
	 *
	 * @throws Exception
	 */
	public void testGetDateTwoDigitsGerman() throws Exception {
		for (int i = 0; i <= 99; i++) {
			assertDate(
					"01.01." + (i < 10 ? "0" + i : "" + i),
					"01.01." + (i < 10 ? "0" + i : "" + i));
		}
	}

	/**
	 * Test the {@link BeanFactory#transform(Object, Class)} method with
	 * two digit year dates, in english.
	 *
	 * @throws Exception
	 */
	public void testGetDateTwoDigitsEnglish() throws Exception {
		for (int i = 0; i <= 99; i++) {
			assertDate(
					"01.01." + (i < 10 ? "0" + i : "" + i),
					"01-01-" + (i < 10 ? "0" + i : "" + i));
		}
	}

	/**
	 * Test the {@link BeanFactory#transform(Object, Class)} method with
	 * four digit year dates, in german.
	 *
	 * @throws Exception
	 */
	public void testGetDateFourDigitsGerman() throws Exception {
		for (int i = 1500; i <= 2500; i++) {
			assertDate("01.01." + i, "01.01." + i);
		}
	}

	/**
	 * Test the {@link BeanFactory#transform(Object, Class)} method with
	 * four digit year dates, in english.
	 *
	 * @throws Exception
	 */
	public void testGetDateFourDigitsEnglish() throws Exception {
		for (int i = 1500; i <= 2500; i++) {
			assertDate("01.01." + i, "01-01-" + i);
		}
	}

	/**
	 * Test some date input.
	 *
	 * @param expectedInput Expected date in the german format <code>d.M.y</code>.
	 * @param actualInput The "userinput" which will be verified.
	 * @throws BeanException
	 * @throws ParseException
	 */
	private void assertDate(String expectedInput, String actualInput) throws BeanException, ParseException {
		Date expected = new SimpleDateFormat("d.M.y").parse(expectedInput);
		Date actual = (Date)BeanFactory.transform(actualInput, Date.class);
		assertEquals(expected, actual);
	}
}
