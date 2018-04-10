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
