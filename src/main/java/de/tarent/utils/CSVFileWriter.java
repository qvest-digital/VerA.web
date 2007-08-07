/*
 * $Id: CSVFileWriter.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * http://sourceforge.net/projects/csvfile
 */
package de.tarent.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

/**
 * CSVFileWriter is a class derived from CSVFile used to format some fields into
 * a new CSV file.
 * 
 * @author Fabrizio Fazzino
 */
public class CSVFileWriter extends CSVFile {
    //
    // Konstruktoren
    //
    /**
     * CSVFileWriter constructor just need the name of the CSV file that will be
     * written.
     * 
     * @param outputFileName
     *            The name of the CSV file to be opened for writing
     * @throws IOException
     *             If an error occurs while creating the file
     */
    public CSVFileWriter(String outputFileName) throws IOException {
        this(outputFileName, DEFAULT_FIELD_SEPARATOR);
    }

    /**
     * CSVFileWriter constructor with a given field separator.
     * 
     * @param outputFileName
     *            The name of the CSV file to be opened for writing
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @throws IOException
     *             If an error occurs while creating the file
     */
    public CSVFileWriter(String outputFileName, char sep) throws IOException {
        this(outputFileName, sep, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * CSVFileWriter constructor with given field separator and text qualifier.
     * 
     * @param outputFileName
     *            The name of the CSV file to be opened for writing
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @param qual
     *            The text qualifier to be used; overwrites the default one
     * @throws IOException
     *             If an error occurs while creating the file
     */
    public CSVFileWriter(String outputFileName, char sep, char qual) throws IOException {
        this(new FileWriter(outputFileName), sep, qual);
    }

    /**
     * CSVFileWriter constructor with given field separator and text qualifier.
     * 
     * @param writer
     *            The Writer to be opened for writing
     * @param sep
     *            The field separator to be used; overwrites the default one
     * @param qual
     *            The text qualifier to be used; overwrites the default one
     * @throws IOException
     *             If an error occurs while creating the file
     */
    public CSVFileWriter(Writer writer, char sep, char qual) {
        super(sep, qual);
        out = new PrintWriter(new BufferedWriter(writer));
    }

    //
    // �ffentliche Methoden
    //
    /**
     * Close the output CSV file.
     * 
     * @throws IOException
     *             If an error occurs while closing the file
     */
    public void close() {
        out.flush();
        out.close();
    }

    /**
     * Join the fields and write them as a new line to the CSV file.
     * 
     * @param fields
     *            The list of strings containing the fields
     */
    public void writeFields(List fields) {
        int n = fields.size();
        for (int i = 0; i < n; i++) {
            out.print(prepareField(fields.get(i)));
            if (i < (n - 1))
                out.print(fieldSeparator);
        }
        out.println();
    }
    
    //
    // gesch�tzte Hilfsmethoden
    //
    String prepareField(Object field) {
        String fieldString = (field != null) ? field.toString() : "";
        if (fieldString.indexOf(fieldSeparator) >=0 ||
            fieldString.indexOf('\n') >= 0 ||
            fieldString.indexOf('\r') >= 0 ||
            fieldString.indexOf(textQualifier) == 0) {
            return textQualifier + fieldString.replaceAll(String.valueOf(textQualifier), new String(new char[]{textQualifier, textQualifier})) + textQualifier;
        }
        return fieldString;
    }
    
    //
    // gesch�tzte Membervariablen
    //
    /** The print writer linked to the CSV file to be written. */
    protected final PrintWriter out;
}
