/*
 * $Id: Importer.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 16.06.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;

/**
 * Diese Schnittstelle ist für jede grundsätzliche Importvariante umzusetzen. 
 * 
 * @author mikel
 */
public interface Importer {
    /**
     * Diese Methode führt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatensätze und Zusätze nacheinander dem übergebenen 
     * {@link ImportDigester} übergeben.
     * 
     * @param digester der {@link ImportDigester}, der die Datensätze weiter
     *  verarbeitet.
     * @throws IOException 
     */
    public void importAll(ImportDigester digester) throws IOException;
}
