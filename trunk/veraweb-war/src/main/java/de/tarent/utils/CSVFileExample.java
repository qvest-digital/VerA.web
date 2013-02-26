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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Diese Klasse dient als Beispiel f�r die Klassen {@link CSVFile},
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
