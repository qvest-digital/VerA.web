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

/**
 *
 */
package de.tarent.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * A simple util for converting different formats of phone-numbers
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class PhoneNumberFormat
{
	/**
	 *
	 * Converts an input-string into a string containing only digits
	 *
	 * <p>
	 * A leading '+'-character is converted into two zeros
	 * </p>
	 * <h3>Example:</h3>
	 * <p>
	 * The input-string "+49 (2143) - 52453243" will be converted to "0049214352453243"
	 *
	 * @param number a phone number as string in a arbitrary format
	 * @return a string containing only digits
	 */
	public static String convertToPlainDigitSequence(String number)
	{
		String result = number;

		if(result.charAt(0) == '+')
			result = "00" + result.substring(1); //$NON-NLS-1$

		Pattern p = Pattern.compile("[0-9]+"); //$NON-NLS-1$
		Matcher m = p.matcher(result);

		result = ""; //$NON-NLS-1$

		while ( m.find() ) {
		    result += m.group();
		}

		return result;
	}
}
