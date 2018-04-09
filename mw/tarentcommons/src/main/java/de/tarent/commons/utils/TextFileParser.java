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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * Simple class for reading and writing text-files
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de), tarent GmbH Bonn
 *
 */
public class TextFileParser
{

	/**
	 * Reads in a text file
	 *
	 * @param pFile the file to be read
	 * @return a String representing the text of the textfile.
	 * @throws IOException if an IO-error occurs
	 */
	public static String readFile(File pFile) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(pFile));
		String text = "";
		while(reader.ready())
		{
			text += reader.readLine()+ "\r\n";
		}
		reader.close();
		return text;
	}

	/**
	 * Writes a String to a text-file
	 *
	 * @param pFile the file to be written
	 * @param pText the text to write into the textfile
	 * @throws IOException if an IO-Error occurs
	 */

	public static void writeTextToFile(File pFile, String pText) throws IOException
	{
		FileWriter writer = new FileWriter(pFile);
		writer.write(pText);
		writer.close();
	}
}
