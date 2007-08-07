/*
 * $Id: AlternativeDestination.java,v 1.1 2007/06/20 11:56:52 christoph Exp $
 * 
 * Created on 08.11.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Diese Schnittstelle erlaubt {@link de.tarent.aa.veraweb.utils.Exporter}-Instanzen,
 * alternative Exportziele anzugeben, zum Beispiel im lokalen Dateisystem des Servers.
 * 
 * @author mikel
 */
public interface AlternativeDestination {
    /**
     * Diese Methode liefert ein alternatives Ziel in Form eines {@link OutputStream}s. 
     * 
     * @return ein {@link OutputStream} oder <code>null</code>.
     * @throws IOException
     */
    public OutputStream getAlternativeOutputStream() throws IOException;

    /**
     * Diese Methode führt einen Rollback auf das alternative Ziel durch,
     * löscht dabei z.B. neu erstellte Dateien wieder.
     */
    public void rollback();
}
