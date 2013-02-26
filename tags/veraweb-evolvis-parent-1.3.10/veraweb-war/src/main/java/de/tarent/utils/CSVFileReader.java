/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVFileReader is a class derived from CSVFile used to parse an existing CSV
 * file.
 * <p>
 * Adapted from a C++ original that is Copyright (C) 1999 Lucent Technologies.<br>
 * Excerpted from 'The Practice of Programming' by Brian Kernighan and Rob Pike.
 * <p>
 * Included by permission of the <a href="http://tpop.awl.com/">Addison-Wesley</a>
 * web site, which says: <cite>"You may use this code for any purpose, as long
 * as you leave the copyright notice and book citation attached"</cite>.
 * 
 * @author Brian Kernighan and Rob Pike (C++ original)
 * @author Ian F. Darwin (translation into Java and removal of I/O)
 * @author Ben Ballard (rewrote handleQuotedField to handle double quotes and
 *         for readability)
 * @author Fabrizio Fazzino (added integration with CSVFile, handling of
 *         variable textQualifier and Vector with explicit String type)
 * @author Mikel (removal of String template parameters)
 */
public class CSVFileReader extends CSVFile {
    //
    // Konstruktoren
    //
    /**
     * CSVFileReader constructor just need the name of the existing CSV file
     * that will be read.
     * 
     * @param inputFileName
     *            The name of the CSV file to be opened for reading
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     */
    public CSVFileReader(String inputFileName) throws FileNotFoundException {
        this(inputFileName, DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * CSVFileReader constructor just need a reader for CSV data that will be
     * read.
     * 
     * @param reader
     *            The Reader for reading CSV data
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     */
    public CSVFileReader(Reader reader) {
        this(reader, DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * CSVFileReader constructor just need the name and encoding of the existing
     * CSV file that will be read.
     * 
     * @param inputFileName
     *            The name of the CSV file to be opened for reading
     * @param charsetName
     *            The name of a supported charset
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public CSVFileReader(String inputFileName, String charsetName) throws FileNotFoundException,
            UnsupportedEncodingException {
        this(inputFileName, charsetName, DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * CSVFileReader constructor just need an InputStream and encoding for the
     * CSV data that will be read.
     * 
     * @param stream
     *            The InputStream for reading CSV data
     * @param charsetName
     *            The name of a supported charset
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public CSVFileReader(InputStream stream, String charsetName) throws UnsupportedEncodingException {
        this(stream, charsetName, DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * CSVFileReader constructor with a given field separator.
     * 
     * @param inputFileName
     *            The name of the CSV file to be opened for reading
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     */
    public CSVFileReader(String inputFileName, char sep) throws FileNotFoundException {
        this(inputFileName, sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFileReader constructor with a given field separator.
     * 
     * @param reader
     *            The Reader for reading CSV data
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     */
    public CSVFileReader(Reader reader, char sep) {
        this(reader, sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFileReader constructor with a given field separator.
     * 
     * @param inputFileName
     *            The name of the CSV file to be opened for reading
     * @param charsetName
     *            The name of a supported charset
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public CSVFileReader(String inputFileName, String charsetName, char sep) throws FileNotFoundException,
            UnsupportedEncodingException {
        this(inputFileName, charsetName, sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFileReader constructor with a given field separator.
     * 
     * @param stream
     *            The stream for reading CSV data
     * @param charsetName
     *            The name of a supported charset
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public CSVFileReader(InputStream stream, String charsetName, char sep) throws UnsupportedEncodingException {
        this(stream, charsetName, sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFileReader constructor with given field separator and text qualifier.
     * 
     * @param inputFileName
     *            The name of the CSV file to be opened for reading
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @param qual
     *            The text qualifier to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     */
    public CSVFileReader(String inputFileName, char sep, char qual) throws FileNotFoundException {
        this(new FileReader(inputFileName), sep, qual);
    }

    /**
     * CSVFileReader constructor with given field separator and text qualifier.
     * 
     * @param reader
     *            The Reader for reading CSV data
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @param qual
     *            The text qualifier to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     */
    public CSVFileReader(Reader reader, char sep, char qual) {
        super(sep, qual);
        in = new BufferedReader(reader);
    }

    /**
     * CSVFileReader constructor with given field separator and text qualifier.
     * 
     * @param inputFileName
     *            The name of the CSV file to be opened for reading
     * @param charsetName
     *            The name of a supported charset
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @param qual
     *            The text qualifier to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public CSVFileReader(String inputFileName, String charsetName, char sep, char qual) throws FileNotFoundException,
            UnsupportedEncodingException {
        this(new FileInputStream(inputFileName), charsetName, sep, qual);
    }

    /**
     * CSVFileReader constructor with given field separator and text qualifier.
     * 
     * @param stream
     *            The InputStream for reading CSV data
     * @param charsetName
     *            The name of a supported charset
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @param qual
     *            The text qualifier to be used; overwrites the default one
     * @throws FileNotFoundException
     *             If the file to be read does not exist
     * @throws UnsupportedEncodingException
     *             If the named charset is not supported
     */
    public CSVFileReader(InputStream stream, String charsetName, char sep, char qual) throws UnsupportedEncodingException {
        super(sep, qual);
        in = new BufferedReader(new InputStreamReader(stream, charsetName));
    }

    //
    // �ffentliche Methoden
    //
    /**
     * Split the next line of the input CSV file into fields.
     * <p>
     * This is currently the most important function of the package.
     * 
     * @return List of strings containing each field from the next line of the
     *         file
     * @throws IOException
     *             If an error occurs while reading the new line from the file
     */
    public List readFields() throws IOException {
        List fields = new ArrayList();
        StringBuffer sb = new StringBuffer();
        line = in.readLine();
        if (line == null)
            return null;

        if (line.length() == 0) {
            fields.add(line);
            return fields;
        }

        int i = 0;
        do {
            sb.setLength(0);
            if (i < line.length() && line.charAt(i) == textQualifier) {
                i = handleQuotedField(sb, ++i); // skip quote
            } else {
                i = handlePlainField(sb, i);
            }
            fields.add(sb.toString());
            i++;
        } while (i < line.length());

        return fields;
    }

    /**
     * Close the input CSV file.
     * 
     * @throws IOException
     *             If an error occurs while closing the file
     */
    public void close() throws IOException {
        in.close();
    }

    /**
     * Handles a quoted field.<br>
     * TODO: Sehr empfindlich gegen F�llzeichen zwischen schlie�endem Quote und Feld- oder Zeilenende 
     * 
     * @return index of next separator
     * @throws IOException 
     */
    protected int handleQuotedField(StringBuffer sb, int i) throws IOException {
        int j;
        int len = line.length();
        for (j = i; j < len; j++) {
            if (line.charAt(j) == textQualifier) {
                if (j + 1 == len) // end quotes at end of line
                    break; // done
                else if (line.charAt(j + 1) == textQualifier)
                    j++; // skip escape char
                else if (line.charAt(j + 1) == fieldSeparator) { // next delimiter
                    j++; // skip end quotes
                    break;
                }
            }
            sb.append(line.charAt(j)); // regular character
        }
        if (j >= len) {
            line = in.readLine();
            if (line == null) {
                line = String.valueOf(textQualifier);
                return 0;
            }
            sb.append('\n');
            return handleQuotedField(sb, 0);
        }
        return j;
    }

    /**
     * Handles an unquoted field.
     * 
     * @return index of next separator
     */
    protected int handlePlainField(StringBuffer sb, int i) {
        int j = line.indexOf(fieldSeparator, i); // look for separator
        if (j == -1) { // none found
            sb.append(line.substring(i));
            return line.length();
        } else {
            sb.append(line.substring(i, j));
            return j;
        }
    }

    //
    // gesch�tzte Member
    //
    /**
     * The buffered reader linked to the CSV file to be read.
     */
    protected final BufferedReader in;
    
    /**
     * Die aktuell gelesene Zeile.
     */
    String line = null;
}
