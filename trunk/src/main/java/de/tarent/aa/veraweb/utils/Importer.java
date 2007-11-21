/*
 * $Id: Importer.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 16.06.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;

/**
 * Diese Schnittstelle ist f�r jede grunds�tzliche Importvariante umzusetzen. 
 * 
 * @author mikel
 */
public interface Importer {
    /**
     * Diese Methode f�hrt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatens�tze und Zus�tze nacheinander dem �bergebenen 
     * {@link ImportDigester} �bergeben.
     * 
     * @param digester der {@link ImportDigester}, der die Datens�tze weiter
     *  verarbeitet.
     * @throws IOException 
     */
    public void importAll(ImportDigester digester) throws IOException;
}
