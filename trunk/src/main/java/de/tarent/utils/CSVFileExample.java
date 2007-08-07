/*
 * $Id: CSVFileExample.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * http://sourceforge.net/projects/csvfile
 */
package de.tarent.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Diese Klasse dient als Beispiel für die Klassen {@link CSVFile},
 * {@link CSVFileReader} und {@link CSVFileWriter}.
 */
public class CSVFileExample {
    /**
     * This method simply converts one CSV file into another one that makes use
     * of a different notation for field separator and text qualifier.
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        CSVFileReader in = new CSVFileReader("csv_in.txt", ';', '"');
        CSVFileWriter out = new CSVFileWriter("csv_out.txt", ',', '\'');

        List fields = in.readFields();
        while (fields != null) {
            out.writeFields(fields);
            fields = in.readFields();
        }

        in.close();
        out.close();
    }
}
