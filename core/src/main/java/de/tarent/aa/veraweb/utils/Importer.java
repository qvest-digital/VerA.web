package de.tarent.aa.veraweb.utils;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;

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
     * @param digester           der {@link ImportDigester}, der die Datensätze weiter
     *                           verarbeitet.
     * @param transactionContext FIXME
     * @throws IOException   FIXME
     * @throws BeanException FIXME
     */
    void importAll(ImportDigester digester, TransactionContext transactionContext) throws IOException, BeanException;
}
